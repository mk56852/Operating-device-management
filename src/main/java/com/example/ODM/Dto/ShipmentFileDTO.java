package com.example.ODM.Dto;

import com.example.ODM.Domain.ShipmentFile.ShipmentFileStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;



@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Service
public class ShipmentFileDTO {
    private int id ;
    private String name ;
    private ShipmentFileStatus shipmentFileStatus ;
    private String typeCompteur ;
    private UserDto owner ;
    private  String reason ;

}
