package com.auto.registration.exception;

public class InvalidDocumentException extends RuntimeException{
    public InvalidDocumentException(String message){
        super(message);
    }
}
