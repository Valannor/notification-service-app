package com.kurdov.task.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Notification implements Comparable, Serializable {

    private final String externalId;
    private final String message;
    private final Date time;
    private final Type notificationType;
    private final String extraParam;

    public Notification(String externalId,
                        String message,
                        Date time,
                        Type notificationType,
                        String extraParam) {
        this.externalId = externalId;
        this.message = message;
        this.time = time;
        this.notificationType = notificationType;
        this.extraParam = extraParam;
    }

    public enum Type {
        MAIL,
        HTTP
    }

    public String getExternalId() {
        return externalId;
    }

    public String getMessage() {
        return message;
    }

    public Date getTime() {
        return time;
    }

    public Type getNotificationType() {
        return notificationType;
    }

    public String getExtraParam() {
        return extraParam;
    }

    public int compareTo(Object o) {
        if (o instanceof Notification) {
            Notification n = (Notification) o;
            return this.getTime().compareTo(n.getTime());
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(externalId, that.externalId) &&
                Objects.equals(message, that.message) &&
                Objects.equals(time, that.time) &&
                notificationType == that.notificationType &&
                Objects.equals(extraParam, that.extraParam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, message, time, notificationType, extraParam);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "externalId='" + externalId + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", notificationType=" + notificationType +
                ", extraParam='" + extraParam + '\'' +
                '}';
    }
}
