package com.oligarhselmasha.taskmanager.dao;

import com.oligarhselmasha.taskmanager.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserStorage {
    boolean isAdmin(String login);

    User createUser(User user);

    List<User> getUsers();

    User getUser(int userId);
}
