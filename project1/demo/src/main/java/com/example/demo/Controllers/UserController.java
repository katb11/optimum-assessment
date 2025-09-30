package com.example.demo.Controllers;

import com.example.demo.Services.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String demo(Model model) {

        model.addAttribute("users", userService.getUsers());

        return "users";
    }
}