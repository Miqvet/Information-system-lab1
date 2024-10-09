package com.example.lab1.controller;

import com.example.lab1.entity.Person;
import com.example.lab1.entity.auth.Role;
import com.example.lab1.entity.auth.User;
import com.example.lab1.entity.enums.RoleName;
import com.example.lab1.repository.auth.RoleRepository;
import com.example.lab1.repository.auth.UserRepository;
import com.example.lab1.service.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyGroupService studyGroupService;

    public AdminController(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    @Autowired
    private RoleRepository roleRepository;


    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("requests", userRepository.findByWishToBeAdmin(true));
        return "admin/index"; // Здесь должен быть путь к шаблону из /admin/
    }


    @PostMapping("/admin")
    public String approveAdminRequest(@RequestParam("userId") Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Role userRole = roleRepository.findByName(RoleName.valueOf("ROLE_ADMIN")).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setWishToBeAdmin(false);
        user.setRole(userRole);

        // Сохранение пользователя
        userRepository.save(user);

        return "redirect:/admin";
    }



    @GetMapping("/admin/admin-page")
    public String adminPageRequest(Model model) {
        return "admin/admin-page"; // Здесь должен быть путь к шаблону из /admin/
    }

    @PostMapping("/admin/admin-page/less-than")
    public String countShouldBeExpelledLessThan(@RequestParam("threshold1") int threshold, Model model) {
        long count = studyGroupService.countByShouldBeExpelledLessThan(threshold);
        model.addAttribute("countLessThan", count);
        return "admin/admin-page";
    }

    @PostMapping("/admin/admin-page/greater-than")
    public String countShouldBeExpelledGreaterThan(@RequestParam("threshold2") int threshold, Model model) {
        long count = studyGroupService.countByShouldBeExpelledGreaterThan(threshold);
        model.addAttribute("countGreaterThan", count);
        return "admin/admin-page";
    }

    @GetMapping("/admin/admin-page/unique-admins")
    public String getUniqueGroupAdmins(Model model) {
        List<Person> uniqueAdmins = studyGroupService.findUniqueGroupAdmins();
        model.addAttribute("uniqueAdmins", uniqueAdmins);
        return "admin/admin-page";
    }

    @PostMapping("/admin/admin-page/expel-group")
    public String expelGroupStudents(@RequestParam int groupId, Model model) {
        try{
            studyGroupService.expelGroupStudents(groupId);
        }catch (NoSuchElementException e){
            model.addAttribute("message1", "Student group does not exist");
            return "admin/admin-page";
        }
        return "redirect:/admin/admin-page";
    }

    @PostMapping("/admin/admin-page/transfer-students")
    public String transferStudents(@RequestParam("fromGroupId") int fromGroupId, @RequestParam("toGroupId") int toGroupId, Model model) {
        try{
            studyGroupService.transferStudents(fromGroupId, toGroupId);
        }catch (NoSuchElementException e){
            model.addAttribute("message2", "Student group does not exist");
            return "admin/admin-page";
        }
        return "redirect:/admin/admin-page";
    }
}
