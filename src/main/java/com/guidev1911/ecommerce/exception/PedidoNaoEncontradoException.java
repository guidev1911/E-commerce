package com.guidev1911.ecommerce.exception;

import com.guidev1911.ecommerce.exception.global.EntidadeNaoEncontradaException;

public class PedidoNaoEncontradoException extends EntidadeNaoEncontradaException {

    public PedidoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}