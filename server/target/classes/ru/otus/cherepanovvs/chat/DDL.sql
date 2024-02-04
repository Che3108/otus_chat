CREATE DATABASE chat_users;

CREATE TABLE Roles (
  role_id INT AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(255)
);

CREATE TABLE Users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  login VARCHAR(255),
  password VARCHAR(255),
  user_name VARCHAR(255),
  role_id INT,
  FOREIGN KEY (role_id) REFERENCES Roles(role_id)
);