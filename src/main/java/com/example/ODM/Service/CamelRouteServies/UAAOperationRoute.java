package com.example.ODM.Service.CamelRouteServies;

import com.example.ODM.Configuration.ShipmentFileConfiguration.ShipmentFileConfig;
import com.example.ODM.Exceptions.ChannelNotEmptyException;
import com.example.ODM.Exceptions.InvalidMeterIdException;
import com.example.ODM.Exceptions.InvalidRequestException;
import com.example.ODM.Service.MeterService.MeterElecServiceImpl;
import com.example.ODM.Service.MeterService.MeterGazServiceImpl;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.consumer.KafkaManualCommit;
import org.apache.camel.impl.engine.FileStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.File;

@Component
public class UAAOperationRoute extends RouteBuilder {
    @Autowired
    private ShipmentFileConfig config ;


    @Bean(name = "fileStore", initMethod = "start", destroyMethod = "stop")
    public FileStateRepository fileStore() {
        FileStateRepository fileStateRepository =
                FileStateRepository.fileStateRepository(new File("OdmKafkaOffsetFileStore"));
        fileStateRepository.setMaxFileStoreSize(10485760); // 10MB max

        return fileStateRepository;

    }

    @Override
    public void configure() throws Exception {



        from("kafka:UaaOperationTopic?allowManualCommit=true&offsetRepository=#fileStore&maxPollIntervalMs=5555555")

               .doTry()

                .choice()
                .when(simple("${header.kafka.KEY} == 'ELEC_DISCOVERY'"))
                .to("validator:XSD/discoveryElec.xsd")
                .bean(MeterElecServiceImpl.class , "meterDiscoveryOperation")
                .log("Meter Elec Discovery Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'GAZ_DISCOVERY'"))
                .bean(MeterGazServiceImpl.class , "meterDiscoveryOperation")
                .to("validator:XSD/discoveryGaz.xsd")
                .log("Meter Gaz Discovery Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'GAZ_ADD'"))
                .to("validator:XSD/addElec.xsd")
                .bean(MeterGazServiceImpl.class,"meterAddOperation")
                .log("Meter Gaz Add Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'ELEC_ADD'"))
                .to("validator:XSD/addElec.xsd")
                .bean(MeterElecServiceImpl.class , "meterAddOperation")
                .log("Meter Elec Add Request")
                .endChoice()

                 .otherwise()
                .when(simple("${header.kafka.KEY} == 'ELEC_ACTIVATION'"))
                .to("validator:XSD/activation.xsd")
                .bean(MeterElecServiceImpl.class , "meterActivationOperation")
                .log("Meter Elec Activation Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'GAZ_ACTIVATION'"))
                .to("validator:XSD/activation.xsd")
                .bean(MeterGazServiceImpl.class , "meterActivationOperation")
                .log("Meter Gaz Activation Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'GAZ_REMOVE'"))
                .to("validator:XSD/delete.xsd")
                .bean(MeterGazServiceImpl.class , "meterDeleteOperation")
                .log("Meter Gaz Delete Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'ELEC_REMOVE'"))
                .to("validator:XSD/delete.xsd")
                .bean(MeterElecServiceImpl.class , "meterDeleteOperation")
                .log("Meter Elec Delete Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'ELEC_SCRAP'"))
                .to("validator:XSD/activation.xsd")
                .bean(MeterElecServiceImpl.class , "meterScrapOperation")
                .log("Meter Elec Scrap Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'GAZ_SCRAP'"))
                .to("validator:XSD/activation.xsd")
                .bean(MeterGazServiceImpl.class , "meterScrapOperation")
                .log("Meter Gaz Scrap Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'GAZ_UPDATE'"))
                .to("validator:XSD/addElec.xsd")
                .bean(MeterGazServiceImpl.class , "meterUpdateOperation")
                .log("Meter Gaz Update Request")
                .endChoice()

                .otherwise()
                .when(simple("${header.kafka.KEY} == 'ELEC_UPDATE'"))
                .to("validator:XSD/addElec.xsd")
                .bean(MeterElecServiceImpl.class , "meterUpdateOperation")
                .log("Meter Elec Update Request")
                .endChoice()



                .endDoTry()
                .doCatch(Exception.class )
                .log("${exception.message}")

                .doFinally()

                .process(exchange -> {
                    KafkaManualCommit manual = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                    if (manual != null)
                        manual.commit();
                })
                .end() ;









    }
}
