package com.example.ODM.Configuration.JobConfiguration;

import com.example.ODM.Service.CamelRouteServies.CamelRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobListener implements JobExecutionListener {

    @Autowired
    CamelRouteService camelRouteService ;

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }
    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            log.info("Job Terminer avec status Completed (^_^)");
            camelRouteService.activateRouteToM2M("Mapping_File_"+jobExecution.getJobParameters().getString("path"));
        }
        else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("Job Terminer avec status Failed (T_T) ");
        }
    }
}
