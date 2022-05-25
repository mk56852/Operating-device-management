package com.example.ODM.Util;
import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class UpdateShipmentFileRequest {

    private String name ;
    private ShipmentFileStatus status ;
}
