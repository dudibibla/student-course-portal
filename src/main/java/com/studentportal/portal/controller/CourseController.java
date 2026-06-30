package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.CourseReviewService;
import com.studentportal.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class CourseController {

    private final CourseService courseService;
    private final CourseReviewService courseReviewService;
    private final UserService userService;

    @Autowired
    public CourseController(CourseService courseService, CourseReviewService courseReviewService, UserService userService) {
        this.courseService = courseService;
        this.courseReviewService = courseReviewService;
        this.userService = userService;
    }

    @GetMapping("/courses/{id}")
    public String viewCourseDetails(@PathVariable Long id, Model model) {
        Optional<Course> courseOpt = courseService.getCourseById(id);
        if (courseOpt.isEmpty()) {
            return "redirect:/courses?error=notfound";
        }
        
        Course course = courseOpt.get();
        model.addAttribute("course", course);
        model.addAttribute("reviews", courseReviewService.getReviewsForCourse(id));
        model.addAttribute("availableSpots", courseService.getAvailableSpots(id));
        
        return "course_details";
    }

    @PostMapping("/courses/{id}/review")
    public String addReview(@PathVariable Long id, 
                            @RequestParam String content,
                            @org.springframework.security.core.annotation.AuthenticationPrincipal com.studentportal.portal.security.CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }

        User student = userService.findById(userDetails.getId()).orElse(null);
        if (student == null) {
            return "redirect:/login";
        }

        courseReviewService.addReview(id, student, content);
        return "redirect:/courses/" + id + "?reviewAdded=true";
    }
}
