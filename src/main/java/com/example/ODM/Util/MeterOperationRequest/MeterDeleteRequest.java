package com.example.ODM.Util.MeterOperationRequest;

import com.example.ODM.Util.Parameters;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "devUaaOperation")
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
@Data
@NoArgsConstructor

public class MeterDeleteRequest {

    @XmlElement(name = "meterId")
    private Long meterId ;
    @XmlElement(name = "parameters")
    private Parameters parameters ;
}
