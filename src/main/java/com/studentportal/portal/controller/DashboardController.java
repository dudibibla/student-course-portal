package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final UserService userService;
    private final CourseService courseService;

    @Autowired
    public DashboardController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // TODO: Replace with Spring Security logged in user
        Long currentUserId = 1L; 
        
        User user = userService.findById(currentUserId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        if (user.getRole() == Role.STUDENT) {
            return "student_dashboard";
        } else if (user.getRole() == Role.TEACHER || user.getRole() == Role.ADMIN) {
            return "teacher_dashboard";
        }

        return "redirect:/";
    }

    // Teacher specific action - add new course
    @PostMapping("/dashboard/course/add")
    public String addCourse(@RequestParam String name, 
                            @RequestParam String description, 
                            @RequestParam int maxStudents) {
        // TODO: Get real teacher
        Long currentUserId = 1L; 
        User teacher = userService.findById(currentUserId).orElseThrow();
        
        if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
            return "redirect:/dashboard?error=unauthorized";
        }

        Course course = new Course(name, description, maxStudents, teacher);
        courseService.createCourse(course, teacher);
        
        return "redirect:/dashboard?courseAdded=true";
    }
}
