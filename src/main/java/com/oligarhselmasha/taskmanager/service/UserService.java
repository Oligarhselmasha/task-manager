package com.oligarhselmasha.taskmanager.service;

import com.oligarhselmasha.taskmanager.dao.TaskStorage;
import com.oligarhselmasha.taskmanager.dao.UserStorage;
import com.oligarhselmasha.taskmanager.model.Task;
import com.oligarhselmasha.taskmanager.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }
}
