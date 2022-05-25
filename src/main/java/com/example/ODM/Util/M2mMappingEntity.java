package com.example.ODM.Util;


import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@XmlRootElement(name="Mapping")
@XmlAccessorType(XmlAccessType.FIELD)
public class M2mMappingEntity {

    @XmlElement(name = "deviceName")
    private String deviceName ;

    @XmlElement(name = "logicalDeviceName")
    private String logicalDeviceName ;
}
