package application.controllers;

import application.Repositories.UserRepository;
import application.domain.User;
import application.service.PollService;
import application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @Autowired
    UserService userService;

    @Autowired
    PollService pollService;

    @GetMapping("/")
    public String homePage(Model model) {

        pollService.findAllPolls();

        model.addAttribute("message", "There's nothing to see here");


        return "home";
    }
}
