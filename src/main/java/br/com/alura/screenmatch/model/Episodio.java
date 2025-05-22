package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale; // Para DateTimeFormatter, se necessário

public class Episodio {

    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
        this.temporada = numeroTemporada;
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero(); // Número DO EPISÓDIO

        // Tratamento robusto para avaliação
        if (dadosEpisodio.avaliacao() != null && !dadosEpisodio.avaliacao().equalsIgnoreCase("N/A")) {
            try {
                this.avaliacao = Double.parseDouble(dadosEpisodio.avaliacao());
            } catch (NumberFormatException e) {
                System.err.println("AVISO: Avaliação '" + dadosEpisodio.avaliacao() +
                        "' inválida para o episódio T" + this.temporada + "E" + this.numeroEpisodio +
                        " (" + this.titulo + "). Definindo avaliação como null.");
                this.avaliacao = null;
            }
        } else {
            this.avaliacao = null; // Se for "N/A" ou null, define como null
        }

        // Tratamento para data de lançamento
        if (dadosEpisodio.dataLancamento() != null && !dadosEpisodio.dataLancamento().equalsIgnoreCase("N/A")) {
            try {
                // Tenta o formato ISO padrão YYYY-MM-DD primeiro
                this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
            } catch (DateTimeParseException e) {
                // Se falhar, tenta outros formatos comuns que a OMDB pode usar
                // Exemplo para "dd MMM yyyy" (ex: "17 Apr 2011")
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
                    this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento(), formatter);
                } catch (DateTimeParseException ex) {
                    // Se ainda assim falhar, ou se você souber de outros formatos, adicione mais try-catch
                    System.err.println("AVISO: Data de lançamento '" + dadosEpisodio.dataLancamento() +
                            "' inválida para o episódio T" + this.temporada + "E" + this.numeroEpisodio +
                            " (" + this.titulo + "). Formato não reconhecido. Definindo data como null.");
                    this.dataLancamento = null;
                }
            }
        } else {
            this.dataLancamento = null; // Se for "N/A" ou null
        }
    }

    // Getters
    public Integer getTemporada() { return temporada; }
    public String getTitulo() { return titulo; }
    public Integer getNumeroEpisodio() { return numeroEpisodio; }
    public Double getAvaliacao() { return avaliacao; }
    public LocalDate getDataLancamento() { return dataLancamento; }

    // Setters (opcional, depende se você precisa modificar após a criação)
    public void setTemporada(Integer temporada) { this.temporada = temporada; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setNumeroEpisodio(Integer numeroEpisodio) { this.numeroEpisodio = numeroEpisodio; }
    public void setAvaliacao(Double avaliacao) { this.avaliacao = avaliacao; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }

    @Override
    public String toString() {
        String avaliacaoStr = (avaliacao != null) ? String.format("%.1f", avaliacao) : "N/A";
        String dataLancamentoStr = (dataLancamento != null) ? dataLancamento.format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A";
        return String.format("T%02dE%02d - '%s' (Avaliação: %s, Lançamento: %s)",
                temporada, numeroEpisodio, titulo, avaliacaoStr, dataLancamentoStr);
    }
}