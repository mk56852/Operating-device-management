package com.example.ODM.Controller;


import com.example.ODM.Domain.Meter.Meter;
import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Domain.ShipmentFile.ShipmentFile;
import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import com.example.ODM.Dto.ShipmentFileDTO;
import com.example.ODM.Dto.ShipmentFileUpdateStatus;
import com.example.ODM.Service.CamelRouteServies.CamelRouteService;
import com.example.ODM.Service.DataQueryServices.DataQueryService;
import com.example.ODM.Service.MeterService.*;
import com.example.ODM.Service.ShipmentFileService.ShipmentFileService;

import com.example.ODM.Service.UserService.UserService;
import com.example.ODM.Util.DeletingShipmentFileMetersRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@CrossOrigin(value = "http://localhost:3000" )
@RequestMapping(path = "/shipmentFile")
public class ShipmentFileController {
    @Autowired
    private ShipmentFileService shipmentFileService ;
    @Autowired
    private UserService userService ;
    @Autowired
    private CamelRouteService camelRouteService ;
    @Autowired
    private DataQueryService dataQueryService ;










    @GetMapping("/getHistory/{id}")
    public ResponseEntity get(@PathVariable int id)
    {
        ShipmentFile shipmentFile = shipmentFileService.getShipmentFileEntity(id) ;
        return ResponseEntity.ok(shipmentFileService.getHistory(shipmentFile)) ;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getSF(@PathVariable int id )
    {
        ShipmentFileDTO shipmentFileDTO = shipmentFileService.getShipmentFile(id) ;
        return  ResponseEntity.ok(shipmentFileDTO) ;
    }

    @GetMapping("/getAll")
    public ResponseEntity getAll()
    {
        return ResponseEntity.ok(shipmentFileService.getAllShipmentFile()) ;
    }


    @GetMapping("/getLastFive")
    public ResponseEntity getLastFive()
    {
        return ResponseEntity.ok(shipmentFileService.getLastFiveShipmentFile()) ;
    }




    @PostMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable int id){
        shipmentFileService.delete(id);
        return ResponseEntity.ok("File Deleted") ;
    }


    @PostMapping("/update")
    public ResponseEntity update(@RequestBody ShipmentFileUpdateStatus update)
    {
        if(update.getStatus() == ShipmentFileStatus.ODM_PROCESSING)
        {
            shipmentFileService.setOdmProcessingStatus(update.getName()) ;
        }
        else
            shipmentFileService.setRejectedStatus(update.getName()) ;
        return ResponseEntity.ok("") ;
    }

    @PostMapping("/resendMapping")
    public ResponseEntity resendMapping(@RequestBody ShipmentFileUpdateStatus shipmentFileRequest)
    {
        camelRouteService.activateRouteToM2M(shipmentFileRequest.getName());
        shipmentFileService.setOdmProcessingStatus(shipmentFileRequest.getName()) ;
        return ResponseEntity.ok("Route Activated") ;
    }

    @PostMapping("/deleteMeters")
    public ResponseEntity deletingShipmentFileMeters(@RequestBody DeletingShipmentFileMetersRequest request){
        shipmentFileService.deleteAllShipmentFileMeters(request.getShipmentFileName());
        return ResponseEntity.ok("") ;
    }

    @GetMapping("/getMeters/{id}")
    public ResponseEntity getMeters(@PathVariable int id , @RequestParam int page) {
       return ResponseEntity.ok(shipmentFileService.getAllShipmentFileMeters(id , PageRequest.of(page,6))) ;
    }


    @PostMapping("/add")
    public ResponseEntity addSF2(@RequestParam(name = "file") MultipartFile file , @RequestParam(name="name") String name, @RequestParam(name = "typeCompteur") String typeCompteur ,  @RequestParam(name="ownerId") int ownerId) {

        ShipmentFile shipmentFile = new ShipmentFile(name, typeCompteur);
        shipmentFile.setOwner(userService.findUserById(ownerId));
        if(shipmentFileService.storeProcess(shipmentFile,file,ownerId))
        {

            return ResponseEntity.ok("File Inserted") ;
        }
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(" File Already Exist !") ;

    }


    @GetMapping("/dashboard")
    public ResponseEntity getData() {
        return ResponseEntity.ok(dataQueryService.getDashboardData()) ;
    }








}
