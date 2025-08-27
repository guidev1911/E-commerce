package com.guidev1911.ecommerce.exception;


public class ProdutoNotFoundException extends RuntimeException {

    public ProdutoNotFoundException(Long id) {
        super("Produto não encontrado com id: " + id);
    }
}