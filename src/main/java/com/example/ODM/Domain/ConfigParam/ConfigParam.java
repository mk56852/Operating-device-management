package com.example.ODM.Domain.ConfigParam;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ConfigParam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;
    @Column(unique = true)
    private String name;
    private String modelVersion;
    private String scope;
    private String description;
    private String defaultValue;
    private String valueType;





}
