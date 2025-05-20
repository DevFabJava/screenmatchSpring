package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;


/*@JsonAlias X @JasonProperty:
* @JsonAlias = coleta a informação e reescreve com nome trocado.
* @JasonProperty = coleta a informação e escreve com nome original.  */

public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totaTemporadas,
                         @JsonAlias("imdbRating") String avaliacao){
}
