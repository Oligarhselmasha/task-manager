package com.oligarhselmasha.taskmanager.controllers;

import com.oligarhselmasha.taskmanager.model.User;
import com.oligarhselmasha.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping()
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
