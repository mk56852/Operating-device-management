package com.example.ODM.Dto;


import com.example.ODM.Domain.User.Role;
import lombok.*;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto implements Serializable {
    private int id ;
    private String userName ;
    private String email ;
    private String description;
    private String phoneNumber ;
    private Role role ;
    private boolean isEnable ;



}
