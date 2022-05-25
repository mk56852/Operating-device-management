package com.example.ODM.Service.UserService;

import com.example.ODM.Domain.User.User;
import com.example.ODM.Dto.UserDto;

import java.util.Set;


public interface UserService {

    /***    FIND METHOD    ***/

    public User findUserByUserName (String Username) ;
    public User findUserById(int id);
    public User findUserByEmail(String email) ;


    /***     Get Details Method   ***/

    public UserDto getUserDetailsById (int id ) ;
    public UserDto getUserDetailsByUserName(String userName);
    public Set<UserDto> getAllUsers() ;


    /***   Other Method   ***/
    public boolean enableUserApp(String Username) ;
    public boolean addUser(User user)    ;


    /***    Update Method   ***/

    public boolean updateUser(String UserName , User user)  ;



    /***    Delete  Method   ***/
    public boolean DeleteUser(int id)  ;

}
