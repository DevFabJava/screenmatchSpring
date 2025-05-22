package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(@JsonAlias("Title") String titulo,
                            @JsonAlias("Episode") Integer numero, // Número do episódio
                            @JsonAlias("imdbRating") String avaliacao,
                            @JsonAlias("Released") String dataLancamento) {} // Data como String
