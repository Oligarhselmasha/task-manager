INSERT INTO status (status_name)
VALUES ('NEW');

INSERT INTO status (status_name)
VALUES ('IN_PROGRESS');

INSERT INTO status (status_name)
VALUES ('DONE');

INSERT INTO task_types (task_type_name)
VALUES ('Менеджер');

INSERT INTO task_types (task_type_name)
VALUES ('Технический специалист');

INSERT INTO users (user_name, user_login, password)
VALUES ('Кирилл Буланов', 'user', 'password');

INSERT INTO users (user_name, user_login, password)
VALUES ('Кирилл Буланов', 'admin', 'password');

INSERT INTO roles (role)
VALUES ('ROLE_ADMIN');

INSERT INTO roles (role)
VALUES ('ROLE_USER');

INSERT INTO user_roles (user_login, user_role)
VALUES ('admin', 1);

INSERT INTO user_roles (user_login, user_role)
VALUES ('user', 2);