package com.guidev1911.ecommerce.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(Long produtoId, int quantidade, int estoqueDisponivel) {
        super("Quantidade solicitada (" + quantidade + ") para o produto ID " + produtoId +
                " excede o estoque disponível (" + estoqueDisponivel + ").");
    }
}