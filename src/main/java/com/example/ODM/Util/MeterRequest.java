package com.example.ODM.Util;

import com.example.ODM.Domain.Meter.MeterStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeterRequest {
    MeterStatus status ;
}
