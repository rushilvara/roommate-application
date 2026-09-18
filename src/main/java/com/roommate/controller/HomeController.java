package com.roommate.controller;

import com.roommate.config.SessionConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute(SessionConstants.USER_ID) != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }
}
