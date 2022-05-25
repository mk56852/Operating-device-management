package com.example.ODM.Service.DataQueryServices;

import com.example.ODM.Domain.Location.Location;
import com.example.ODM.Domain.Meter.MeterGaz;
import com.example.ODM.Domain.Meter.MeterStatus;
import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import com.example.ODM.Dto.DataQueryDto;
import com.example.ODM.Dto.LocationDto;
import com.example.ODM.Repository.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor

public class DataQueryServiceImpl implements DataQueryService {
    @Autowired
    private MeterElecRepository meterElecRepository ;
    @Autowired
    private MeterGazRepository meterGazRepository ;
    @Autowired
    private ShipmentFileRepository shipmentFileRepository ;
    @Autowired
    private LocationRepository locationRepository ;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper ;

    @Override
    public DataQueryDto getDashboardData() {
        DataQueryDto dataQueryDto = new DataQueryDto()  ;
        dataQueryDto.setProvisionnedMeter(meterGazRepository.countByStatus(MeterStatus.Provisionned)+meterElecRepository.countByStatus(MeterStatus.Provisionned));
        dataQueryDto.setDiscoveredMeter(meterElecRepository.countByStatus(MeterStatus.Dicovered) + meterGazRepository.countByStatus(MeterStatus.Dicovered));
        dataQueryDto.setActivatedMeter(meterElecRepository.countByStatus(MeterStatus.Activated) + meterGazRepository.countByStatus(MeterStatus.Activated));
        dataQueryDto.setInstalledMeter(meterElecRepository.countByStatus(MeterStatus.Installed) + meterGazRepository.countByStatus(MeterStatus.Installed));
        dataQueryDto.setRemovedMeter(meterElecRepository.countByStatus(MeterStatus.Removed) + meterGazRepository.countByStatus(MeterStatus.Removed));
        dataQueryDto.setActivationPendingMeter(meterElecRepository.countByStatus(MeterStatus.Activation_Pending) + meterGazRepository.countByStatus(MeterStatus.Activation_Pending));

        dataQueryDto.setMeterElecNumber(meterElecRepository.countAll());
        dataQueryDto.setMeterGazNumber(meterGazRepository.countAll());
        dataQueryDto.setShipmentFileNumber(shipmentFileRepository.countAll());
        dataQueryDto.setRejectedShipmentFile(shipmentFileRepository.countByStatus(ShipmentFileStatus.REJECTED));
        dataQueryDto.setUploadedShipmentFile(shipmentFileRepository.countByStatus(ShipmentFileStatus.UPLOADED));
        dataQueryDto.setProvisionnedShipmentFile(shipmentFileRepository.countByStatus(ShipmentFileStatus.PROVISIONNED));
        dataQueryDto.setKmsProcessingShipmentFile(shipmentFileRepository.countByStatus(ShipmentFileStatus.KMS_PROCESSING));
        dataQueryDto.setOdmProcessingShipmentFile(shipmentFileRepository.countByStatus(ShipmentFileStatus.ODM_PROCESSING));
        dataQueryDto.setImportAbortedShipmentFile(shipmentFileRepository.countByStatus(ShipmentFileStatus.IMPORT_ABORTED));
        dataQueryDto.setUserNumber(userRepository.countAll());



        return dataQueryDto ;
    }
}
