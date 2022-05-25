package com.example.ODM.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataQueryDto {
/**         Meters         **/
    private int meterGazNumber ;
    private int meterElecNumber ;
    private int scrapedMeter ;
    private int installedMeter ;
    private int provisionnedMeter ;
    private int activatedMeter ;
    private int removedMeter ;
    private int discoveredMeter ;
    private int activationPendingMeter ;

/**        Users        **/
    private int userNumber ;

/**      ShipmentFile    **/
    private  int shipmentFileNumber ;
    private int provisionnedShipmentFile ;
    private int uploadedShipmentFile ;
    private int kmsProcessingShipmentFile ;
    private int importAbortedShipmentFile ;
    private int odmProcessingShipmentFile ;
    private int rejectedShipmentFile ;
/**        Location        **/
    private List<LocationDto> mustThreeMeterLocation ;






}
