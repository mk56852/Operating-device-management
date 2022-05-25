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
import com.example.ODM.Dto.MeterGazDto;
import com.example.ODM.Dto.MeterLiteDetailsDto;
import com.example.ODM.Exceptions.ChannelNotEmptyException;
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

@Slf4j
@Service(value = "meterGazService")
@Transactional
public class MeterGazServiceImpl implements MeterService, MeterGazService  {
    @Autowired
    private MeterGazRepository meterGazRepository ;

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
    private  ModelMapper modelMapper  ;

    @Autowired
    private LocationRepository locationRepository ;




    @Override
    public void deleteAllMeters(int shipmentFileId ){
        meterGazRepository.deleteByShipmentFileId((shipmentFileId));
    }

    @Override
    public List<MeterHistory> getHistory(Long id) {
        List<Object[]> result = (List<Object[]>) auditReader.createQuery().forRevisionsOfEntity (MeterGaz.class,true,true )

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
    public MeterLiteDetailsDto getMeterDetails(Long id) {
        MeterGaz meter = meterGazRepository.findById(id).get() ;
        MeterLiteDetailsDto dto = modelMapper.map(meter,MeterLiteDetailsDto.class) ;
        return dto ;
    }

    @Override
    public List<MeterLiteDetailsDto> getAllMetersDetailsWithStatus(MeterStatus status, Pageable pageable) {
        Page<MeterGaz> meterGazList = meterGazRepository.findAllByStatus(status , pageable) ;
        List<MeterLiteDetailsDto> dtoList = new ArrayList<>() ;
        for (MeterGaz meter: meterGazList
        ) {
            MeterLiteDetailsDto dto = modelMapper.map(meter,MeterLiteDetailsDto.class) ;
            dtoList.add(dto) ;

        }
        return dtoList ;
    }

    @Override
    public List<MeterLiteDetailsDto> getAllMetersDetails(Pageable pageable) {
        Page<MeterGaz> meterGazList = meterGazRepository.findAll(pageable) ;
        List<MeterLiteDetailsDto> dtoList = new ArrayList<>() ;
        for (MeterGaz meter: meterGazList
        ) {
            MeterLiteDetailsDto dto = modelMapper.map(meter,MeterLiteDetailsDto.class) ;
            dtoList.add(dto) ;
        }
        return dtoList ;
    }



    @Override
    public List<MeterLiteDetailsDto> getAllMeterByShipmentFileId(int shipmentFileId,Pageable pageable) {
        Page<MeterGaz> meterList = meterGazRepository.findAllByShipmentFileId(shipmentFileId,pageable) ;
        List<MeterLiteDetailsDto> dtoList = new ArrayList<>() ;
        for (MeterGaz meter: meterList
        ) {
            dtoList.add(modelMapper.map(meter,MeterLiteDetailsDto.class)) ;
        }
        return dtoList ;
    }



    @Override
        public void meterDiscoveryOperation(Exchange exchange ) throws Exception {

            MeterDiscoveryRequest request =(MeterDiscoveryRequest) discoveryUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
            MeterGaz meterGaz = (MeterGaz) meterGazRepository.findById(request.getId()).orElseThrow( () -> new InvalidMeterIdException(request.getId()));

            if(checkAssocietedMeterElecInstalled(request.getDevElecId()) && checkEmptyChannel(request.getChannel(), request.getDevElecId()));
            {
                meterGaz = updateAmrRouterAndChannel(meterGaz, request.getDevElecId(), request.getChannel());
                boolean condition = request.getDevElecId().toString().equals(meterGaz.getAmrRouter());
                MeterStatus currentStatus = meterGaz.getStatus();
                meterGaz.setStatus(meterGaz.getStatus().nextStatus(MeterOperationRequestType.METER_DISCOVERY, condition));
                meterGaz.setMeterElecId(meterElecRepository.findById(request.getDevElecId()).orElseThrow(() -> new InvalidMeterIdException(request.getDevElecId())));

                if (currentStatus == MeterStatus.Provisionned)
                    this.insertConfigParamToMeter(meterGaz);

                meterGaz.setConnected(true);
                meterGazRepository.save(meterGaz);
            }
        }




    @Override
    public void meterAddOperation(Exchange exchange) throws Exception {

        MeterAddRequest request =(MeterAddRequest) addUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
        MeterGaz meter = meterGazRepository.findById(request.getId()).orElseThrow( () -> new InvalidMeterIdException(request.getId())) ;


        boolean condition = false ;
        MeterStatus currentStatus = meter.getStatus() ;
        if(currentStatus == MeterStatus.Dicovered)
            condition = request.getType().equals(meter.getType()) ;

        meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_ADD,condition));


        Location location = request.getLocation() ;
        location.setMeterId(meter.getId());
        locationRepository.save(location) ;


        if(currentStatus == MeterStatus.Provisionned)
            insertConfigParamToMeter(meter);

