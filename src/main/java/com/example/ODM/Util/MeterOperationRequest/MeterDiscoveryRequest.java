package com.example.ODM.Util.MeterOperationRequest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@ToString
@XmlRootElement(name = "devdisc")
@XmlAccessorType(XmlAccessType.FIELD)

public class MeterDiscoveryRequest  extends OperationRequest{
    @XmlElement(name = "devId")
    Long id;
    @XmlElement(name = "time")
    String time;
    @XmlElement(name = "dcId")
    String amrRouter;
    @XmlElement(name = "devElecId")
    Long devElecId;

    @XmlElement(name = "channel")
    Integer channel;


    public MeterDiscoveryRequest( Long id, String time, String amrRouter, Long devElecId,  Integer channel) {
        super(MeterOperationRequestType.METER_DISCOVERY);
        this.id = id;
        this.time = time;
        this.amrRouter = amrRouter;
        this.devElecId = devElecId;
        this.channel = channel;
    }
}