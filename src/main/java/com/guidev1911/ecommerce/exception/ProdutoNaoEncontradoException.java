package com.guidev1911.ecommerce.exception;


import com.guidev1911.ecommerce.exception.global.EntidadeNaoEncontradaException;

public class ProdutoNaoEncontradoException extends EntidadeNaoEncontradaException {
    public ProdutoNaoEncontradoException(Long id) {
        super("Produto com ID " + id + " não encontrado.");
    }
}
