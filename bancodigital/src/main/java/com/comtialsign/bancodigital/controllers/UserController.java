package com.comtialsign.bancodigital.controllers;

import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.dtos.UserDto;
import com.comtialsign.bancodigital.services.impls.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/create")
    public ResponseEntity<User> createUser(@RequestBody UserDto user) throws Exception {
        User newUser = userService.createUser(user);
        return new ResponseEntity(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findAllUsers() throws Exception {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
