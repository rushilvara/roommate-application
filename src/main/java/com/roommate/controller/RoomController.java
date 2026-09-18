package com.roommate.controller;

import com.roommate.config.SessionConstants;
import com.roommate.dto.RoomRequest;
import com.roommate.model.Room;
import com.roommate.model.User;
import com.roommate.service.RoomService;
import com.roommate.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;

    public RoomController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping
    public String listRooms(@RequestParam(required = false) String city,
                            @RequestParam(required = false) BigDecimal minRent,
                            @RequestParam(required = false) BigDecimal maxRent,
                            Model model) {
        model.addAttribute("rooms", roomService.search(city, minRent, maxRent));
        model.addAttribute("city", city);
        model.addAttribute("minRent", minRent);
        model.addAttribute("maxRent", maxRent);
        return "rooms";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("roomRequest", new RoomRequest());
        return "room-form";
    }

    @PostMapping
    public String createRoom(@Valid @ModelAttribute("roomRequest") RoomRequest request,
                             BindingResult result,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "room-form";
        }

        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        User owner = userService.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));

        roomService.createRoom(request, owner);
        redirectAttributes.addFlashAttribute("success", "Room listing created.");
        return "redirect:/rooms";
    }

    @GetMapping("/{id}")
    public String roomDetails(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        Room room = roomService.getRoom(id);
        model.addAttribute("room", room);
        model.addAttribute("isOwner", room.getOwner().getId().equals(userId));
        return "room-detail";
    }

    @GetMapping("/{id}/edit")
    public String editRoom(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        Room room = roomService.getRoom(id);
        if (!room.getOwner().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "You cannot edit this room.");
            return "redirect:/rooms/" + id;
        }

        RoomRequest request = new RoomRequest();
        request.setTitle(room.getTitle());
        request.setDescription(room.getDescription());
        request.setLocation(room.getLocation());
        request.setMonthlyRent(room.getMonthlyRent());
        request.setAvailableFrom(room.getAvailableFrom());
        request.setFurnished(room.isFurnished());

        model.addAttribute("roomRequest", request);
        model.addAttribute("roomId", id);
        return "room-form";
    }

    @PostMapping("/{id}")
    public String updateRoom(@PathVariable Long id,
                             @Valid @ModelAttribute("roomRequest") RoomRequest request,
                             BindingResult result,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roomId", id);
            return "room-form";
        }

        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        try {
            roomService.updateRoom(id, request, userId);
            redirectAttributes.addFlashAttribute("success", "Room listing updated.");
            return "redirect:/rooms/" + id;
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/rooms/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteRoom(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(SessionConstants.USER_ID);
        try {
            roomService.deleteRoom(id, userId);
            redirectAttributes.addFlashAttribute("success", "Room listing deleted.");
            return "redirect:/rooms";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/rooms/" + id;
        }
    }
}
