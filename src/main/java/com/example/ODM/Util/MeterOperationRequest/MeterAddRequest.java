package com.example.ODM.Util.MeterOperationRequest;
import com.example.ODM.Domain.Location.Location;
import com.example.ODM.Util.Parameters;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "devUaaOperation")
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
@Data
@NoArgsConstructor
public class MeterAddRequest extends OperationRequest{

    @XmlElement(name = "timeStamp")
    String timeStamp ;
    @XmlElement(name = "mRID")
    Long id;
    @XmlElement(name = "type")
    String type;
    @XmlElement(name = "armrRouter")
    String armrRouter;
    @XmlElement(name = "location")

    Location location;

    @XmlElement(name = "parameters")
    Parameters parameters ;

    public MeterAddRequest( String timeStamp, Long id, String type, String armrRouter, Location location, Parameters parameters) {
        super(MeterOperationRequestType.METER_ADD);
        this.timeStamp = timeStamp;
        this.id = id;
        this.type = type;
        this.armrRouter = armrRouter;
        this.location = location;
        this.parameters = parameters;
    }
}




