package com.oligarhselmasha.taskmanager.controllers;

import com.oligarhselmasha.taskmanager.model.Task;
import com.oligarhselmasha.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Task createProject(@Valid @RequestBody Task task, Principal principal) {
        return taskService.createProject(task, principal.getName());
    }

    @GetMapping()
    public List<Task> getStructure() {
        return taskService.getAllProjects();
    }

    @PostMapping("/{id}")
    public Task createSubproject(@Valid @RequestBody Task task, Principal principal, @PathVariable("id") Integer id) {
        return taskService.createSubproject(task, principal.getName(), id);
    }

    @PostMapping("/task/{id}")
    public Task createSubtask(@Valid @RequestBody Task task, Principal principal, @PathVariable("id") Integer id) {
        return taskService.createSubtask(task, principal.getName(), id);
    }

    @GetMapping("/{id}")
    public Task getProject(@PathVariable("id") Integer projectId) {
        return taskService.getTask(projectId);
    }

    @PutMapping
    public Task updateProject(@RequestBody Task task, Principal principal) {
        return taskService.updateProject(task, principal.getName());
    }

    @DeleteMapping("/{id}")
    public void removeProject(@PathVariable("id") Integer taskId, Principal principal) {
        taskService.removeProject(taskId, principal.getName());
    }
}
