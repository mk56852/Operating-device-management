package com.example.ODM.Domain.ConfigParam;

import com.example.ODM.Domain.Meter.Meter;
import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Domain.Meter.MeterGaz;
import com.example.ODM.Util.ConfigParamInfoId;
import com.example.ODM.Util.LocationId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ConfigParamInfoId.class)
@XmlRootElement(name = "parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigParamInfo {

    @Id
    private Long meterId ;
    @Id
    private Long configParamId ;

    @XmlElement(name = "name")
    private String name ;
    @XmlElement(name = "value")
    private String value;





}
