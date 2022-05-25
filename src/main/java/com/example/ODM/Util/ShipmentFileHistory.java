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
public class ShipmentFileHistory {

    private ShipmentFileStatus status ;
    private String modficationDate ;
    private String reason ;
    private String ownerName;

}
