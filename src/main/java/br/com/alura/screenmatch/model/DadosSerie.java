package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/*@JsonAlias X @JasonProperty:
* @JsonAlias = coleta a informação e reescreve com nome trocado.
* @JasonProperty = coleta a informação e escreve com nome original.  */

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao){
}
