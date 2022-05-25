package com.example.ODM.Service.CamelRouteServies;

import com.example.ODM.Configuration.ShipmentFileConfiguration.ShipmentFileConfig;
import com.example.ODM.Domain.ShipmentFile.ShipmentFile;
import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import com.example.ODM.Service.ShipmentFileService.ShipmentFileService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kafka.KafkaConstants;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
@AllArgsConstructor
@NoArgsConstructor

public class CamelRouteServiceImpl implements CamelRouteService {
    @Autowired
    ShipmentFileService shipmentFileService ;
    @Autowired
    ShipmentFileConfig shipmentFileConfig ;
    @Autowired
    CamelContext camelContext ;

    @Override
    public void validateFileName(Exchange exchange) {
        String fileName  = (String) exchange.getIn().getHeader("CamelFileNameConsumed") ;
        String pattern = "^(SAG|AMM)_.{10}[a-z A-Z 0-9]*.xml" ;
        boolean step1 = fileName.matches(pattern) ;
        boolean step2 = false;
        if(step1) {
            Date date = parseDate(fileName.substring(4, 14));
            Date min = parseDate("01-01-2020");
            Date max = parseDate("01-01-2022");

            if (date.compareTo(max) <= 0 && date.compareTo(min) >= 0)
                step2 = true;
        }

        if(step1&&step2)
            exchange.setProperty("validFileName",true);
        else
            exchange.setProperty("validFileName",false) ;

    }

    @Override
    public void validateFileFormat(Exchange exchange) {
        String schemaFile = this.getXsdDir() ;
        String filePath = shipmentFileConfig.getUploadDir() +"/"+ exchange.getIn().getHeader("CamelFileNameConsumed") ;

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Source file = new StreamSource(new FileReader(filePath)) ;
            Schema schema = schemaFactory.newSchema(new File(schemaFile));

            Validator validator = schema.newValidator();
            validator.validate(file);
            exchange.setProperty("xsdValidation",true);
        } catch (Exception a) {

            exchange.setProperty("xsdValidation",false);
        }

    }

    @Override
    public void saveFileToDataBaseWithDragAndDrop(Exchange exchange) {
        String fileName  = (String) exchange.getIn().getHeader("CamelFileNameConsumed") ;
        String typeCompteur = "Not Defined";
        if(fileName.substring(0,3).compareTo("SAG")==0 )
            typeCompteur ="GAZ" ;
        if(fileName.substring(0,3).compareTo("AMM")==0)
            typeCompteur="ELEC" ;

        ShipmentFile shipmentFile = new ShipmentFile(fileName,typeCompteur) ;
        if(shipmentFileService.isNotDuplicated(fileName))
        {
            shipmentFileService.addShipmentFile(shipmentFile,2) ;
            exchange.setProperty("FileAdded" , true);
        }
        else
        {
            if(shipmentFileService.isRejected(shipmentFile))
            {
                shipmentFileService.updateSF(fileName , ShipmentFileStatus.TO_TREAT) ;
                exchange.setProperty("FileAdded" , true);
            }
            else
            {
                exchange.setProperty("FileAdded" , false);
            }

        }

    }

    @Override
    public void sendMessageToKMS(Exchange exchange) {
        String request = "{\"Method\":\"Upload\" ,\n\t \"Body\" : {\"path\" : \""+shipmentFileConfig.getKmsUploadDir()+"/.camel/"+exchange.getIn().getHeader("CamelFileName")+"\"} }" ;
        try {
            exchange.getOut().setBody(new JSONObject(request));
        } catch (JSONException e)
        {}
        exchange.getOut().setHeader(KafkaConstants.KEY , exchange.getIn().getHeader("CamelFileName"));

    }


    @Override
    public void sendMessageToM2M(Exchange exchange) {
        ProducerTemplate producer =  exchange.getContext().createProducerTemplate();
        producer.sendBodyAndHeader("kafka:M2M_Sending_Topic" , exchange.getIn().getBody() , "kafka.KEY",exchange.getIn().getHeader("kafka.KEY"));
    }

    @Override
    public void activateRouteToM2M(String fileName) {
        ProducerTemplate producer = camelContext.createProducerTemplate();
        producer.sendBodyAndHeader("direct:M2mActivationDirect" , shipmentFileConfig.getMappingFileDirectory()+"/"+fileName , "kafka.KEY",fileName);

    }

    private   Date parseDate(String date) {
        try {
            return new SimpleDateFormat("MM-dd-yyyy").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private String getXsdDir()
    {

        return "C:/Users/dell/Desktop/PfeBackend/ODM/Files/ShipmentFile.xsd" ;
    }

}
