package com.oligarhselmasha.taskmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity()
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "select user_login, password, 'true' from users " +
                                "where user_login = ? ")
                .authoritiesByUsernameQuery(
                        "SELECT u.user_login, r.role " +
                                "FROM users u " +
                                "LEFT JOIN user_roles ur ON u.user_login = ur.user_login " +
                                "LEFT JOIN roles r ON ur.user_role = r.role_id " +
                                "where u.user_login = ? ");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/project/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/project/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/project/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/project/task/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/project/task/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/project/task/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/project/task/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/project/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/users/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/users/**").hasAnyRole("ADMIN")
                .and().formLogin();

        http.cors().disable().csrf().disable(); // Added for Postman's testing
    }
}
