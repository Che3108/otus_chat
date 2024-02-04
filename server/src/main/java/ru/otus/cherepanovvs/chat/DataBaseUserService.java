package ru.otus.cherepanovvs.chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataBaseUserService implements UserService {
    class User {
        private String login;
        private String password;
        private String username;
        private String role;

        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.role = "USER";
        }

        public User(String login, String password, String username, String role) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.role = role;
        }
    }

    private static final String DATABASE_URL = "jdbc:mariadb://localhost:3306/chat_users?user=user&password=12345";
    private static final String SELECT_USERS_SQL = "SELECT `Users`.login AS login, `Users`.password AS password, `Users`.user_name AS username, `Roles`.role_name AS role FROM `Users` JOIN `Roles` ON `Users`.role_id = `Roles`.role_id;";
    private static final String INSERT_USERS_SQL = "INSERT INTO `Users` (login, password, user_name, role_id) VALUES ( ?, ?, ?, (SELECT `Roles`.role_id FROM `Roles` WHERE `Roles`.role_name = ?));";
    private List<User> users;

    public DataBaseUserService() {
        this.users = getUsersList();
    }

    public List<User> getUsersList() {
        List<User> users = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            try(Statement statement = connection.createStatement()) {
                try(ResultSet resultSet = statement.executeQuery(SELECT_USERS_SQL)) {
                    while (resultSet.next()) {
                        users.add(new User(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.username;
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        User newUser = new User(login, password, username);
        try(Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            try(PreparedStatement ps = connection.prepareStatement(INSERT_USERS_SQL)) {
                ps.setString(1, newUser.login);
                ps.setString(2, newUser.password);
                ps.setString(3, newUser.username);
                ps.setString(4, newUser.role);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.users = getUsersList();
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAdminRole(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                if (u.role.equals("ADMIN")) {
                    return true;
                }
            }
        }
        return false;
    }
}