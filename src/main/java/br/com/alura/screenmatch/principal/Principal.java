package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream; // Import para Stream.empty()

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=c6994f81"; // Sua chave aqui

    public void exibeMenu() {
        System.out.println("Digite o nome da série que deseja buscar: ");
        var nomeSerie = leitura.nextLine();
        var nomeSerieFormatado = nomeSerie.replace(" ", "+"); // Guardar nome formatado

        var json = consumo.obterDados(ENDERECO + nomeSerieFormatado + API_KEY);
        System.out.println("DEBUG: JSON da Série: " + json); // Log do JSON
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println("Dados da Série (DTO): " + dados);

        // Verifica se os dados básicos da série foram carregados
        if (dados == null || dados.totalTemporadas() == null) {
            System.out.println("Não foi possível obter os dados da série ou o total de temporadas.");
            return; // Sai do método se não há dados da série
        }

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerieFormatado + "&season=" + i + API_KEY); // URL da temporada CORRIGIDA
            System.out.println("DEBUG: JSON da Temporada " + i + ": " + json); // Log do JSON da temporada
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            if (dadosTemporada != null) { // Evita adicionar null à lista
                temporadas.add(dadosTemporada);
            } else {
                System.out.println("AVISO: Dados nulos retornados para temporada " + i);
            }
        }
        System.out.println("\n--- Dados das Temporadas (DTOs) ---");
        temporadas.forEach(System.out::println);

        System.out.println("\n--- Títulos dos Episódios (a partir dos DTOs) ---");
        // Loop for tradicional com verificações de nulidade
        for (DadosTemporada dt : temporadas) {
            if (dt != null && dt.episodios() != null) {
                for (DadosEpisodio de : dt.episodios()) {
                    if (de != null && de.titulo() != null) {
                        System.out.println("Temporada " + dt.numero() + " - " + de.titulo());
                    }
                }
            }
        }

        // Coletando todos os DadosEpisodio (DTOs) em uma lista
        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .filter(t -> t != null && t.episodios() != null) // Garante que temporada e lista de episódios não são nulos
                .flatMap(t -> t.episodios().stream())
                .filter(Objects::nonNull) // Garante que nenhum DadosEpisodio individual é nulo
                .collect(Collectors.toList());

        System.out.println("\n--- Top 5 Episódios (DTOs) ---");
        dadosEpisodios.stream()
                .filter(e -> e.avaliacao() != null && !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        // Convertendo DadosTemporada e DadosEpisodio para a classe de domínio Episodio
        System.out.println("\n--- Convertendo para Objetos Episodio ---");
        List<Episodio> episodios = temporadas.stream()
                .filter(dt -> dt != null && dt.episodios() != null && dt.numero() != null) // Filtra DadosTemporada nulos ou com campos essenciais nulos
                .flatMap(dadosTemporada -> { // dadosTemporada é um DadosTemporada
                    final String numeroTemporadaStr = dadosTemporada.numero(); // Número da temporada como String
                    try {
                        final Integer numeroTemporadaInt = Integer.parseInt(numeroTemporadaStr); // Converte para Integer

                        return dadosTemporada.episodios().stream() // Stream<DadosEpisodio>
                                .filter(Objects::nonNull) // Filtra DadosEpisodio nulos
                                .map(dadosEp -> new Episodio(numeroTemporadaInt, dadosEp)); // Cria o Episodio
                    } catch (NumberFormatException e) {
                        System.err.println("AVISO: Não foi possível converter o número da temporada '" + numeroTemporadaStr +
                                "' para inteiro. Episódios desta temporada não serão processados.");
                        return Stream.empty(); // Retorna um stream vazio se a conversão do número da temporada falhar
                    }
                })
                .filter(Objects::nonNull) // Garante que nenhum Episodio nulo (se o construtor por algum motivo retornasse null) entre na lista
                .collect(Collectors.toList());

        System.out.println("\n--- Lista de Objetos Episodio Convertidos ---");
        episodios.forEach(System.out::println); // Requer um bom toString() em Episodio

        // Exemplo de como usar a lista de objetos Episodio para o Top 5
        System.out.println("\n--- Top 5 Objetos Episodio (após conversão) ---");
        episodios.stream()
                .filter(ep -> ep.getAvaliacao() != null) // Avaliação já é Double, só checar se não é null
                .sorted(Comparator.comparing(Episodio::getAvaliacao).reversed()) // Ordena decrescente
                .limit(5)
                .forEach(System.out::println);
    }
}