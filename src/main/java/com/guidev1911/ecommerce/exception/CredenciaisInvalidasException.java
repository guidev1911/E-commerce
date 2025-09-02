package com.guidev1911.ecommerce.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Credenciais inválidas");
    }
}