        updateConfigParam(meter,request.getParameters().getParameters());
        meterGazRepository.save(meter) ;

    }

    @Override
    public void meterActivationOperation(Exchange exchange) throws Exception {
        MeterActivitionRequest request = (MeterActivitionRequest) activationUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class)))) ;
        MeterGaz meter = meterGazRepository.findById(request.getMeterId()).orElseThrow( () -> new InvalidMeterIdException(request.getMeterId())) ;
        Random random = new Random();
        boolean condition = random.nextBoolean();
        meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_ACTIVATION,condition));
        meterGazRepository.save(meter) ;
    }


    @Override
    public void meterDeleteOperation(Exchange exchange) throws Exception {

        MeterDeleteRequest request = (MeterDeleteRequest) deleteUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class)))) ;
        MeterGaz meter = meterGazRepository.findById(request.getMeterId()).orElseThrow( () -> new InvalidMeterIdException(request.getMeterId())) ;
        meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_DELETE, false));

        Location location = locationRepository.findByMeterId(meter.getId());
        locationRepository.delete(location);
        if (request.getParameters().getParameters() != null)
            updateConfigParam(meter, request.getParameters().getParameters());
        else
            setConfigParamToDefault(meter);

        meter.setChannel(null);
        meter.setMeterElecId(null);
        meter.setConnected(false);
        meterGazRepository.save(meter);

    }

    @Override
    public void meterScrapOperation(Exchange exchange) throws Exception {
        MeterActivitionRequest request = (MeterActivitionRequest) activationUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
        MeterGaz meter = meterGazRepository.findById(request.getMeterId()).orElseThrow( () -> new InvalidMeterIdException(request.getMeterId()));
        meter.setStatus(meter.getStatus().nextStatus(MeterOperationRequestType.METER_SCRAP,false));
        setConfigParamToNull(meter);
        meter.setChannel(null);
        meter.setMeterElecId(null);
        meterGazRepository.save(meter) ;
    }


    @Override
    public void meterUpdateOperation(Exchange exchange) throws Exception {
        MeterAddRequest request =(MeterAddRequest) addUnmarshaller.unmarshal(new StreamSource(new StringReader(exchange.getIn().getBody(String.class))));
        MeterGaz meter = meterGazRepository.findById(request.getId()).orElseThrow( () -> new InvalidMeterIdException(request.getId())) ;


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

        meterGazRepository.save(meter) ;

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
            ConfigParamInfo configParam = configParamInfoRepository.findByNameAndMeterId(configParamInfo.getName(),meter.getId());
            configParam.setValue(configParamInfo.getValue());
            configParamInfoRepository.save(configParam) ;

        }

    }

    @Override
    public MeterGaz updateAmrRouterAndChannel(MeterGaz meter, Long meterElecId, int channel) {
        meter.setAmrRouter(meterElecId.toString());
        meter.setChannel(channel);
        meter.setType("GAS_MBUS");
        return meter ;
    }


    /*****      Data Opreration        ******/


    @Override
    public List<MeterGazDto> getAllMeterGaz() {
        List<MeterGaz> meterGazList = meterGazRepository.findAllByOrderByIdDesc() ;
        List<MeterGazDto> result = new ArrayList<>() ;
        meterGazList.forEach(meter -> { result.add(modelMapper.map(meter,MeterGazDto.class));});
        return result;
    }

    @Override
    public MeterGazDto getMeterGaz(Long id) {
        MeterGaz meter = meterGazRepository.findById(id).get() ;
        MeterGazDto meterGazDto = modelMapper.map(meter,MeterGazDto.class) ;
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
        meterGazDto.setConfigParamInfoList(configParamDtoList) ;
        LocationDto locationDto = new LocationDto() ;
        if(location != null)
            locationDto = modelMapper.map( location , LocationDto.class) ;
        meterGazDto.setLocationDto(locationDto); ;
        return meterGazDto ;
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
    public boolean checkAssocietedMeterElecInstalled(Long meterElecId) throws InvalidMeterIdException {
        MeterElec meterElec = meterElecRepository.findById(meterElecId).orElseThrow(() -> new InvalidMeterIdException(meterElecId) ) ;
        return
                (meterElec.getStatus() != MeterStatus.Provisionned) &&
                        (meterElec.getStatus() != MeterStatus.Dicovered) &&
                        (meterElec.getStatus() != MeterStatus.Scrapped) &&
                        (meterElec.getStatus() != MeterStatus.Removed) ;
    }

    @Override
    public boolean checkEmptyChannel(int channel, Long meterElecId) throws ChannelNotEmptyException {
        MeterElec meterElec = meterElecRepository.findById(meterElecId).get();
        List<MeterGaz> meterGazList = meterElec.getMeterGazList() ;
        for (MeterGaz meterGaz: meterGazList
             ) {
            if(channel == meterGaz.getChannel())
                throw new ChannelNotEmptyException(channel) ;
        }
        return true ;
    }
}


