package com.kurdov.task.client;

import com.kurdov.task.core.Connection;
import com.kurdov.task.exception.IncorrectDataException;

import java.io.IOException;

public class ClientSocket extends Thread {

    private Connection connection;

    public ClientSocket(Connection connection) {
        this.connection = connection;
    }

    public boolean isClientConnected() {
        if (connection == null) {
            return false;
        }
        return connection.isConnectionActive();
    }

    public void setClientConnected(boolean clientConnected) {
        if (connection == null) {
            return;
        }
        connection.setConnectionActive(clientConnected);
    }

    @Override
    public void run() {
        try {
            while (isClientConnected()) {
                System.out.println(connection.receive());
            }
        } catch (IOException e) {
//            System.err.println(e.getMessage() + ". Lost connection with Server.");
            setClientConnected(false);
            try {
                if (!connection.isConnectionActive()) {
                    connection.close();
                }
            } catch (IOException e1) {
                System.err.println(e.getMessage() + ". Problem occurred while connection close.");
            }
        } catch (IncorrectDataException e) {
            System.err.println(e.getMessage() + ". Client received damaged notification.");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage() + ". Client received incompatible data.");
        }
    }
}
