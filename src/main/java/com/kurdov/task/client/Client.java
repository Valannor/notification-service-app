package com.kurdov.task.client;

import com.kurdov.task.core.Connection;
import com.kurdov.task.core.Notification;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class Client {

    private final String host;
    private final int port;
    private Connection connection;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;

        createConnection();
    }

    private void createConnection() {
        try {
            this.connection = new Connection(new Socket(host, port));
        } catch (IOException e) {
            System.err.println(e.getMessage() + ". Problem in client while establishing connection. Check if server is started.");
        }
    }

    public void runClient(List<Notification> notifications) {
        ClientSocket clientSocket = new ClientSocket(connection);
        clientSocket.start();

        if (clientSocket.isClientConnected()) {
            System.out.println("Running client");
        } else {
            System.err.println("Problem while starting client");
        }

        while (clientSocket.isClientConnected()) {
            for (Notification notification : notifications) {
                send(notification);
            }
            notifications.clear();
        }
    }

    private void send(Notification notification) {
        try {
            connection.send(notification);
        } catch (IOException e) {
            System.err.println(e.getMessage() + ". Problem occurred while sending " + notification.getNotificationType()
                    + ". Notifications externalId = " + notification.getExternalId());
        }
    }

    public Notification buildNotification(String externalId,
                                          String message,
                                          Date time,
                                          Notification.Type notificationType,
                                          String extraParam) throws NullPointerException {
        return new Notification(externalId, message, time, notificationType, extraParam);
    }
}
