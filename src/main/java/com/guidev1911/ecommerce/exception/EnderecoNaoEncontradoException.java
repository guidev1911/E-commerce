package com.guidev1911.ecommerce.exception;

import com.guidev1911.ecommerce.exception.global.EntidadeNaoEncontradaException;

public class EnderecoNaoEncontradoException extends EntidadeNaoEncontradaException {
    public EnderecoNaoEncontradoException(Long enderecoId) {
        super("Endereço não encontrado: " + enderecoId);
    }
}