package com.roommate.controller;

import com.roommate.config.SessionConstants;
import com.roommate.dto.RoommatePreferenceRequest;
import com.roommate.model.RoommatePreference;
import com.roommate.model.User;
import com.roommate.service.PreferenceService;
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
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final UserService userService;

    public PreferenceController(PreferenceService preferenceService, UserService userService) {
        this.preferenceService = preferenceService;
        this.userService = userService;
    }

    @GetMapping("/preferences")
    public String preferenceForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        RoommatePreferenceRequest request = new RoommatePreferenceRequest();

        preferenceService.findByUserId(userId).ifPresent(pref -> {
            request.setPreferredCity(pref.getPreferredCity());
            request.setMinBudget(pref.getMinBudget());
            request.setMaxBudget(pref.getMaxBudget());
            request.setPreferredGender(pref.getPreferredGender());
            request.setSmokingAllowed(pref.getSmokingAllowed());
            request.setPetsAllowed(pref.getPetsAllowed());
            request.setCleanlinessPreference(pref.getCleanlinessPreference());
            request.setSleepSchedule(pref.getSleepSchedule());
        });

        model.addAttribute("preferenceRequest", request);
        return "preferences";
    }

    @PostMapping("/preferences")
    public String savePreferences(@Valid @ModelAttribute("preferenceRequest") RoommatePreferenceRequest request,
                                  BindingResult result,
                                  HttpSession session,
                                  Model model) {
        if (result.hasErrors()) {
            return "preferences";
        }

        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        RoommatePreference saved = preferenceService.upsert(user, request);
        model.addAttribute("preferenceRequest", request);
        model.addAttribute("success", "Preferences saved successfully.");
        model.addAttribute("savedPreferenceId", saved.getId());
        return "preferences";
    }
}
