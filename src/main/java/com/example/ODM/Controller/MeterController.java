package com.example.ODM.Controller;

import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Domain.Meter.MeterStatus;
import com.example.ODM.Dto.MeterLiteDetailsDto;
import com.example.ODM.Repository.MeterElecRepository;
import com.example.ODM.Service.ConfigParamInfoServices.ConfigParamInfoService;
import com.example.ODM.Service.MeterService.MeterElecServiceImpl;
import com.example.ODM.Service.MeterService.MeterGazServiceImpl;
import com.example.ODM.Util.MeterRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@CrossOrigin(value = "http://localhost:3000" )
@RequestMapping(path = "/meter")
public class MeterController {


    @Autowired
    private MeterGazServiceImpl meterGazService ;
    @Autowired
    private MeterElecServiceImpl meterElecService ;
    @Autowired
    private ConfigParamInfoService configParamInfoService ;





    @GetMapping("/getAllMeterGaz")
    public ResponseEntity getAllMeterGaz(@RequestParam Optional<Integer> page) {
        List<MeterLiteDetailsDto> meterGazDtoList = meterGazService.getAllMetersDetails(PageRequest.of(page.orElse(0),6));
        return  ResponseEntity.ok(meterGazDtoList ) ;
    }

    @PostMapping("/getAllMeterGazWithStatus")
    public ResponseEntity getAllMeterGazWithStatus(@RequestBody MeterRequest status ,@RequestParam Optional<Integer> page ) {
        List<MeterLiteDetailsDto> meterGazDtoList = meterGazService.getAllMetersDetailsWithStatus(status.getStatus(),PageRequest.of(page.orElse(0),6));
        return  ResponseEntity.ok(meterGazDtoList ) ;
    }

    @PostMapping("/getAllWithStatus")
    public ResponseEntity getAllWithStatus(@RequestBody MeterRequest status , @RequestParam Optional<Integer> page) {
        List<MeterLiteDetailsDto> meterElecDtoList = meterElecService.getAllMetersDetailsWithStatus(status.getStatus(),PageRequest.of(page.orElse(0),3));

        List<MeterLiteDetailsDto> meterGazDtoList = meterGazService.getAllMetersDetailsWithStatus(status.getStatus(),PageRequest.of(page.orElse(0),6-meterElecDtoList.size()));

        meterElecDtoList.addAll(meterGazDtoList) ;
        return  ResponseEntity.ok(meterElecDtoList ) ;
    }

    @PostMapping("/getAllMeterElecWithStatus")
    public ResponseEntity getAllMeterElecWithStatus(@RequestBody MeterRequest status, @RequestParam Optional<Integer> page) {
        List<MeterLiteDetailsDto> meterElecDtoList = meterElecService.getAllMetersDetailsWithStatus(status.getStatus(),PageRequest.of(page.orElse(0),5));
        return  ResponseEntity.ok(meterElecDtoList ) ;
    }


    @GetMapping("/getAll")
    public ResponseEntity getAllMeter(@RequestParam Optional<Integer> page)
    {
        List<MeterLiteDetailsDto> meterGazDtoList = meterGazService.getAllMetersDetails(PageRequest.of(page.orElse(0),3));
        List<MeterLiteDetailsDto> meterElecDtoList = meterElecService.getAllMetersDetails(PageRequest.of(page.orElse(0),   6-meterGazDtoList.size()));
        meterElecDtoList.addAll(meterGazDtoList) ;
        return ResponseEntity.ok(meterElecDtoList) ;
    }

    @GetMapping("/getAllMeterElec")
    public ResponseEntity getAllMeterElec(@RequestParam Optional<Integer> page) {
        List<MeterLiteDetailsDto> meterDtoList = meterElecService.getAllMetersDetails(PageRequest.of(page.orElse(0),3));
        return  ResponseEntity.ok(meterDtoList) ;

    }


    @GetMapping("/getMeterGaz/{meterId}")
    public ResponseEntity getMeterGaz(@PathVariable Long meterId)
    {
        return ResponseEntity.ok(meterGazService.getMeterGaz(meterId)) ;

    }


    @GetMapping("/getMeterElec/{meterId}")
    public ResponseEntity getMeterElec(@PathVariable Long meterId)
    {
        return ResponseEntity.ok(meterElecService.getMeterElec(meterId)) ;

    }

    @GetMapping("getConfigParam/{meterId}")
    public ResponseEntity getMeterConfigParamInfoList(@PathVariable Long meterId)
    {

        return ResponseEntity.ok(configParamInfoService.getAllConfigParamOfMeter(meterId)) ;
    }

    @GetMapping("history")
    public ResponseEntity getHistory(@RequestParam(name = "id") Long id , @RequestParam(name = "type") String type ){
        if(type.equals("Elec"))
            return ResponseEntity.ok(meterElecService.getHistory(id));
        else
            return ResponseEntity.ok(meterGazService.getHistory(id));

    }


}
