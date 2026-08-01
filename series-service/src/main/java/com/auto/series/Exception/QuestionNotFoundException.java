package com.auto.series.Exception;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(String message){
        super(message);
    }
}
