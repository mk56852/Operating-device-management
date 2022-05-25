package com.example.ODM.Domain.Meter;

import com.example.ODM.Exceptions.InvalidRequestException;
import com.example.ODM.Util.MeterOperationRequest.MeterOperationRequestType;

import static com.example.ODM.Util.MeterOperationRequest.MeterOperationRequestType.*;

public enum MeterStatus implements MeterStatusHandler{


    Provisionned {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            if( typeRequest == METER_DISCOVERY)
                return MeterStatus.Dicovered ;
            if(typeRequest == MeterOperationRequestType.METER_ADD)
                return MeterStatus.Installed ;
           throw new InvalidRequestException() ;
        }
    },
    Installed  {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            if(typeRequest == METER_DISCOVERY)
                if(condition)
                    return MeterStatus.Activation_Pending;
                else
                    return MeterStatus.Reconcillation_Pending ;

            if(typeRequest == METER_DELETE)
                return  MeterStatus.Removed ;

            if(typeRequest == METER_SCRAP)
                return  MeterStatus.Scrapped ;

            throw new InvalidRequestException() ;

        }
    },
    Dicovered {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            if(typeRequest == METER_ADD)
                if(condition)
                    return MeterStatus.Activation_Pending ;
                else
                    return MeterStatus.Reconcillation_Pending ;

            throw new InvalidRequestException() ;
        }
    },
    Activation_Pending {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            if(typeRequest == METER_ACTIVATION)
                    if(condition)                   // Condition =>  Meter Response
                        return  MeterStatus.Activated ;
                    else
                        return MeterStatus.Activation_Failed ;
            throw new InvalidRequestException() ;
        }
    } ,
    Reconcillation_Pending {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            if(condition) {
                if(typeRequest == METER_UPDATE || typeRequest == METER_DISCOVERY)
                    return MeterStatus.Activation_Pending;
                else
                    throw new InvalidRequestException() ;
            }
            else
                return MeterStatus.Reconcillation_Pending ;
        }


    } ,
    Activation_Failed  {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {

            if(typeRequest == METER_DELETE)
                return MeterStatus.Removed ;
            if(typeRequest == METER_SCRAP)
                return MeterStatus.Scrapped ;
            throw new InvalidRequestException() ;

        }
    },
    Activated  {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            if(typeRequest == METER_DELETE)
                return MeterStatus.Removed ;
            if(typeRequest == METER_SCRAP)
                return MeterStatus.Scrapped ;
            throw new InvalidRequestException() ;
        }
    },
    Removed  {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            if( typeRequest == METER_DISCOVERY)
                return MeterStatus.Dicovered ;
            if(typeRequest == MeterOperationRequestType.METER_ADD)
                return MeterStatus.Installed ;
            if(typeRequest == METER_SCRAP)
                return MeterStatus.Scrapped ;
            throw new InvalidRequestException() ;
        }
    },
    Scrapped {
        @Override
        public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException {
            return null;
        }
    }
}
