**Notification service**

1. The application listens on the TCP port for a `command` like “Remind me something”;

2. Notifications can be sent by one of two channels: `email` or `http`;

3. `Command` includes:`externalId` (_string_), `message` (_string_), `time`, `notificationType` (_enum: mail/http_), `extra_params`;

4. `Extra_params` for _http_: `url=http://....` for _mail_: `email=a@a.ru`;

5. Server accepts command and puts it into the queue;

6. When the specified time comes, application sends the notification by the chosen channel;

7. It's forbidden to use Spring Boot, servers and server containers, messaging systems.
