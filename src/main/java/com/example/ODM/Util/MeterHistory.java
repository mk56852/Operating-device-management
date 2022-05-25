package com.example.ODM.Util;

import com.example.ODM.Domain.Meter.MeterStatus;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class MeterHistory {

    private MeterStatus status ;
    private String modficationDate ;
    private boolean isConnected ;


}
