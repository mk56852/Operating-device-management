package com.example.ODM.Service.CamelRouteServies;
import com.example.ODM.Configuration.ShipmentFileConfiguration.ShipmentFileConfig;
import com.example.ODM.Service.JobServices.JobLauncherService;
import com.example.ODM.Service.ShipmentFileService.ShipmentFileServiceImpl;
import com.example.ODM.Util.KmsResponseEntity;
import com.example.ODM.Util.M2mResponseEntity;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component

public class ODMRoute extends RouteBuilder {
    @Autowired
    private ShipmentFileConfig config ;




    @Override
    public void configure() throws Exception {


        /**** From Personal Respo to the Upload Repo *****/


        from("file:"+config.getPersonalDir() + "?readLock=changed" )
                .bean(CamelRouteService.class,"saveFileToDataBaseWithDragAndDrop")
                .choice()
                    .when(simple("${exchangeProperty.FileAdded} == true"))
                        .to("file:"+config.getUploadDir()  )
                .otherwise()
                        .to("file:"+config.getRejectedFileDir() + "?doneFileName=done") ;



        /**** From the Upload Repo to the KMS Repo *****/



        from("file:"+config.getUploadDir()+ "?readLock=changed")

                .bean(CamelRouteService.class , "validateFileName")
                .choice()
                    .when(simple("${exchangeProperty.validFileName} == true"))
                        .to("direct:xsdValidation")
                    .otherwise()
                        .log("File Rejected -> invalid FileName")
                        .to("file:"+config.getRejectedFileDir() + "?doneFileName=done" )
                .endChoice() ;

        from("direct:xsdValidation")
               .bean(CamelRouteService.class,"validateFileFormat")
               .choice()
                    .when(simple("${exchangeProperty.xsdValidation} == true"))
                        .to("file:"+config.getKmsUploadDir() )
                        .bean(ShipmentFileServiceImpl.class,"setUploadedStatus(${headers.CamelFileNameConsumed})")

                    .otherwise()
                         .log("File Rejected -> invalid File Format")
                         .to("file:"+config.getRejectedFileDir() + "?doneFileName=done")
                         .bean(ShipmentFileServiceImpl.class,"RejectSF(${headers.CamelFileNameConsumed},Invalid File Format)")
                .endChoice() ;






        /**** From KMS Repo to Kafka Sending Topic *****/

       from("file:"+config.getKmsUploadDir()+ "?readLock=changed" )
               .bean(CamelRouteService.class,"sendMessageToKMS")
               .to("kafka:KMS_Sending_Topic") ;



       /****      SEND MAPPING FILE PATH TO M2M             ****/


       from("direct:M2mActivationDirect")
                .bean(CamelRouteService.class ,"sendMessageToM2M") ;





        /**** From Receiving Topic  ->  Data Process *****/



      from("kafka:KMS_Receiving_Topic")
                .unmarshal(new JacksonDataFormat(KmsResponseEntity.class))
                .choice()
                    .when(simple("${body.getTypeResponse()} == 'ACK'"))
                        .bean(ShipmentFileServiceImpl.class , "setKmsProcessingStatus(${body.getShipmentFileName()})")
                    .when(simple("${body.getTypeResponse()} ==  'GET_STATUS_RESPONSE'"))
                        .choice()
                            .when(simple("${body.getResponse()} == 'Processed_Successfully' "))
                                .bean(JobLauncherService.class , "jobStart(${body.getShipmentFileName()})")
                            .otherwise()
                                .bean(ShipmentFileServiceImpl.class , "RejectSF(${body.getShipmentFileName()} , KMS_PROCESSING_FAILED :: INVALID_KEY)")

                        .endChoice()
              .endChoice() ;



        from("kafka:M2M_Receiving_Topic")

                .unmarshal(new JacksonDataFormat(M2mResponseEntity.class))
                .choice()
                    .when(simple("${body.getKo()} == 0"))
                        .bean(ShipmentFileServiceImpl.class, "setProvisionnedStatus(${body.getShipmentFileName()})")
                    .otherwise()
                        .bean(ShipmentFileServiceImpl.class , "setImportAbortedStatus(${body.getShipmentFileName()} , ODM_PROCESSING_FAILED -> M2m_does_not_accept_${body.getKo()}_elements)")
                .endChoice();








    }
}






