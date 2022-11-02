package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {
    private final UserService userService;
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "home"})
    public String home(Model model, Principal principal) {
        User user = userService.readByEmail(principal.getName());
        if(user.getRole().getName().equals("ADMIN")) {
            model.addAttribute("users", userService.getAll());
        } else {
            model.addAttribute("users", List.of(user));
        }
        return "home";
    }
}
