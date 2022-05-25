package com.example.ODM.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(value = "http://localhost:3000" )
public class AuthentificationController {


    @PostMapping("/login")
    public void login() {

    }
}
