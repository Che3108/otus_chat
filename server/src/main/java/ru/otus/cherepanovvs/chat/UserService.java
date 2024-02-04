package ru.otus.cherepanovvs.chat;

public interface UserService {
    String getUsernameByLoginAndPassword(String login, String password);
    void createNewUser(String login, String password, String username);
    boolean isLoginAlreadyExist(String login);
    boolean isUsernameAlreadyExist(String username);
    boolean isAdminRole(String username);
}
