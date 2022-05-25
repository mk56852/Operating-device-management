package com.example.ODM.Exceptions;

import com.example.ODM.Domain.Meter.Meter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidMeterIdException extends  Exception{
    private Long id ;

    @Override
    public String getMessage() {
        return "Invalid Request -> Meter with id =  "+ id + " does not exist " ;
    }
}
