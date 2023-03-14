package com.oligarhselmasha.taskmanager.service;

import com.oligarhselmasha.taskmanager.dao.TaskStorage;
import com.oligarhselmasha.taskmanager.exceptions.MissingException;
import com.oligarhselmasha.taskmanager.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskStorage taskStorage;

    public Task createProject(Task task, String login) {
        return taskStorage.newProject(task, login);
    }

    public Task createSubtask(Task task, String login, Integer id) {
        return taskStorage.createSubtask(task, login, id);
    }

    public Task createSubproject(Task task, String login, Integer id) {
        if (taskStorage.isProject(id)) {
            return taskStorage.createSubtask(task, login, id);
        } else throw new MissingException("Parent task is not project");
    }

    public Task getTask(Integer taskId) {
        return taskStorage.getTask(taskId);
    }

    public List<Task> getAllProjects() {
        return taskStorage.getAllProjects();
    }

    public Task updateProject(Task task, String login) {
        return taskStorage.updateTask(task, login);
    }

    public void removeProject(Integer taskId, String login) {
        taskStorage.removeProject(taskId, login);
    }

}
