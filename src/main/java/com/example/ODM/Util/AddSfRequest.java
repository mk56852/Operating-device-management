package com.example.ODM.Util;



import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddSfRequest {
    private String name ;
    private String  typeCompteur ;
    private ShipmentFileStatus shipmentFileStatus ;
    private int ownerId ;



}
