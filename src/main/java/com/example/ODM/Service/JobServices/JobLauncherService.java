package com.example.ODM.Service.JobServices;


import com.example.ODM.Service.ShipmentFileService.ShipmentFileServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class JobLauncherService {

    @Autowired
    private Job job ;
    @Autowired
    private JobLauncher jobLauncher ;
    @Autowired
    private ShipmentFileServiceImpl shipmentFileService ;

    public void jobStart(String fileName) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        String meterType ;
        if(fileName.startsWith("AMM"))
            meterType = "ELEC" ;
        else
            meterType= "GAZ" ;
        shipmentFileService.setOdmProcessingStatus(fileName) ;
        String shipmentFileId = Integer.toString(shipmentFileService.getShipmentFileEntity(fileName).getId());
        Map<String, JobParameter> parameter = new HashMap<>() ;
        parameter.put("path",new JobParameter(fileName)) ;
        parameter.put("shipmentFileId",new JobParameter(shipmentFileId)) ;
        parameter.put("meterType" , new JobParameter(meterType)) ;
        parameter.put("time" , new JobParameter(System.currentTimeMillis())) ;
        JobParameters parameters = new JobParameters(parameter) ;
        JobExecution jobExecution = jobLauncher.run(job,parameters) ;
    }



}
