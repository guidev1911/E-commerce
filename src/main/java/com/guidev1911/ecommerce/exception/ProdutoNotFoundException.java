package com.guidev1911.ecommerce.exception;


import com.guidev1911.ecommerce.exception.global.EntidadeNaoEncontradaException;

public class ProdutoNotFoundException extends EntidadeNaoEncontradaException {
    public ProdutoNotFoundException(Long id) {
        super("Produto com ID " + id + " não encontrado.");
    }
}
