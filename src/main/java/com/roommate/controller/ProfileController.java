package com.roommate.controller;

import com.roommate.config.SessionConstants;
import com.roommate.dto.ProfileRequest;
import com.roommate.model.User;
import com.roommate.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        ProfileRequest request = new ProfileRequest();
        request.setName(user.getName());
        request.setPhone(user.getPhone());
        request.setGender(user.getGender());
        request.setAge(user.getAge());
        request.setOccupation(user.getOccupation());
        request.setCity(user.getCity());
        request.setBio(user.getBio());

        model.addAttribute("profileRequest", request);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileRequest") ProfileRequest request,
                                BindingResult result,
                                HttpSession session,
                                Model model) {
        if (result.hasErrors()) {
            return "profile";
        }

        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        try {
            User updated = userService.updateProfile(userId, request);
            model.addAttribute("success", "Profile updated successfully.");
            model.addAttribute("user", updated);
            return "profile";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "profile";
        }
    }
}
