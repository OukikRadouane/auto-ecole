package com.auto.series.Exception;

public class SerieNotFoundException extends RuntimeException {
    public SerieNotFoundException(String message){
        super(message);
    }
}
