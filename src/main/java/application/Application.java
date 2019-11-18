package application;

import LoggerLogic.Logger;
import application.CompositeKeys.PollID;
import application.Modals.Poll;
import application.Modals.User;
import application.Repositories.PollRepository;
import com.github.seratch.jslack.Slack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException {
        run(Application.class, args);
        Logger logger = new Logger();

    }
}
