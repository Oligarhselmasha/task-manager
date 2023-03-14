package com.oligarhselmasha.taskmanager.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class User {

    private Integer id;
    @NonNull
    private String userName;
    @NonNull
    private String login;
    @NonNull
    private String password;
    @NonNull
    private Integer roleId;
    private String role;

}
