package com.kurdov.task.server.handler;

import com.kurdov.task.core.Notification;

public interface NotificationHandler {

    void sendNotification(Notification notification) throws Exception;
}
