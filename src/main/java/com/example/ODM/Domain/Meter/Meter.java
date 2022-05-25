package com.example.ODM.Domain.Meter;



import com.example.ODM.Util.MeterOperationRequest.MeterOperationRequestType;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@XmlRootElement(name="Meter")
@XmlAccessorType(XmlAccessType.FIELD)
@MappedSuperclass
public class Meter {
    @Id
    @SequenceGenerator(name = "meter_seq", sequenceName = "meter_seq", allocationSize = 1)
    @GeneratedValue(generator = "meter_seq")
    @Column(name = "id" , columnDefinition = "integer NOT NULL DEFAULT nextval('meter_seq')")
    private Long id ;

    @XmlElement(name = "deviceName" )
    protected String deviceName ;

    @XmlElement(name = "logicalDeviceName")
    protected String logicalDeviceName ;

    @XmlElement(name = "type")
    protected String type ;

    @XmlElement(name = "key")
    protected int key ;

    @XmlElement(name = "modelVersion")
    protected String modelVersion ;


    @XmlElement(name = "shipmentFileId")
    protected int shipmentFileId ;
    @XmlElement(name = "amrRouter")
    protected String amrRouter ;

    @XmlElement(name = "communicationMethod")
    protected String communicationMethod;

    @XmlElement(name = "hardwareVersion")
    protected String hardwareVersion;

    @XmlElement(name = "box")
    protected String box ;

    @XmlElement(name = "firmwareVersion")
    protected String firmwareVersion ;

    @Audited
    protected MeterStatus status ;
    @Audited
    protected boolean isConnected ;





    public Meter(String deviceName, String logicalDeviceName, String type, int key,  String modelVersion, int shipmentFileId, String amrRouter, String communicationMethod, String hardwareVersion, String box, String firmwareVersion) {
        this.deviceName = deviceName;
        this.logicalDeviceName = logicalDeviceName;
        this.type = type;
        this.key = key;
        this.modelVersion = modelVersion;
        this.shipmentFileId = shipmentFileId;
        this.amrRouter = amrRouter;
        this.communicationMethod = communicationMethod;
        this.hardwareVersion = hardwareVersion;
        this.box = box;
        this.firmwareVersion = firmwareVersion;
        this.isConnected = false ;
        this.status = MeterStatus.Provisionned;

    }




}
