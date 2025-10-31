package org.jedi_bachelor.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/home")
    public String home() {
        return "home"; // вернет home.html
    }
}
