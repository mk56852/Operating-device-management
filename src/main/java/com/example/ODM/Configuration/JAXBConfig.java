package com.example.ODM.Configuration;

import com.example.ODM.Util.MeterOperationRequest.MeterActivitionRequest;
import com.example.ODM.Util.MeterOperationRequest.MeterAddRequest;
import com.example.ODM.Util.MeterOperationRequest.MeterDeleteRequest;
import com.example.ODM.Util.MeterOperationRequest.MeterDiscoveryRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Configuration
public class JAXBConfig {

    @Bean(name = "DiscoveryUnmarshaller")
    public Unmarshaller unmarshallerDiscovery() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MeterDiscoveryRequest.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller ;
    }

    @Bean(name = "AddUnmarshaller")
    public Unmarshaller unmarshallerAdd() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MeterAddRequest.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller ;
    }

    @Bean(name = "ActivationUnmarshaller")
    public Unmarshaller umarshallerActiation() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MeterActivitionRequest.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller ;
    }

    @Bean(name = "DeleteUnmarshaller")
    public Unmarshaller umarshallerDelete() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MeterDeleteRequest.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller ;
    }
}
