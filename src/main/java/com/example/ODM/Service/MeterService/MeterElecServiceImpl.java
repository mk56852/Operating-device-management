package com.example.ODM.Service.MeterService;

import com.example.ODM.Domain.ConfigParam.ConfigParam;
import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;

import com.example.ODM.Domain.Location.Location;
import com.example.ODM.Domain.Meter.Meter;
import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Domain.Meter.MeterGaz;
import com.example.ODM.Domain.Meter.MeterStatus;
import com.example.ODM.Dto.ConfigParamDto;
import com.example.ODM.Dto.LocationDto;
import com.example.ODM.Dto.MeterElecDto;
import com.example.ODM.Dto.MeterLiteDetailsDto;
import com.example.ODM.Exceptions.InvalidMeterIdException;
import com.example.ODM.Exceptions.InvalidRequestException;
import com.example.ODM.Repository.*;
import com.example.ODM.Util.MeterHistory;
import com.example.ODM.Util.MeterOperationRequest.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service(value = "meterElecService")
@Transactional
@Slf4j
public class MeterElecServiceImpl implements MeterService , MeterElecService {
    @Autowired
    private MeterElecRepository meterElecRepository ;
    @Autowired
    private AuditReader auditReader ;

    @Qualifier(value = "DiscoveryUnmarshaller")
    @Autowired
    private Unmarshaller discoveryUnmarshaller ;

    @Qualifier(value = "AddUnmarshaller")
    @Autowired
    private Unmarshaller addUnmarshaller ;
    @Qualifier(value = "ActivationUnmarshaller")
    @Autowired
    private Unmarshaller activationUnmarshaller ;

    @Qualifier(value = "DeleteUnmarshaller")
    @Autowired
    private Unmarshaller deleteUnmarshaller ;



    @Autowired
    private ConfigParamInfoRepository configParamInfoRepository  ;
    @Autowired
    private ConfigParamRepository configParamRepository ;
    @Autowired
    private LocationRepository locationRepository ;
    @Autowired
    private MeterGazRepository meterGazRepository ;

    @Autowired
    private ModelMapper modelMapper ;


    @Override
    public List<MeterHistory> getHistory(Long id) {
        List<Object[]> result = (List<Object[]>) auditReader.createQuery().forRevisionsOfEntity (MeterElec.class,true,true )

                .add(AuditEntity.id().eq(id))
                .addProjection(AuditEntity.property("status"))
                .addProjection(AuditEntity.revisionProperty("timestamp"))
                .addProjection(AuditEntity.property("isConnected"))

                .getResultList();

        List<MeterHistory> reslt = new ArrayList<>() ;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        for (Object[] res:result) {
            reslt.add(new MeterHistory((MeterStatus)res[0],formatter.format(new Date((Long)res[1])),(boolean)res[2])) ;

        }

        return reslt ;
    }


    @Override
    public void deleteAllMeters(int shipmentFileId ){
        meterElecRepository.deleteByShipmentFileId((shipmentFileId));
    }

    @Override
    public MeterLiteDetailsDto getMeterDetails(Long id) {
        MeterElec meterElec = meterElecRepository.findById(id).get() ;
        MeterLiteDetailsDto dto = modelMapper.map(meterElec,MeterLiteDetailsDto.class) ;
        return dto ;
    }
    @Override
    public List<MeterLiteDetailsDto> getAllMetersDetailsWithStatus(MeterStatus status , Pageable pageable) {
        Page<MeterElec> meterElecList = meterElecRepository.findAllByStatus(status,pageable) ;
        List<MeterLiteDetailsDto> dtoList = new ArrayList<>() ;
        for (MeterElec meter: meterElecList
        ) {
            MeterLiteDetailsDto dto = modelMapper.map(meter,MeterLiteDetailsDto.class) ;
            dtoList.add(dto) ;

        }
        return dtoList ;
    }

    @Override
    public List<MeterLiteDetailsDto> getAllMetersDetails(Pageable pageable) {
        Page<MeterElec> meterElecList = meterElecRepository.findAll(pageable) ;
        List<MeterLiteDetailsDto> dtoList = new ArrayList<>() ;
        for (MeterElec meterElec: meterElecList
        ) {
            MeterLiteDetailsDto dto = modelMapper.map(meterElec,MeterLiteDetailsDto.class) ;
            dtoList.add(dto) ;

        }
        return dtoList ;
    }




