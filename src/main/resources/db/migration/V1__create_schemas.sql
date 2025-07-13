CREATE SCHEMA IF NOT EXISTS auth_schema;

INSERT INTO role(id, name)
VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_RESCUE_ME_USER');

INSERT INTO usr(id, email, password, refreshToken, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled)
VALUES
    (1, 'ignaciovillarreal20031231@gmail.com', '12345678', null, true, true, true, true);

INSERT INTO user_role(id, role_id, user_id)
VALUES
    (1, 1, 1),
    (1, 2, 1)