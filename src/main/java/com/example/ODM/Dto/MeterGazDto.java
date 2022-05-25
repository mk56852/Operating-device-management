package com.example.ODM.Dto;


import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;
import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Domain.Meter.MeterStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeterGazDto {

    private Long id ;

    private String deviceName ;

    private String logicalDeviceName ;

    private String type ;

    private int key ;

    private String modelVersion ;

    private int shipmentFileId ;

    private String amrRouter ;

    private String communicationMethod;

    private String hardwareVersion;

    private String box ;

    private String firmwareVersion ;

    private MeterStatus status ;

    private boolean isConnected ;

    private Integer channel ;

    private Long meterElecId ;

    private LocationDto locationDto ;

    private List<ConfigParamDto> configParamInfoList  ;



}
