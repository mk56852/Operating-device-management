package com.example.ODM.Controller;


import com.example.ODM.Domain.User.User;
import com.example.ODM.Dto.UserDto;
import com.example.ODM.Service.UserService.UserService;
import com.example.ODM.Util.AddUserRequest;
import com.example.ODM.Util.UpdateUserRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")

public class UserController {


    UserService userSevice ;





    @PostMapping(path = "/add")
    public ResponseEntity add(@RequestBody AddUserRequest request )
    {
        User user =  new User(request.getUserName(), request.getEmail(), request.getPassword(),request.getDescription(),request.getRole() ,request.getPhoneNumber()) ;

        if(userSevice.addUser(user)) {
            log.info("New User Added Succesfully");
            return ResponseEntity.ok("User Added") ;
        }
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
    }

    @GetMapping("/getAll")
    public ResponseEntity getAllUsers() {
        log.info("GetAll Users ") ;
        return ResponseEntity.ok(userSevice.getAllUsers()) ;
    }

    @GetMapping("/get/{UserName}")
    public ResponseEntity getUserByUserName(@PathVariable(name = "UserName") String userName){
        UserDto userDetails = userSevice.getUserDetailsByUserName(userName) ;
      
        if(userDetails != null ){
            log.info("getUser "+userName);
            return ResponseEntity.ok(userDetails);

        }


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
    }




    @PutMapping("/update/{UserName}")
    public ResponseEntity updateUser(@PathVariable(name="UserName") String userName , @RequestBody UpdateUserRequest request)
    {
        User user = new User(request.getUserName(), request.getEmail(), "0000" ,request.getDescription(), request.getRole() , request.getPhoneNumber(),request.isEnable()) ;

        if(userSevice.updateUser(userName , user ) )
        {
            log.info("User Updated ");
            return ResponseEntity.ok("User Updated");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable(name="id") int Id)
    {
        if(userSevice.DeleteUser(Id)){
            log.info("User Deleted");
            return ResponseEntity.ok("User Deleted") ;
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;

    }


}
