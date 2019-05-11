package demo.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.kurdov.task.client.Client;
import com.kurdov.task.core.Notification;
import demo.environment.SmtpServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Before running this demo, start server via ServerStart class.
 */
@RunWith(MockitoJUnitRunner.class)
public class Demo {

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule(2525);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private Client client;
    private List<Notification> notifications;

    @Before
    public void init() {
        //Creating client
        client = new Client("localhost", 12345);

        //List of notifications
        notifications = new ArrayList<>();

        //Creating mailNotification object
        Date date1 = new Date();
        Notification mailNotification = client.buildNotification(
                "1",
                "Luke, I am your father. Notification date " + date1,
                date1,
                Notification.Type.MAIL,
                "luke_skywalker@noreply.com");
        notifications.add(mailNotification);

        //Creating httpNotification object for real
        Date date2 = new Date();
        Notification httpNotification1 = client.buildNotification(
                "2",
                "Luke, I am your father. Notification date " + date2,
                date2,
                Notification.Type.HTTP,
                "https://httpbin.org/post");
        notifications.add(httpNotification1);

        //Creating httpNotification object for WireMock local server
        Date date3 = new Date();
        Notification httpNotification2 = client.buildNotification(
                "2",
                "Luke, I am your father. Notification date " + date3,
                date3,
                Notification.Type.HTTP,
                "http://localhost:8080");
        notifications.add(httpNotification2);

        //Configuration of WireMock response
        configureFor("localhost", 8080);
        wireMockRule.stubFor(get(urlPathEqualTo("/notification"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Accept-Encoding", "gzip,deflate")
                        .withHeader("Content-Type", "application/x-www-form-urlencoded")
                        .withHeader("Host", "localhost:8080")
                        .withHeader("User-Agent", "Notification service")
                        .withBody("Success in sending HttpNotification")));
    }

    /**
     * This is a demo, to show that server sends notifications.
     * Method basically prints to the console the result.
     * @throws MessagingException
     * @throws IOException
     */
    @Test
    public void mailDemo() throws MessagingException, IOException {
        //Starting client
        client.runClient(notifications);

        System.out.println("MAIL NOTIFICATION DEMO");
        MimeMessage[] receivedMessages = smtpServerRule.getMessages();
        MimeMessage current = receivedMessages[0];
        System.out.println("Subject: " + current.getSubject());
        System.out.println("Recipients: " + current.getAllRecipients()[0]);
        System.out.println("Sender: " + current.getFrom()[0]);
        String content = (String) current.getContent();
        System.out.println("Content: " + content);
    }
}
