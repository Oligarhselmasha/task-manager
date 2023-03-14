package com.oligarhselmasha.taskmanager.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Task {

    private Integer id;
    @NotBlank
    private String name;
    private String status;
    @NotBlank
    private String description;
    private Integer parentId;
    private String login;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Task> subTask;
    private TaskType taskType;
}
