package com.example.ODM.Domain.Meter;

import com.example.ODM.Exceptions.InvalidRequestException;
import com.example.ODM.Util.MeterOperationRequest.MeterOperationRequestType;
import com.example.ODM.Util.MeterOperationRequest.OperationRequest;

public interface MeterStatusHandler {

    public MeterStatus nextStatus(MeterOperationRequestType typeRequest, boolean condition) throws InvalidRequestException;
}
