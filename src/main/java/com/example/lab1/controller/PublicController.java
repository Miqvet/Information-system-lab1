package com.example.lab1.controller;

import com.example.lab1.entity.auth.Role;
import com.example.lab1.entity.auth.User;
import com.example.lab1.entity.enums.RoleName;
import com.example.lab1.repository.auth.RoleRepository;
import com.example.lab1.repository.auth.UserRepository;
import com.example.lab1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PublicController {

    @GetMapping("/home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "home";  // Главная страница
    }

    @GetMapping("/about")
    public String about() {
        return "about";  // Страница "О нас"
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";  // Страница "Контакты"
    }

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Страница логина
    @GetMapping("/login")
    public String login() {
        return "login"; // Вернёт шаблон login.html
    }

    // Страница регистрации
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Обработка регистрации
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Проверка на уникальность логина
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", null, "Пользователь с таким логином уже существует");
            return "register";
        }
        if(result.hasErrors()){
            return "register";
        }
        // Шифрование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Назначение роли
        if(!userRepository.existsByRole_Name(RoleName.valueOf("ROLE_ADMIN")) && user.isWishToBeAdmin()) {
            Role userRole = roleRepository.findByName(RoleName.valueOf("ROLE_ADMIN")).orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(userRole);
            user.setWishToBeAdmin(false);
        }else{
            Role userRole = roleRepository.findByName(RoleName.valueOf("ROLE_USER")).orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(userRole);
        }
        // Сохранение пользователя
        userService.save(user);

        return "redirect:/login";
    }
}
