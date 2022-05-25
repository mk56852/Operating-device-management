package com.example.ODM.Domain.Location;


import com.example.ODM.Domain.Meter.MeterElec;
import com.example.ODM.Util.LocationId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@IdClass(LocationId.class)
@XmlRootElement(name = "location")
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {

    @XmlElement(name = "latitude")
    @Id
    private Long latitude ;
    @XmlElement(name = "longitude")
    @Id
    private Long longitude ;
    @Id
    private Long meterId ;

    @XmlElement(name = "region")
    private String region ;


    public Location(Long latitude, Long longitude, String region) {
        this.meterId = 0L ;
        this.latitude = latitude;
        this.longitude = longitude;
        this.region = region;
    }

    public Location(Long latitude, Long longitude) {
        this.meterId = 0L ;
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
