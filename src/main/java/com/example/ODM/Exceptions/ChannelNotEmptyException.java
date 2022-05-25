package com.example.ODM.Exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChannelNotEmptyException extends Exception{
    private int channel ;
    @Override
    public String getMessage() {
        return "Invalid Request -> Channel "+channel+" was not Empty " ;
    }
}
