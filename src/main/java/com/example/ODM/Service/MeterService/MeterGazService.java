package com.example.ODM.Service.MeterService;


import com.example.ODM.Domain.Meter.MeterGaz;
import com.example.ODM.Dto.MeterGazDto;
import com.example.ODM.Exceptions.ChannelNotEmptyException;
import com.example.ODM.Exceptions.InvalidMeterIdException;

import java.util.List;

public interface MeterGazService  {

    public MeterGaz updateAmrRouterAndChannel(MeterGaz meter , Long meterElecId , int channel) ;

    /*  Dto Operation */

    public List<MeterGazDto> getAllMeterGaz() ;
    public MeterGazDto getMeterGaz(Long id) ;


    /*   DB Operation   */

    public boolean checkAssocietedMeterElecInstalled(Long meterElecId) throws InvalidMeterIdException;
    public boolean checkEmptyChannel(int channel ,Long meterElecId) throws ChannelNotEmptyException;

}
