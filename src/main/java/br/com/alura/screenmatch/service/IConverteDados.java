package br.com.alura.screenmatch.service;

public interface IConverteDados {

    /*Método genérico para executarmos com diferentes tipos de dados*/

    <T> T obterDados(String json, Class<T> classe);

}
