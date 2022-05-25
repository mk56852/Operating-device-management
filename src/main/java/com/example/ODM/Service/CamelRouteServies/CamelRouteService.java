package com.example.ODM.Service.CamelRouteServies;


import org.apache.camel.Exchange;
import org.codehaus.jettison.json.JSONException;

public interface CamelRouteService {

    public void validateFileName(Exchange exchange) ;
    public void validateFileFormat(Exchange exchange) ;
    public void saveFileToDataBaseWithDragAndDrop(Exchange exchange) ;
    public void sendMessageToKMS(Exchange exchange) ;
    public void sendMessageToM2M(Exchange exchange) ;
    public void activateRouteToM2M(String fileName);


}
