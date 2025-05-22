package br.com.alura.screenmatch.service;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration; // Para timeouts (opcional, mas bom)

public class ConsumoApi {

    // Opcional: Definir um timeout para as requisições
    private static final int REQUEST_TIMEOUT_SECONDS = 10;

    public String obterDados(String endereco) {
        // 1. Criar o cliente HTTP
        // É recomendado criar uma instância e reutilizá-la se fizer muitas chamadas,
        // mas para este exemplo, criar um novo a cada vez é simples.
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS)) // Timeout de conexão
                .build();

        // 2. Criar a requisição HTTP
        HttpRequest request;
        try {
            System.out.println("INFO: Construindo URI para: " + endereco);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(endereco)) // Pode lançar IllegalArgumentException
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS)) // Timeout para esta requisição
                    .GET() // Explícito, embora seja o padrão
                    .build();
        } catch (IllegalArgumentException e) {
            System.err.println("ERRO: Endereço da API inválido fornecido: " + endereco);
            // Lançar uma exceção que o chamador possa tratar ou que pare a execução
            throw new RuntimeException("Endereço da API inválido: '" + endereco + "'. Causa: " + e.getMessage(), e);
        }

        // 3. Enviar a requisição e obter a resposta
        HttpResponse<String> response;
        try {
            System.out.println("INFO: Enviando requisição para: " + request.uri());
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.err.println("ERRO: Problema de IO ao tentar acessar a API em " + request.uri() + ": " + e.getMessage());
            throw new RuntimeException("Erro de comunicação (IO) com a API: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            System.err.println("ERRO: Requisição à API em " + request.uri() + " foi interrompida: " + e.getMessage());
            // Restaurar o status de interrupção da thread é uma boa prática
            Thread.currentThread().interrupt();
            throw new RuntimeException("Requisição à API interrompida: " + e.getMessage(), e);
        }

        // 4. Verificar o código de status da resposta
        int statusCode = response.statusCode();
        String responseBody = response.body(); // Obter o corpo para log ou retorno

        System.out.println("INFO: Status Code recebido da API: " + statusCode);
        // É bom logar o corpo APENAS em debug se ele for muito grande ou contiver dados sensíveis.
        // Para fins de depuração durante o desenvolvimento, pode ser útil:
        // System.out.println("DEBUG: Corpo da resposta da API: " + responseBody);

        if (statusCode >= 200 && statusCode < 300) {
            // Sucesso (códigos 2xx)
            if (responseBody == null || responseBody.trim().isEmpty()) {
                System.out.println("AVISO: API retornou sucesso ("+ statusCode +") mas com corpo vazio para: " + request.uri());
                // Decida como tratar: retornar null, string vazia, ou lançar exceção
                // Para o Screenmatch, um corpo vazio provavelmente significa que não há dados válidos.
                // Lançar uma exceção pode ser mais explícito do que retornar null e causar um NPE depois.
                throw new RuntimeException("API retornou sucesso ("+ statusCode +") mas com corpo vazio para: " + request.uri());
            }
            return responseBody; // Retorna o JSON como String
        } else {
            // Erro da API (códigos 3xx, 4xx, 5xx)
            String mensagemErro = "ERRO: Falha na requisição à API. Status: " + statusCode +
                    ". URI: " + request.uri() +
                    ". Resposta: " + (responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody); // Limita o tamanho do log do corpo
            System.err.println(mensagemErro);
            // Lançar uma exceção para indicar que a chamada falhou e por quê.
            // O chamador pode decidir como tratar essa exceção.
            throw new ApiException(mensagemErro, statusCode, responseBody);
        }
    }
}

// Opcional: Criar uma exceção customizada para erros da API
class ApiException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public ApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ApiException(String message, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
