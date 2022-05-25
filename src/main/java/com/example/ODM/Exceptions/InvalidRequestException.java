package com.example.ODM.Exceptions;

public class InvalidRequestException extends Exception{

    @Override
    public String getMessage() {
        return "Invalid Request : Cannot apply this request to Meter ";
    }
}
