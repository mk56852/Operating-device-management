package com.example.ODM.Service.MeterService;

import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;
import com.example.ODM.Domain.Meter.Meter;
import com.example.ODM.Domain.Meter.MeterStatus;
import com.example.ODM.Dto.MeterLiteDetailsDto;
import com.example.ODM.Util.MeterHistory;
import org.apache.camel.Exchange;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeterService {

/*              Meter Operation              */

    public void meterDiscoveryOperation(Exchange exchange) throws Exception;
    public void meterAddOperation(Exchange exchange) throws Exception ;
    public void meterActivationOperation(Exchange exchange) throws  Exception ;
    public void meterDeleteOperation(Exchange exchange) throws Exception ;
    public void meterUpdateOperation(Exchange exchange) throws  Exception ;
    public void meterScrapOperation(Exchange exchange) throws Exception;


/*             History                 */

    public List<MeterHistory> getHistory(Long MeterId) ;



/*             Data Operation              */

    public void deleteAllMeters(int shipmentFileId ) ;
    public MeterLiteDetailsDto getMeterDetails(Long id);
    public List<MeterLiteDetailsDto> getAllMetersDetails(Pageable pageable) ;
    public List<MeterLiteDetailsDto> getAllMetersDetailsWithStatus(MeterStatus status , Pageable pageable) ;
    public List<MeterLiteDetailsDto> getAllMeterByShipmentFileId(int shipmentFileId , Pageable pageable) ;

/*            Util Operation                 */

    public void insertConfigParamToMeter(Meter meter) ;
    public void updateConfigParam(Meter meter , List<ConfigParamInfo> configParamInfoList);
    public void setConfigParamToDefault(Meter meter) ;
    public void setConfigParamToNull(Meter meter) ;


}
