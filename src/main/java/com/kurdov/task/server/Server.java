package com.kurdov.task.server;

import com.kurdov.task.core.Notification;
import com.kurdov.task.server.handler.HttpHandler;
import com.kurdov.task.server.handler.MailHandler;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int serverPort;
    private final HttpHandler httpHandler;
    private final MailHandler mailHandler;

    private final List<Notification> notifications;
    private final List<Notification> tryAgainNotifications;

    private boolean isRunning;

    public Server(int serverPort,
                  HttpHandler httpHandler,
                  MailHandler mailHandler) {
        this.serverPort = serverPort;
        this.httpHandler = httpHandler;
        this.mailHandler = mailHandler;

        this.notifications = new CopyOnWriteArrayList<>();
        this.tryAgainNotifications = new CopyOnWriteArrayList<>();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            //Inner flag for programmatically shutdown
            isRunning = true;

            //Processors are daemons in order to stop inner loops when main thread shutdown
            Processor mainProcessThread = new Processor(notifications, tryAgainNotifications, httpHandler, mailHandler);
            mainProcessThread.setDaemon(true);
            mainProcessThread.start();

            Processor processThreadForTryAgain = new Processor(tryAgainNotifications, tryAgainNotifications, httpHandler, mailHandler);
            processThreadForTryAgain.setDaemon(true);
            processThreadForTryAgain.start();

            //The main cycle of the application, which runs until the processing of the last received notification
            ExecutorService service = Executors.newCachedThreadPool();
            while (isRunning || !notifications.isEmpty()) {
                if (isRunning) {
                    //New receiver for every new client connected
                    Socket receivingSocket = serverSocket.accept();
                    service.submit(new Receiver(notifications, receivingSocket));
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage() + ". Server has problems with connection. Server stopped working.");
        }
    }
}
