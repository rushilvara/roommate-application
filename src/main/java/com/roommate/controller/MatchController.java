package com.roommate.controller;

import com.roommate.config.SessionConstants;
import com.roommate.service.MatchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/matches")
    public String matches(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        model.addAttribute("matches", matchService.findMatchesForUser(userId));
        return "matches";
    }
}
