package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.UserService;
import com.studentportal.portal.service.ChatService;
import com.studentportal.portal.service.RegistrationService;
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
    private final ChatService chatService;
    private final RegistrationService registrationService;

    @Autowired
    public DashboardController(UserService userService, CourseService courseService, ChatService chatService, RegistrationService registrationService) {
        this.userService = userService;
        this.courseService = courseService;
        this.chatService = chatService;
        this.registrationService = registrationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @org.springframework.security.core.annotation.AuthenticationPrincipal com.studentportal.portal.security.CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long currentUserId = userDetails.getId(); 
        
        User user = userService.findById(currentUserId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("messages", chatService.getGlobalMessages());

        if (user.getRole() == Role.STUDENT) {
            return "student_dashboard";
        } else if (user.getRole() == Role.TEACHER || user.getRole() == Role.ADMIN) {
            model.addAttribute("allCourses", courseService.getAllCourses());
            model.addAttribute("allRegistrations", registrationService.getAllRegistrations());
            return "teacher_dashboard";
        }

        return "redirect:/";
    }

    // Teacher specific action - add new course
    @PostMapping("/dashboard/course/add")
    public String addCourse(@RequestParam String name, 
                            @RequestParam String description, 
                            @RequestParam int maxStudents,
                            @org.springframework.security.core.annotation.AuthenticationPrincipal com.studentportal.portal.security.CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long currentUserId = userDetails.getId(); 
        User teacher = userService.findById(currentUserId).orElseThrow();
        
        if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
            return "redirect:/dashboard?error=unauthorized";
        }

        Course course = new Course(name, description, maxStudents, teacher);
        courseService.createCourse(course, teacher);
        
        return "redirect:/dashboard?courseAdded=true";
    }
}
