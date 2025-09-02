package com.guidev1911.ecommerce.exception;

public class EmailJaRegistradoException extends RuntimeException {
    public EmailJaRegistradoException(String email) {
        super("Email já registrado: " + email);
    }
}