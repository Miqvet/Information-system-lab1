package com.example.lab1.controller;

import com.example.lab1.entity.StudyGroup;
import com.example.lab1.entity.auth.User;
import com.example.lab1.entity.enums.Color;
import com.example.lab1.entity.enums.Country;
import com.example.lab1.entity.enums.FormOfEducation;
import com.example.lab1.entity.enums.Semester;
import com.example.lab1.repository.StudyGroupRepository;
import com.example.lab1.service.StudyGroupService;
import com.example.lab1.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import java.security.Principal;
import java.util.List;

@Controller
public class StudyGroupController {
    @Autowired
    private StudyGroupService studyGroupService; // Сервис для работы с данными StudyGroup

    @Autowired
    private UserService userService; // Сервис для работы с данными StudyGroup

    private final WebSocketController webSocketController;
    @Autowired
    private StudyGroupRepository studyGroupRepository;

    public StudyGroupController(StudyGroupService studyGroupService, UserService userService, WebSocketController webSocketController) {
        this.studyGroupService = studyGroupService;
        this.userService = userService;
        this.webSocketController = webSocketController;
    }

    @GetMapping("/user")
    public String userPage(HttpSession session, Model model,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "") String filterField, // Поле для фильтрации
                           @RequestParam(defaultValue = "") String filterValue, // Значение для фильтрации
                           @RequestParam(defaultValue = "name") String sortBy,
                           @RequestParam(required = false) String filter) {
        // Извлекаем данные из сессии
        String username = (String) session.getAttribute("username");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        // Если данных нет в сессии, добавляем их
        if (username == null || isAdmin == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            username = authentication.getName(); // Логин пользователя
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

            // Сохраняем данные в сессии
            session.setAttribute("username", username);
            session.setAttribute("isAdmin", isAdmin);
        }

        // Добавляем данные в модель
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);

        Page<StudyGroup> studyGroups = studyGroupService.findFilteredAndSorted(page, size, filterField, filterValue, sortBy);
        model.addAttribute("studyGroups", studyGroups);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studyGroups.getTotalPages());
        model.addAttribute("filterField", filterField);
        model.addAttribute("filterValue", filterValue);
        model.addAttribute("sortBy", sortBy);
        return "user/index";
    }

    @GetMapping("/user/create")
    public String showCreateForm(Model model) {
        model.addAttribute("studyGroup", new StudyGroup());
        createFullModel(model);
        return "user/create-edit";
    }

    @PostMapping("/user/create")
    public String createStudyGroup(@Valid @ModelAttribute() StudyGroup studyGroup, BindingResult result, Principal principal, Model model) {
        if (result.hasErrors()) {
            createFullModel(model);
            return "user/create-edit";
        }
        // Устанавливаем текущего пользователя как владельца (createdBy)
        User currentUser = userService.findByUsername(principal.getName());
        studyGroup.setCreatedBy(currentUser);
        studyGroupService.save(studyGroup);
        webSocketController.notifyClients("StudyGroup deleted with ID: ");
        return "redirect:/user";

    }

    @GetMapping("/user/edit/{id}")
    public String showEditForm(@PathVariable int id,Principal principal, Model model) {
        StudyGroup studyGroup = studyGroupService.getById(id);
        // Проверка: только админ или создатель объекта может удалять
        if (!principal.getName().equals(studyGroup.getCreatedBy().getUsername()) && !isAdmin(principal)) {
            return "error/403"; // Страница с ошибкой доступа
        }
        model.addAttribute("studyGroup", studyGroup);
        createFullModel(model);
        return "user/create-edit";
    }

    @PostMapping("/user/edit/{id}")
    public String updateStudyGroup(@PathVariable int id, @Valid @ModelAttribute StudyGroup studyGroup, BindingResult result,Principal principal, Model model) {

        if (result.hasErrors()) {
            createFullModel(model);
            model.addAttribute("studyGroup", studyGroup);
            return "user/create-edit";
        }

        if (!principal.getName().equals(studyGroupService.getById(id).getCreatedBy().getUsername()) && !isAdmin(principal)) {
            return "error/403";
        }
        studyGroupService.update(id, studyGroup);
        webSocketController.notifyClients("StudyGroup deleted with ID: ");
        return "redirect:/user";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteStudyGroup(@PathVariable int id,Principal principal) {
        StudyGroup studyGroup = studyGroupService.getById(id);

        if (!principal.getName().equals(studyGroup.getCreatedBy().getUsername()) && !isAdmin(principal)) {
            return "error/403";
        }
        studyGroupService.deleteById(id);
        webSocketController.notifyClients("StudyGroup deleted with ID: ");
        return "redirect:/user";
    }


    @GetMapping("/user/visualization")
    public String showVisualization(Model model) {
        List<StudyGroup> studyGroups = studyGroupRepository.findAll();
        model.addAttribute("studyGroups", studyGroups);
        return "user/visualization-page";
    }

    @GetMapping("/user/study-groups")
    public ResponseEntity<List<StudyGroup>> getStudyGroups() {
        List<StudyGroup> studyGroups = studyGroupRepository.findAll();
        return ResponseEntity.ok(studyGroups);
    }


    private void createFullModel(Model model){
        model.addAttribute("formOfEducationEnum", FormOfEducation.values());
        model.addAttribute("semesterEnum", Semester.values());
        model.addAttribute("countryEnum", Country.values());
        model.addAttribute("colorEnum", Color.values());
    }

    // Метод для проверки роли администратора
    private boolean isAdmin(Principal principal) {
        UserDetails userDetails = (UserDetails) ((Authentication) principal).getPrincipal();
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
