package com.example.ODM.Service.ShipmentFileService;

import com.example.ODM.Configuration.ShipmentFileConfiguration.ShipmentFileConfig;
import com.example.ODM.Domain.ShipmentFile.ShipmentFile;
import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import com.example.ODM.Domain.User.User;
import com.example.ODM.Dto.MeterLiteDetailsDto;
import com.example.ODM.Dto.ShipmentFileDTO;
import com.example.ODM.Dto.ShipmentFileUpdateStatus;
import com.example.ODM.Repository.ShipmentFileRepository;
import com.example.ODM.Repository.UserRepository;
import com.example.ODM.Service.MeterService.MeterElecService;
import com.example.ODM.Service.MeterService.MeterService;
import com.example.ODM.Util.ShipmentFileHistory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ShipmentFileServiceImpl implements ShipmentFileService {

    private ShipmentFileRepository shipmentFileRepository ;
    private UserRepository userRepository ;
    private AuditReader auditReader ;
    private ModelMapper modelMapper  ;
    private ShipmentFileConfig shipmentFileConfig ;

    @Autowired
    private SimpMessageSendingOperations messageTemplate ;

    @Autowired
    @Qualifier(value = "meterElecService")
    private MeterService meterElecService ;

    @Autowired
    @Qualifier(value = "meterGazService")
    private MeterService meterGazService ;

    @Override
    public boolean addShipmentFile(ShipmentFile shipmentFile, int userId) {
        ShipmentFile shipmentFile1 = shipmentFileRepository.findByName(shipmentFile.getName()) ;
        if(shipmentFile1 == null)
        {
            User user = userRepository.findById(userId) ;
            shipmentFile.setOwner(user);
            shipmentFileRepository.save(shipmentFile) ;
            return true ;
        }
        return false ;
    }




    @Override
    public ShipmentFileDTO getShipmentFile(int id ) {
        ShipmentFile shipmentFile = shipmentFileRepository.findById(id) ;
        ShipmentFileDTO shipmentFileDTO = modelMapper.map(shipmentFile,ShipmentFileDTO.class) ;
        return shipmentFileDTO ;

    }

    @Override
    public List<ShipmentFileDTO> getAllShipmentFile() {
        List<ShipmentFile> shipmentFiles = shipmentFileRepository.findAllByOrderByIdDesc() ;
        List<ShipmentFileDTO> result = new ArrayList<>() ;
        shipmentFiles.forEach(file -> { result.add(modelMapper.map(file,ShipmentFileDTO.class));});
        return result;
    }

    @Override
    public List<ShipmentFileDTO> getLastFiveShipmentFile() {
        List<ShipmentFile> shipmentFiles = shipmentFileRepository.findTop5ByOrderByIdDesc() ;
        List<ShipmentFileDTO> result = new ArrayList<>() ;
        shipmentFiles.forEach(file -> { result.add(modelMapper.map(file,ShipmentFileDTO.class));});
        return result;
    }

    @Override
    public boolean updateSF(int id , ShipmentFileStatus status ) {
        ShipmentFile shipmentFile = shipmentFileRepository.findById(id) ;
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(status);
        shipmentFile.setReason(null);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }

    @Override
    public boolean updateSF(String name, ShipmentFileStatus status) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(status);
        shipmentFile.setReason(null);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }

    @Override
    public boolean RejectSF(String name ,String reason) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        ShipmentFileUpdateStatus status = new ShipmentFileUpdateStatus(name,ShipmentFileStatus.REJECTED);
        messageTemplate.convertAndSend("/topic/message",status);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(ShipmentFileStatus.REJECTED);
        shipmentFile.setReason(reason);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }

    @Override
    public boolean setUploadedStatus(String name ) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        ShipmentFileUpdateStatus status = new ShipmentFileUpdateStatus(name,ShipmentFileStatus.UPLOADED);
        messageTemplate.convertAndSend("/topic/message",status);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(ShipmentFileStatus.UPLOADED);
        shipmentFile.setReason(null);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }

    @Override
    public boolean setRejectedStatus(String name) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        ShipmentFileUpdateStatus status = new ShipmentFileUpdateStatus(name,ShipmentFileStatus.REJECTED);
        messageTemplate.convertAndSend("/topic/message",status);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(ShipmentFileStatus.REJECTED);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }


    @Override
    public boolean UpdateSFWithReason(int id, ShipmentFileStatus status, String reason) {
        ShipmentFile shipmentFile = shipmentFileRepository.findById(id) ;
        if(shipmentFile == null )
            return false ;
        shipmentFile.setStatus(status);
        shipmentFile.setReason(reason);
        shipmentFileRepository.save(shipmentFile) ;
        return true ;
    }

    @Override
    public boolean updateOwner(int id ,String shipmentFileName) {
        User user = userRepository.findById(id) ;

        if(user == null)
            return false ;
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(shipmentFileName) ;
        shipmentFile.setOwner(user);
        shipmentFileRepository.save(shipmentFile) ;
        return true ;
    }

    @Override
    public boolean setKmsProcessingStatus(String name) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        ShipmentFileUpdateStatus status = new ShipmentFileUpdateStatus(name,ShipmentFileStatus.KMS_PROCESSING);
        messageTemplate.convertAndSend("/topic/message",status);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(ShipmentFileStatus.KMS_PROCESSING);
        shipmentFile.setReason(null);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
}


    @Override
    public boolean setOdmProcessingStatus(String name) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        ShipmentFileUpdateStatus status = new ShipmentFileUpdateStatus(name,ShipmentFileStatus.ODM_PROCESSING);
        messageTemplate.convertAndSend("/topic/message",status);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(ShipmentFileStatus.ODM_PROCESSING);
        shipmentFile.setReason(null);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }


    @Override
    public boolean setProvisionnedStatus(String name) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        ShipmentFileUpdateStatus status = new ShipmentFileUpdateStatus(name,ShipmentFileStatus.PROVISIONNED);
        messageTemplate.convertAndSend("/topic/message",status);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(ShipmentFileStatus.PROVISIONNED);
        shipmentFile.setReason(null);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }


    @Override
    public boolean setImportAbortedStatus(String name , String reason) {
        ShipmentFile shipmentFile = shipmentFileRepository.findByName(name);
        ShipmentFileUpdateStatus status = new ShipmentFileUpdateStatus(name,ShipmentFileStatus.IMPORT_ABORTED);
        messageTemplate.convertAndSend("/topic/message",status);
        if(shipmentFile == null)
            return false ;
        shipmentFile.setStatus(ShipmentFileStatus.IMPORT_ABORTED);
        shipmentFile.setReason(reason);
        shipmentFileRepository.save(shipmentFile) ;
        return true  ;
    }


    @Override
    public boolean isNotDuplicated(String fileName) {
        ShipmentFile shipmentFile1 = this.getShipmentFileEntity(fileName) ;
        if(shipmentFile1 == null)
            return true ;
        return false ;
    }

    @Override
    public boolean isRejected(ShipmentFile shipmentFile){
        ShipmentFile shipmentFile1 = this.getShipmentFileEntity(shipmentFile.getName()) ;
        if (shipmentFile1.getStatus() == ShipmentFileStatus.REJECTED  )
            return true ;
        return  false ;
    }

    @Override
    public List<ShipmentFileHistory> getHistory(ShipmentFile shipmentFile) {

        List<Object[]> result = (List<Object[]>) auditReader.createQuery().forRevisionsOfEntity(ShipmentFile.class,true,true )

                .add(AuditEntity.id().eq(shipmentFile.getId()))
                .addProjection(AuditEntity.property("status"))
                .addProjection(AuditEntity.revisionProperty("timestamp"))
                .addProjection(AuditEntity.property("reason"))
                .addProjection(AuditEntity.property("owner_id"))
                .getResultList();

        List<ShipmentFileHistory> reslt = new ArrayList<>() ;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        for (Object[] res:result) {
            User user = userRepository.getById((int)res[3]) ;
            reslt.add(new ShipmentFileHistory((ShipmentFileStatus)res[0],formatter.format(new Date((Long)res[1])),(String)res[2],user.getUsername())) ;
        }

        return reslt ;
    }

    @Override
    public ShipmentFile getShipmentFileEntity(int id) {
        return shipmentFileRepository.findById(id) ;
    }

    @Override
    public ShipmentFile getShipmentFileEntity(String name) {
        return shipmentFileRepository.findByName(name) ;
    }







    @Override
    public boolean storeFile(MultipartFile file) {

        boolean result = false ;
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path targetLocation = this.fileStorageLocation().resolve(fileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            result = true ;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return result;

    }


    @Override
    public boolean storeProcess(ShipmentFile shipmentFile , MultipartFile file,int ownerId){
        if(isNotDuplicated(shipmentFile.getName()))
        {

            addShipmentFile(shipmentFile,ownerId);
            return storeFile(file) ;
        }
        else
        {

            if(isRejected(shipmentFile)) {
                ShipmentFile shipmentFile1 = getShipmentFileEntity(shipmentFile.getName()) ;
                updateOwner(ownerId,shipmentFile1.getName());
                updateSF(shipmentFile1.getId(),ShipmentFileStatus.TO_TREAT) ;
                return storeFile(file) ;
            }
            else
                return false ;


        }

    }

    @Override
    public void delete(int id) {
        ShipmentFile shipmentFile = shipmentFileRepository.getById(id) ;
        shipmentFileRepository.delete(shipmentFile);

    }


    @Override
    public void deleteAllShipmentFileMeters(String shipmentFileName) {
        ShipmentFile shipmentFile = this.getShipmentFileEntity(shipmentFileName);
        if(shipmentFile.getTypeCompteur().compareTo("ELEC") == 0 )
            meterElecService.deleteAllMeters(shipmentFile.getId());
        else
            meterGazService.deleteAllMeters(shipmentFile.getId());

    }



    @Override
    public List<MeterLiteDetailsDto> getAllShipmentFileMeters(int shipmentFileId , Pageable pageable) {
        ShipmentFile shipmentFile = this.getShipmentFileEntity(shipmentFileId);
        List<MeterLiteDetailsDto> meterDto = new ArrayList<>() ;
        if(shipmentFile.getTypeCompteur().compareTo("ELEC") == 0 )
            meterDto = meterElecService.getAllMeterByShipmentFileId(shipmentFileId , pageable);
        else
             meterDto = meterGazService.getAllMeterByShipmentFileId(shipmentFileId,pageable);

        return meterDto ;
    }

    private Path fileStorageLocation(){
        Path storageLocation = Paths.get(this.shipmentFileConfig.getUploadDir()).toAbsolutePath().normalize() ;
        return storageLocation ;
    }



}


