package com.kurdov.task.server;

import com.kurdov.task.exception.IncorrectDataException;
import com.kurdov.task.exception.TryLaterException;
import com.kurdov.task.server.handler.HttpHandler;
import com.kurdov.task.server.handler.MailHandler;
import com.kurdov.task.core.Notification;
import org.apache.http.client.ClientProtocolException;

import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import java.util.Date;
import java.util.List;

public class Processor extends Thread {

    private final List<Notification> notifications;
    private final List<Notification> tryAgainNotifications;

    private final HttpHandler httpHandler;
    private final MailHandler mailHandler;

    public Processor(List<Notification> notifications,
                     List<Notification> tryAgainNotifications,
                     HttpHandler httpHandler,
                     MailHandler mailHandler) {
        this.notifications = notifications;
        this.tryAgainNotifications = tryAgainNotifications;
        this.httpHandler = httpHandler;
        this.mailHandler = mailHandler;
    }

    /**
     */
    @Override
    public void run() {
        while (true) {
            if (!notifications.isEmpty()) {
                Notification notification = notifications.get(0);
                Date currentTime = new Date();

                if (notifications.get(0) != null
                        && (notification.getTime().before(currentTime) || notification.getTime().equals(currentTime))) {

                    notifications.remove(0);
                    Notification.Type type = notification.getNotificationType();
                    if (type == Notification.Type.HTTP) {
                        processHttp(notification);
                    }

                    if (type == Notification.Type.MAIL) {
                        processMail(notification);
                    }
                }
            }
        }
    }

    private void processHttp(Notification notification) {
        try {
            httpHandler.sendNotification(notification);
        } catch (TryLaterException e) {
            tryAgainNotifications.add(notification);
            System.err.println(e.getMessage());
        } catch (ClientProtocolException | IncorrectDataException e) {
            System.err.println(e.getMessage());
        }
    }

    private void processMail(Notification notification) {
        try {
            mailHandler.sendNotification(notification);
        } catch (TryLaterException | SendFailedException e) {
            tryAgainNotifications.add(notification);
            System.err.println(e.getMessage());
        } catch (AddressException | IncorrectDataException e) {
            System.err.println(e.getMessage());
        }
    }
}