    @Override
    public List<MeterLiteDetailsDto> getAllMeterByShipmentFileId(int shipmentFileId,Pageable pageable) {
        Page<MeterElec> meterElecList = meterElecRepository.findAllByShipmentFileId(shipmentFileId,pageable) ;
        List<MeterLiteDetailsDto> dtoList = new ArrayList<>() ;
        for (MeterElec meterElec: meterElecList
        ) {
            dtoList.add(modelMapper.map(meterElec,MeterLiteDetailsDto.class)) ;
        }
        return dtoList ;
    }



    @Override
    public void meterDiscoveryOperation(Exchange exchange ) throws Exception {

        MeterDiscoveryRequest request =(MeterDiscoveryRequest) discoveryUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
        request.setTypeRequest(MeterOperationRequestType.METER_DISCOVERY);
        MeterElec meterElec = (MeterElec) meterElecRepository.findById(request.getId()).get();

        MeterStatus currentStatus = meterElec.getStatus() ;
        meterElec = this.updateMeterTypeAndAmrRouter(meterElec) ;
        boolean condition = request.getAmrRouter().equals(meterElec.getAmrRouter()) ;
        meterElec.setStatus(meterElec.getStatus().nextStatus(request.getTypeRequest(),condition)) ;

        if(currentStatus == MeterStatus.Provisionned)
            this.insertConfigParamToMeter(meterElec);

        meterElec.setConnected(true);
        meterElecRepository.save(meterElec) ;
    }


    @Override
    public void meterAddOperation(Exchange exchange) throws Exception {
        MeterAddRequest request =(MeterAddRequest) addUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
        request.setTypeRequest(MeterOperationRequestType.METER_ADD);

        MeterElec meter = meterElecRepository.findById(request.getId()).get() ;
        MeterStatus currentStatus = meter.getStatus() ;
        boolean condition = false ;
        if(currentStatus == MeterStatus.Dicovered)
            condition = request.getType().equals(meter.getType()) ;

        meter.setStatus(meter.getStatus().nextStatus(request.getTypeRequest(),condition));

        Location location = request.getLocation() ;
        location.setMeterId(meter.getId());



        if(currentStatus == MeterStatus.Provisionned)
            insertConfigParamToMeter(meter);

        updateConfigParam(meter,request.getParameters().getParameters());
        meterElecRepository.save(meter) ;
        locationRepository.save(location) ;

    }

