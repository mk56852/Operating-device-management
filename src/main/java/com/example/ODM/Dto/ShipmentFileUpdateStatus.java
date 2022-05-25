package com.example.ODM.Dto;


import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShipmentFileUpdateStatus {
    private String name ;
    private ShipmentFileStatus status ;
}
