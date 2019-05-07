package com.kurdov.task.server;

import com.kurdov.task.core.Connection;
import com.kurdov.task.exception.IncorrectDataException;
import com.kurdov.task.core.Notification;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static java.util.Collections.sort;

public class Receiver extends Thread {

    private final List<Notification> notifications;
    private final Socket receivingSocket;

    public Receiver(List<Notification> notifications, Socket receivingSocket) {
        this.notifications = notifications;
        this.receivingSocket = receivingSocket;
    }

    @Override
    public void run() {
        try (Connection connection = new Connection(receivingSocket)){
            handleNotification(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNotification(Connection connection) {
        while (true) {
            try {
                Notification notification = connection.receive();
                addNotification(notification);
            } catch (IncorrectDataException e) {
                System.err.println(e.getMessage() + ". Server received damaged notification.");
            } catch (ClassNotFoundException e) {
                System.err.println(e.getMessage() + ". Server received incompatible data.");
            } catch (IOException e) {
                System.err.println(e.getMessage() + ". Server has problems with connection. Receiver stopped working.");
                break;
            }
        }
    }

    private void addNotification(Notification notification) {
        notifications.add(notification);
        sort(notifications);
    }
}