package com.guidev1911.ecommerce.exception;

import com.guidev1911.ecommerce.exception.global.EntidadeNaoEncontradaException;

public class CarrinhoNotFoundException extends EntidadeNaoEncontradaException {
    public CarrinhoNotFoundException(String mensagem) {
        super(mensagem);
    }
}
