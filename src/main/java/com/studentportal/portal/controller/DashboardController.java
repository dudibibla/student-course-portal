package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.security.CustomUserDetails;
import com.studentportal.portal.service.ChatService;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.RegistrationService;
import com.studentportal.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("messages", chatService.getGlobalMessages());

        if (user.getRole() == Role.STUDENT) {
            model.addAttribute("registrations", registrationService.getRegistrationsForStudent(user.getId()));
            model.addAttribute("averageGrade", registrationService.calculateAverageGrade(user.getId()));
            return "student_dashboard";
        } else if (user.getRole() == Role.TEACHER || user.getRole() == Role.ADMIN) {
            model.addAttribute("allCourses", courseService.getAllCourses());
            model.addAttribute("allRegistrations", registrationService.getAllRegistrations());
            return "teacher_dashboard";
        }

        return "redirect:/";
    }

    @PostMapping("/dashboard/course/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCourse(@RequestParam String name,
                            @RequestParam String description,
                            @RequestParam int maxStudents) {
        try {
            Course course = new Course(name, description, maxStudents, null);
            courseService.createCourse(course, null);
            return "redirect:/dashboard?courseAdded=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/dashboard?error=invalidCourse";
        }
    }

    @PostMapping("/dashboard/course/claim")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String claimCourse(@RequestParam Long courseId,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        User teacher = userService.findById(userDetails.getId()).orElseThrow();
        try {
            courseService.assignTeacher(courseId, teacher);
            return "redirect:/dashboard?courseClaimed=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/dashboard?error=courseNotFound";
        }
    }
}