    @Override
    public void meterActivationOperation(Exchange exchange) throws Exception{
        MeterActivitionRequest request = (MeterActivitionRequest) activationUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class)))) ;
        MeterElec meter = meterElecRepository.findById(request.getMeterId()).get() ;
        Random random = new Random();
        boolean condition = random.nextBoolean();
        meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_ACTIVATION,condition));
        meterElecRepository.save(meter) ;
    }

    @Override
    public void meterDeleteOperation(Exchange exchange) throws Exception {
        MeterDeleteRequest request = (MeterDeleteRequest) deleteUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class)))) ;
        MeterElec meter = meterElecRepository.findById(request.getMeterId()).get() ;

        meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_DELETE, false));
        Location location = locationRepository.findByMeterId(meter.getId());
        locationRepository.delete(location);

        if (request.getParameters().getParameters() != null)
                updateConfigParam(meter, request.getParameters().getParameters());
        else
            setConfigParamToDefault(meter);
        meter.setConnected(false);
            meterElecRepository.save(meter);


            // Set Meter GAS Removed Also
        List<MeterGaz> meterGazList = meter.getMeterGazList() ;
        setMeterGazListRemoved(meterGazList);


    }

    @Override
    public void meterUpdateOperation(Exchange exchange) throws Exception {
        MeterAddRequest request =(MeterAddRequest) addUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
        MeterElec meter = meterElecRepository.findById(request.getId()).get() ;


        boolean condition = meter.getType().equals(request.getType()) ;
        if(condition) {
            meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_UPDATE, condition));
            updateConfigParam(meter, request.getParameters().getParameters());
            if (request.getLocation().getLatitude() != null) {
                Location location = request.getLocation();
                location.setMeterId(meter.getId());
                locationRepository.save(location);
            }
        }

        meterElecRepository.save(meter) ;
    }

    @Override
    public void meterScrapOperation(Exchange exchange) throws Exception {
        MeterActivitionRequest request = (MeterActivitionRequest) activationUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
        MeterElec meter = meterElecRepository.findById(request.getMeterId()).get();
        meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_SCRAP,false));
        setConfigParamToNull(meter);
        meterElecRepository.save(meter) ;
    }

    @Override
    public void insertConfigParamToMeter(Meter meter) {
        List<ConfigParam> globalConfigParamList = configParamRepository.findAllByScope("global") ;
        List<ConfigParam>  modelVersionConfigParamList = configParamRepository.findAllByModelVersion(meter.getModelVersion()) ;

        for (ConfigParam configParam : globalConfigParamList
             ) {
            configParamInfoRepository.save(new ConfigParamInfo(meter.getId(), configParam.getId(),configParam.getName(),configParam.getDefaultValue()));
        }

        for (ConfigParam configParam: modelVersionConfigParamList
             ) {
            configParamInfoRepository.save(new ConfigParamInfo(meter.getId(), configParam.getId(),configParam.getName(),configParam.getDefaultValue()));
        }

    }

    @Override
    public void updateConfigParam(Meter meter , List<ConfigParamInfo> configParamInfoList) {
        for (ConfigParamInfo configParamInfo:configParamInfoList)
        {
            ConfigParamInfo configParam = configParamInfoRepository.findByNameAndMeterId(configParamInfo.getName(), meter.getId());
            configParam.setValue(configParamInfo.getValue());
            configParamInfoRepository.save(configParam) ;

        }

    }



    @Override
    public MeterElec updateMeterTypeAndAmrRouter(MeterElec meter) {
        String communicationMethod= meter.getCommunicationMethod();
        if (communicationMethod.equals("2G/3G")){
            meter.setType("ELEC_CELLULAR");
            meter.setAmrRouter("VSDC");

        }
        if (communicationMethod.equals("PLC")){
            meter.setType("ELEC_PLC");
            meter.setAmrRouter("DC/GW");
        }

        return meter ;
    }

    @Override
    public List<MeterElecDto> getAllMeterElec() {
        List<MeterElec> meterElecList = meterElecRepository.findAllByOrderByIdDesc() ;
        List<MeterElecDto> result = new ArrayList<>() ;
        meterElecList.forEach
                (meter -> {
                        MeterElecDto meterElecDto = modelMapper.map(meter,MeterElecDto.class);
                        LocationDto locationDto = modelMapper.map(locationRepository.findByMeterId(meter.getId()) , LocationDto.class) ;
                        meterElecDto.setLocation(locationDto);
                        result.add(meterElecDto) ;
                }
                );
        return result;
    }

    @Override
    public MeterElecDto getMeterElec(Long id) {
        MeterElec meter = meterElecRepository.findById(id).get() ;
        MeterElecDto meterElecDto = modelMapper.map(meter,MeterElecDto.class) ;
        Location location = locationRepository.findByMeterId(meter.getId()) ;
        List<ConfigParamInfo> configParamInfos = configParamInfoRepository.findAllByMeterId(meter.getId()) ;
        List<ConfigParamDto> configParamDtoList = new ArrayList<>() ;
        if(!configParamInfos.isEmpty())
        {
            for (ConfigParamInfo info: configParamInfos
                 ) {
                configParamDtoList.add(modelMapper.map(info,ConfigParamDto.class)) ;
            }
        }
        meterElecDto.setConfigParamInfoList(configParamDtoList) ;
        LocationDto locationDto = new LocationDto() ;
        if(location != null)
            locationDto = modelMapper.map( location , LocationDto.class) ;
        meterElecDto.setLocation(locationDto);
        return meterElecDto ;
    }

    @Override
    public void setConfigParamToDefault(Meter meter) {
        List<ConfigParamInfo> configParamInfos =configParamInfoRepository.findAllByMeterId(meter.getId()) ;
        List<ConfigParamInfo> newConfigParamInfos = new ArrayList<>() ;
        for (ConfigParamInfo configParamInfo: configParamInfos

        ) {
            ConfigParam configParam = configParamRepository.findByName(configParamInfo.getName()) ;
            configParamInfo.setValue(configParam.getDefaultValue());
            newConfigParamInfos.add(configParamInfo) ;

        }
        configParamInfoRepository.saveAll(newConfigParamInfos) ;
    }

    @Override
    public void setConfigParamToNull(Meter meter) {
        List<ConfigParamInfo> configParamInfoList = configParamInfoRepository.findAllByMeterId(meter.getId()) ;
        List<ConfigParamInfo> newConfigParamInfoList = new ArrayList<>() ;
        for (ConfigParamInfo configParamInfo: configParamInfoList
        ) {
            configParamInfo.setValue(null);
            newConfigParamInfoList.add(configParamInfo) ;
        }
        configParamInfoRepository.saveAll(newConfigParamInfoList) ;
    }


    @Override
    public void setMeterGazListRemoved(List<MeterGaz> meterGazList) {
        for (MeterGaz meterGaz: meterGazList
             ) {
            meterGaz.setStatus(MeterStatus.Removed);
        }
        meterGazRepository.saveAll(meterGazList) ;
    }
}
