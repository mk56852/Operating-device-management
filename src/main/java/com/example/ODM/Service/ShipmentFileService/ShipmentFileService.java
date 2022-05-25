package com.example.ODM.Service.ShipmentFileService;

import com.example.ODM.Domain.ShipmentFile.ShipmentFile;
import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import com.example.ODM.Dto.MeterLiteDetailsDto;
import com.example.ODM.Dto.ShipmentFileDTO;
import com.example.ODM.Util.ShipmentFileHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ShipmentFileService {

    /**    Add ShipmentFile **/

    public boolean addShipmentFile(ShipmentFile shipmentFile ,int userId) ;
    public boolean storeFile( MultipartFile file) ;
    public boolean storeProcess(ShipmentFile shipmentFile , MultipartFile file,int ownerId) ;


    /**    Get Method      **/
    public List<ShipmentFileDTO> getAllShipmentFile() ;
    public List<ShipmentFileDTO> getLastFiveShipmentFile() ;
    public ShipmentFile getShipmentFileEntity(int id) ;
    public ShipmentFile getShipmentFileEntity(String name);
    public ShipmentFileDTO getShipmentFile(int id) ;
    public List<ShipmentFileHistory> getHistory(ShipmentFile shipmentFile) ;





    /**     Update Method  **/

    public boolean updateSF(int id , ShipmentFileStatus status) ;
    public boolean updateSF(String name , ShipmentFileStatus status) ;
    public boolean UpdateSFWithReason(int id , ShipmentFileStatus status , String reason) ;
    public boolean setRejectedStatus(String name);
    public boolean RejectSF(String name,String reason) ;
    public boolean setUploadedStatus(String name );
    public boolean setKmsProcessingStatus(String name ) ;
    public boolean setOdmProcessingStatus(String name) ;
    public boolean setProvisionnedStatus(String name) ;
    public boolean setImportAbortedStatus(String name ,String reason) ;
    public boolean updateOwner(int id , String shipmentFileName) ;




    /***    Delete Method     **/
    public void delete(int id ) ;

    /*** Verification Method    ***/

    public boolean isRejected(ShipmentFile shipmentFile)  ;
    public boolean isNotDuplicated(String fileName) ;


    /** SHipmentFile meters Method    **/

    public void deleteAllShipmentFileMeters(String shipmentFileName) ;
    public List<MeterLiteDetailsDto> getAllShipmentFileMeters(int shipmentFileId , Pageable pageable) ;


}
