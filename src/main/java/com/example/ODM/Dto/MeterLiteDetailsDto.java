package com.example.ODM.Dto;


import com.example.ODM.Domain.Meter.MeterStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeterLiteDetailsDto {

    private Long id ;
    private String deviceName ;
    private String type ;
    private MeterStatus status ;
    private String modelVersion ;
    private String communicationMethod;
}
