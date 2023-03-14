package com.oligarhselmasha.taskmanager.dao;

import com.oligarhselmasha.taskmanager.exceptions.MissingException;
import com.oligarhselmasha.taskmanager.model.Task;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Component
public class TaskStorageImpl implements TaskStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final TaskTypeDaoImpl taskTypeDao;

    private final static String NEW_PROJECT_SQL = "INSERT INTO task (task_name, " +
            "task_description, user_login, task_type_id) " +
            "values (?, ?, ?, ?);";
    private final static String GET_TASK_SQL = "SELECT t.task_id, t.task_name, s.status_name, t.task_description, t.parent_task_id, t.user_login, " +
            "t.create_time, t.update_time, t.task_type_id " +
            "FROM task t " +
            "LEFT JOIN status s ON s.status_id = t.task_status_id " +
            "LEFT JOIN task_types tt ON tt.task_type_id = t.task_type_id " +
            "WHERE task_id = ?";
    private final static String GET_STRUCTURE_SQL = "SELECT t.task_id, t.task_name, s.status_name, t.task_description, t.parent_task_id, t.user_login, " +
            "t.create_time, t.update_time, t.task_type_id " +
            "FROM task t " +
            "LEFT JOIN status s ON s.status_id = t.task_status_id ";
    private final static String NEW_SUBTASK_SQL = "INSERT INTO task (task_name,  " +
            "task_description, user_login, PARENT_TASK_ID, task_type_id)  " +
            "values (?, ?, ?, ?, ?)";
    private final static String GET_SUBTASKS_SQL = "SELECT t.task_id, t.task_name, s.status_name, t.task_description, t.parent_task_id, t.user_login, " +
            "t.create_time, t.update_time, t.task_type_id " +
            "FROM task t " +
            "LEFT JOIN status s ON s.status_id = t.task_status_id " +
            "WHERE PARENT_TASK_ID = ?";
    private final static String GET_SUBTASKS_BY_PARENT_ID_SQL = "SELECT t.task_id, t.task_name, s.status_name, t.task_description, t.parent_task_id, t.user_login, " +
            "t.create_time, t.update_time, t.task_type_id " +
            "FROM task t " +
            "LEFT JOIN status s ON s.status_id = t.task_status_id " +
            "where parent_task_id = ?";

    @Override
    public Task newProject(Task task, String login) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(NEW_PROJECT_SQL, new String[]{"task_id"});
            ps.setString(1, task.getName());
            ps.setString(2, task.getDescription());
            ps.setString(3, login);
            ps.setString(4, task.getTaskType().toString());
            return ps;
        }, keyHolder);
        int taskId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getTask(taskId);
    }

    @Override
    public Task getTask(int taskId) {
        return jdbcTemplate.query(GET_TASK_SQL, (rs, rowNum) -> makeTasks(rs), taskId)
                .stream()
                .findFirst().orElseThrow(() -> new MissingException("Ошибка при создании задачи"));
    }

    @Override
    public List<Task> getAllProjects() {
        return jdbcTemplate.query(GET_STRUCTURE_SQL, (rs, rowNum) -> makeTasks(rs));
    }

    @Override
    public Task createSubtask(Task task, String login, Integer id) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(NEW_SUBTASK_SQL, new String[]{"task_id"});
            ps.setString(1, task.getName());
            ps.setString(2, task.getDescription());
            ps.setString(3, login);
            ps.setInt(4, id);
            ps.setString(5, task.getTaskType().toString());
            return ps;
        }, keyHolder);
        int taskId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getTask(taskId);
    }

    @Override
    public boolean isProject(int taskId) {
        Task task = getTask(taskId);
        return (task.getParentId() == 0);
    }

    @Override
    public List<Task> getSubtasks(int id) {
        return jdbcTemplate.query(GET_SUBTASKS_SQL, (rs, rowNum) -> makeTasks(rs), id);
    }

    @Override
    public Task updateTask(Task task, String login) {
        if (userStorage.isAdmin(login)) {
            String sql = "update task set  " +
                    "task_name = ?, task_description = ?, TASK_STATUS_ID = ?, user_login = ?,  UPDATE_TIME = ?  " +
                    "where task_id = ? ";
            int statusId = getStatusId(task.getStatus());
            jdbcTemplate.update(sql
                    , task.getName()
                    , task.getDescription()
                    , statusId
                    , login
                    , Timestamp.valueOf(LocalDateTime.now())
                    , task.getId());
            return getTask(task.getId());
        } else {
            if (getTask(task.getId()).getParentId() == 0) {
                throw new MissingException("The user cannot edit the project, only subtasks");
            }
            if (Objects.equals(getTask(task.getId()).getLogin(), login)) {
                String sql = "update task set  " +
                        "TASK_STATUS_ID = ?, UPDATE_TIME = ? " +
                        "where task_id = ? ";
                int statusId = getStatusId(task.getStatus());
                jdbcTemplate.update(sql
                        , statusId
                        , Timestamp.valueOf(LocalDateTime.now())
                        , task.getId());
                return getTask(task.getId());
            } else {
                throw new MissingException("The user can only change the status of his task");
            }
        }
    }

    @Override
    public void removeProject(Integer taskId, String login) {
        String sql = "delete from task where task_id = ?";
        if (userStorage.isAdmin(login)) {
            jdbcTemplate.update(sql, taskId);
            for (Task task : getAllProjectsByParentsIds(taskId)) {
                removeProject(task.getId(), login);
            }
        } else {
            if (isProject(taskId)) {
                throw new MissingException("The user cannot edit the project, only subtasks");
            }
            if (Objects.equals(getTask(taskId).getLogin(), login)) {
                jdbcTemplate.update(sql, taskId);
                for (Task task : getAllProjectsByParentsIds(taskId)) {
                    removeProject(task.getId(), login);
                }
            } else {
                throw new MissingException("The user can only delete his task");
            }
        }
    }


    private Task makeTasks(ResultSet rs) throws SQLException {
        int id = rs.getInt("task_id");
        String name = rs.getString("task_name");
        String description = rs.getString("task_description");
        String status = rs.getString("status_name");
        int parenId = rs.getInt("parent_task_id");
        String login = rs.getString("user_login");
        LocalDateTime createTime = rs.getTimestamp("create_time").toLocalDateTime();
        LocalDateTime updateTime = rs.getTimestamp("update_time").toLocalDateTime();
        List<Task> subtasks = getSubtasks(id);
        int taskTypeId = rs.getInt("task_type_id");
        return Task.builder()
                .id(id)
                .name(name)
                .status(status)
                .description(description)
                .parentId(parenId)
                .login(login)
                .createTime(createTime)
                .updateTime(updateTime)
                .subTask(subtasks)
                .taskType(taskTypeDao.getTaskTypeById(taskTypeId))
                .build();
    }

    private int getStatusId(String status) {
        int id = 0;
        switch (status) {
            case "NEW":
                id = 1;
                break;
            case "DONE":
                id = 3;
                break;
            case "IN_PROGRESS":
                id = 2;
                break;
        }
        return id;
    }

    private List<Task> getAllProjectsByParentsIds(int parentId) {
        return jdbcTemplate.query(GET_SUBTASKS_BY_PARENT_ID_SQL, (rs, rowNum) -> makeTasks(rs), parentId);
    }
}
