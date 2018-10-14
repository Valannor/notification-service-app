package com.kurdov.task.core;

import com.kurdov.task.exception.IncorrectDataException;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    private boolean isConnectionActive;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());

        this.isConnectionActive = true;
    }

    public boolean isConnectionActive() {
        return isConnectionActive;
    }

    public void setConnectionActive(boolean connectionActive) {
        isConnectionActive = connectionActive;
    }

    public void send(Notification notification) throws IOException {
        synchronized (outputStream) {
            outputStream.writeObject(notification);
        }
    }

    public Notification receive() throws IOException, ClassNotFoundException, IncorrectDataException {
        synchronized (inputStream) {
            Notification notification = (Notification) inputStream.readObject();

            if (!isNotificationValid(notification)) {
                throw new IncorrectDataException("Incorrect notification received");
            }

            return notification;
        }
    }

    private static boolean isNotificationValid(Notification notification) {
        return notification.getExternalId() != null
                && notification.getMessage() != null
                && notification.getTime() != null
                && notification.getNotificationType() != null
                && notification.getExtraParam() != null;
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        outputStream.close();
        inputStream.close();
    }
}
