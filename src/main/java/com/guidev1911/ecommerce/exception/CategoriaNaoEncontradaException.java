package com.guidev1911.ecommerce.exception;

public class CategoriaNaoEncontradaException extends RuntimeException {
    public CategoriaNaoEncontradaException(Long id) {
        super("Categoria com ID " + id + " não encontrada.");
    }
}
