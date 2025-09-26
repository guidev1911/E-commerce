package com.guidev1911.ecommerce.exception;

import com.guidev1911.ecommerce.exception.global.EntidadeNaoEncontradaException;

public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException {

    public UsuarioNaoEncontradoException(String email) {
        super("Usuário não encontrado com e-mail: " + email);
    }

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }
}