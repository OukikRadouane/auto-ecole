package com.auto.auth.Exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) { super(message); }
}
