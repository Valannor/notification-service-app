package demo.service;

import com.kurdov.task.server.Server;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ServerStart {
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("config/spring.xml");

        /**
         * Starting server
         */
        Server server = (Server) context.getBean("server");
        server.runServer();
    }
}
