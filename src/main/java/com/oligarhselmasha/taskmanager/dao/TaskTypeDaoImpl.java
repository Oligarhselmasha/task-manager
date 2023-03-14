package com.oligarhselmasha.taskmanager.dao;

import com.oligarhselmasha.taskmanager.exceptions.MissingException;
import com.oligarhselmasha.taskmanager.model.TaskType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class TaskTypeDaoImpl implements TaskTypeDao{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public TaskType getTaskTypeById(int id) {
        String sql = "SELECT * " +
                "FROM task_types " +
                "WHERE task_type_id = ?";
        return jdbcTemplate.query(
                        sql, (rs, rowNum) -> makeTaskType(rs), id
                ).stream()
                .findFirst()
                .orElseThrow(() -> new MissingException("Task type error"));
    }

    private TaskType makeTaskType(ResultSet rs) throws SQLException {
        return TaskType.builder()
                .id(rs.getInt("task_type_id"))
                .name(rs.getString("task_type_name"))
                .build();
    }
}
