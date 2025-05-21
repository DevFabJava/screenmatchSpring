package br.com.alura.screenmatch.service;

public interface IConverteDados {

    /*Método genérico para executarmos com diferentes tipos de dados.
    T é um placeholder para um tipo real que será determinado quando
     o método for chamado.
    *O método recebe um json String (primeiro parâmetro) e
     um objeto do tipo Class (segundo parâmetro), sendo que
     o objeto Class informa ao método, qual é o tipo de objeto JAVA
     que ele deve criar a partir do json recebido.*/

    <T> T obterDados(String json, Class<T> classe);

}
