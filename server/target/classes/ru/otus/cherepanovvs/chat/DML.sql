INSERT INTO `Roles` 
    (role_name) 
VALUES 
    ("ADMIN"),
    ("USER");


INSERT INTO `Users` 
    (login, password, user_name, role_id)
VALUES
    ("login1", "pass1", "user1", 1),
    ("login2", "pass2", "user2", 2);


SELECT 
    `Users`.login AS login,
    `Users`.password AS password,
    `Users`.user_name AS username,
    `Roles`.role_name AS role
FROM `Users`
    JOIN `Roles` ON `Users`.role_id = `Roles`.role_id

INSERT INTO `Users` 
    (login, password, user_name, role_id)
VALUES
    (
        'login3',
        'pass3',
        'user3',
        (SELECT `Roles`.role_id FROM `Roles` WHERE `Roles`.role_name = "USER")
    );


