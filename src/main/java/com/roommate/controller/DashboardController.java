package com.roommate.controller;

import com.roommate.config.SessionConstants;
import com.roommate.model.User;
import com.roommate.service.PreferenceService;
import com.roommate.service.RoomService;
import com.roommate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;
    private final RoomService roomService;
    private final PreferenceService preferenceService;

    public DashboardController(UserService userService, RoomService roomService, PreferenceService preferenceService) {
        this.userService = userService;
        this.roomService = roomService;
        this.preferenceService = preferenceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        model.addAttribute("user", user);
        model.addAttribute("myRooms", roomService.findByOwnerId(userId));
        model.addAttribute("hasPreference", preferenceService.findByUserId(userId).isPresent());

        return "dashboard";
    }
}
