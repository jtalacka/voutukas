package application.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("message", "There's nothing to see here");
        return "home";
    }
}
