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
public class LocationId implements Serializable {

    private Long latitude ;
    private Long longitude ;
    private Long meterId ;
}
