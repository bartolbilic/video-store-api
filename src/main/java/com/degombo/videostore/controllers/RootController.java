package com.degombo.videostore.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @PostMapping("/login")
    public void login() {
        //TODO login
    }

    @PostMapping("/register")
    public void register() {
        //TODO register
    }
}
