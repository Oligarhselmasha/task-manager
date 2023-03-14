package com.oligarhselmasha.taskmanager.dao;

import com.oligarhselmasha.taskmanager.exceptions.MissingException;
import com.oligarhselmasha.taskmanager.model.User;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Data
@Component
public class UserStorageImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String IS_ADMIN_SQL = "SELECT u.user_login, r.role " +
            "FROM users u " +
            "LEFT JOIN user_roles ur ON u.user_login = ur.user_login " +
            "LEFT JOIN roles r ON ur.user_role = r.role_id " +
            "where u.user_login= ? ";
    private final static String CREATE_USER_SQL = "INSERT INTO users (user_name, user_login, password) " +
            "VALUES (?, ?, ?);";
    private final static String MAKE_ROLE_SQL = "INSERT INTO user_roles (user_login, user_role) " +
            "VALUES (?, ?);";
    private final static String GET_USER_SQL = "SELECT u.user_id, u.user_name, u.user_login, u.password, ur.user_role, r.role " +
            "FROM users u " +
            "LEFT JOIN USER_ROLES ur ON u.user_login = ur.user_login " +
            "LEFT JOIN ROLES r ON r.role_id = ur.user_role " +
            "WHERE user_id = ?";
    private final static String GET_USERS_SQL =  "SELECT u.user_id, u.user_name, u.user_login, u.password, ur.user_role, r.role " +
            "FROM users u " +
            "LEFT JOIN USER_ROLES ur ON u.user_login = ur.user_login " +
            "LEFT JOIN ROLES r ON r.role_id = ur.user_role ";

    @Override
    public boolean isAdmin(String login) {
        SqlRowSet sqlRowSetRows = jdbcTemplate.queryForRowSet(IS_ADMIN_SQL, login);
        if (sqlRowSetRows.next()) {
            String role = sqlRowSetRows.getString("role");
            return Objects.equals(role, "ROLE_ADMIN");
        }
        return false;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_USER_SQL, new String[]{"user_id"});
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getPassword());
            return ps;
        }, keyHolder);
        makeUserRole(user.getLogin(), user.getRoleId());
        int userId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getUser(userId);
    }

    private void makeUserRole(String login, Integer role) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(MAKE_ROLE_SQL, new String[]{"user_roles_id"});
            ps.setString(1, login);
            ps.setInt(2, role);
            return ps;
        });
    }

    @Override
    public User getUser(int userId) {
        return jdbcTemplate.query(GET_USER_SQL, (rs, rowNum) -> makeUsers(rs), userId)
                .stream()
                .findFirst().orElseThrow(() -> new MissingException("Ошибка при создании пользователя"));
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(GET_USERS_SQL, (rs, rowNum) -> makeUsers(rs));
    }


    private User makeUsers(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String name = rs.getString("user_name");
        String login = rs.getString("user_login");
        String password = rs.getString("password");
        int roleId = rs.getInt("user_role");
        String role = rs.getString("role");
        return User.builder()
                .id(id)
                .userName(name)
                .login(login)
                .password(password)
                .roleId(roleId)
                .role(role)
                .build();
    }
}
