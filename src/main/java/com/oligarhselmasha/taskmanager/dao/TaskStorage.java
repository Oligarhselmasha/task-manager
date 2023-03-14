package com.oligarhselmasha.taskmanager.dao;

import com.oligarhselmasha.taskmanager.model.Task;

import java.util.List;

public interface TaskStorage {

    Task newProject(Task task, String login);
    List<Task> getAllProjects();

    List <Task> getSubtasks(int id);

    Task getTask(int taskId);

    Task createSubtask(Task task, String login, Integer id);
    
    boolean isProject(int taskId);

    Task updateTask(Task task, String login);

    void removeProject(Integer taskId, String login);
}
