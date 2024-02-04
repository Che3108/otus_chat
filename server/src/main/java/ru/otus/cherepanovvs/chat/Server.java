package ru.otus.cherepanovvs.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new DataBaseUserService();
            System.out.println("Запущен сервис для работы с пользователями");
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandler(this, socket);
                } catch (IOException e) {
                    System.out.println("Не удалось подключить клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("Подключился новый клиент " + clientHandler.getUsername());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Отключился клиент " + clientHandler.getUsername());
    }

    public synchronized boolean isUserBusy(String username) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        ClientHandler listener = null;
        for (ClientHandler client : clients) {
            if (receiverUsername.equals(client.getUsername())) {
                listener = client;
                break;
            }
        }
        if (listener == null) {
            sender.sendMessage("СЕРВЕР: пользователь не найден");
            return;
        }
        sender.sendMessage("Вы шепнули " + listener.getUsername() + ": " + message);
        listener.sendMessage(sender.getUsername() + " шепчет вам: " + message);
    }

    public synchronized void kickUser(ClientHandler initiator, String targetUserName) {
        ClientHandler target = null;
        for (ClientHandler client : clients) {
            if (targetUserName.equals(client.getUsername())) {
                target = client;
                break;
            }
        }
        if (target == null) {
            initiator.sendMessage("СЕРВЕР: пользователь не найден");
            return;
        }
        target.disconnect();
    }


}
