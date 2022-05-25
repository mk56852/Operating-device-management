package com.example.ODM.Service.MeterService;


import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Domain.Meter.MeterGaz;
import com.example.ODM.Dto.MeterElecDto;

import java.util.List;

public interface MeterElecService  {

    public MeterElec updateMeterTypeAndAmrRouter(MeterElec meter) ;

    /*  Dto Operation */
    public List<MeterElecDto> getAllMeterElec() ;
    public MeterElecDto getMeterElec(Long id) ;


    /*    DB Operation    */
    public void setMeterGazListRemoved(List<MeterGaz> meterGazList) ;


}
