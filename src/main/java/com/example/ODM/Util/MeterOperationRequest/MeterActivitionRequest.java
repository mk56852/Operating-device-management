package com.example.ODM.Util.MeterOperationRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "devUaaOperation")
@XmlAccessorType(XmlAccessType.FIELD)

public class MeterActivitionRequest {

    @XmlElement(name = "meterId")
    private Long meterId ;
}
