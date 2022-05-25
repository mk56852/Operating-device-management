package com.example.ODM.Util;



import com.example.ODM.Domain.User.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateUserRequest {

    private String userName ;
    private String email ;
    private String description ;
    private Role role ;
    private String phoneNumber ;
    private boolean isEnable ;
}
