package com.example.ODM.Util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ConfigParamInfoId implements Serializable  {

    private Long configParamId ;
    private Long meterId ;
}
