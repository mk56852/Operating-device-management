package com.example.ODM.Util;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KmsResponseEntity {

    private String shipmentFileName ;
    private String typeResponse ;       /**      ACK   OR    GET_STATUS_RESPONSE      **/
    private String response ;           /**              Processed_Successfully  OR   Processed_With_Error               **/

    public KmsResponseEntity(String shipmentFileName, String typeResponse) {
        this.shipmentFileName = shipmentFileName;
        this.typeResponse = typeResponse;
    }
}
