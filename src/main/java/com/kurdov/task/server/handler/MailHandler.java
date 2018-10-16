package com.kurdov.task.server.handler;

import com.kurdov.task.exception.IncorrectDataException;
import com.kurdov.task.exception.TryLaterException;
import com.kurdov.task.core.Notification;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static javax.mail.Transport.send;

public class MailHandler implements NotificationHandler {

    private String handlerMailUsername;
    private String password;
    private String host;
    private String port;

    private Session session;

    public MailHandler(String handlerMailUsername, String password, String host, String port) {
        this.handlerMailUsername = handlerMailUsername;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    private Session createSession() {

        if (session == null) {
            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(handlerMailUsername, password);
                        }
                    });
        }

        return session;
    }

    public void sendNotification(Notification notification)
            throws TryLaterException, AddressException, IncorrectDataException, SendFailedException {

        Message message = createMimeMessage();
        populateMimeMessage(notification, message);

        try {
            send(message);
        } catch (MessagingException e) {
            throw new SendFailedException("Problems while sending email");
        }
    }

    private void populateMimeMessage(Notification notification,
                                     Message message) throws AddressException, IncorrectDataException {
        String externalId = notification.getExternalId();
        String mailRecipient = notification.getExtraParam();
        String mailMessage = notification.getMessage();

        try {
            message.setFrom(new InternetAddress(handlerMailUsername));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mailRecipient));
            message.setSubject("Mail notification");
            message.setText(mailMessage);

        } catch (AddressException e) {
            throw new AddressException("Wrong email address " + mailRecipient
                    + ". Problematic notifications externalId = " + externalId);
        } catch (MessagingException e) {
            throw new IncorrectDataException("Incorrect message data. " +
                    "Problematic notifications externalId = " + externalId);
        }
    }

    private Message createMimeMessage() throws TryLaterException {
        Message message;
        try {
            message = new MimeMessage(createSession());
        } catch (Exception e) {
            throw new TryLaterException("Can't connect to mail server" + host + ":" + port);
        }
        return message;
    }
}
