package com.example.ODM.Configuration.ShipmentFileConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "application.file")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Component

public class ShipmentFileConfig {

    private String uploadDir ;
    private String kmsUploadDir ;
    private String personalDir ;
    private String rejectedFileDir ;
    private String xsdFileDir ;
    private String odmProcessingFile ;
    private String mappingFileDirectory ;

}
