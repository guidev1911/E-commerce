package com.guidev1911.ecommerce.exception;

public class EnderecoNaoPertenceAoUsuarioException extends RuntimeException {
    public EnderecoNaoPertenceAoUsuarioException(Long enderecoId, Long usuarioId) {
        super("Endereço com id " + enderecoId + " não pertence ao usuário com id " + usuarioId);
    }
}