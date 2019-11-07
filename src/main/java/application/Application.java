package application;

import LoggerLogic.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) throws IOException {
        Logger logger = new Logger();
        SpringApplication.run(Application.class, args);
    }
}
