package com.example.ODM.Util;



import com.example.ODM.Domain.User.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class AddUserRequest {

    private String userName ;
    private String email  ;
    private String password ;
    private String description ;
    private Role role ;
    private String phoneNumber ;

}

