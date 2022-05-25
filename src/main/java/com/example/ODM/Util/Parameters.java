package com.example.ODM.Util;

import com.example.ODM.Domain.ConfigParam.ConfigParamInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString

@XmlRootElement(name = "parameters")

@XmlAccessorType(XmlAccessType.FIELD)
public class Parameters {

    @XmlElement(name = "parameter")
    List<ConfigParamInfo> parameters=null ;



}
