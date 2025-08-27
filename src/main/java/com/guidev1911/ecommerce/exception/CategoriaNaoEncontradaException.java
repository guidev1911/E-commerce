package com.guidev1911.ecommerce.exception;

import com.guidev1911.ecommerce.exception.global.EntidadeNaoEncontradaException;

public class CategoriaNaoEncontradaException extends EntidadeNaoEncontradaException {
    public CategoriaNaoEncontradaException(Long id) {
        super("Categoria com ID " + id + " não encontrada.");
    }
}
