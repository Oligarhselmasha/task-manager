package com.oligarhselmasha.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskType {

    private Integer id;

    private String name;

    @Override
    public String toString() {
        return id.toString();
    }
}
