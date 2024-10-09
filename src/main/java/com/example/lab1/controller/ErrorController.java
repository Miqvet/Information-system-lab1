package com.example.lab1.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403"; // Страница 403
    }

}
