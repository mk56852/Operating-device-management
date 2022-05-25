package com.example.ODM.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class LocationDto {
    private Long latitude ;
    private Long longitude ;
    private String region ;
}
