package com.AchadosPerdidos.API.Application.Exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
