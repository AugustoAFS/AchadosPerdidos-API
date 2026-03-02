package com.AchadosPerdidos.API.Application.Exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
