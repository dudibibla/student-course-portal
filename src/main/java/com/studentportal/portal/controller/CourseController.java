package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.security.CustomUserDetails;
import com.studentportal.portal.service.ChatService;
import com.studentportal.portal.service.CourseReviewService;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Controller
public class CourseController {

    private final CourseService courseService;
    private final CourseReviewService courseReviewService;
    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public CourseController(CourseService courseService, CourseReviewService courseReviewService, UserService userService, ChatService chatService) {
        this.courseService = courseService;
        this.courseReviewService = courseReviewService;
        this.userService = userService;
        this.chatService = chatService;
    }

    @GetMapping("/courses/{id}")
    public String viewCourseDetails(@PathVariable Long id, Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<Course> courseOpt = courseService.getCourseById(id);
        if (courseOpt.isEmpty()) {
            return "redirect:/courses?error=notfound";
        }

        Course course = courseOpt.get();
        model.addAttribute("course", course);
        model.addAttribute("reviews", courseReviewService.getReviewsForCourse(id));
        model.addAttribute("availableSpots", courseService.getAvailableSpots(id));
        model.addAttribute("messages", chatService.getCourseMessages(course));

        if (userDetails != null) {
            userService.findById(userDetails.getId()).ifPresent(user -> model.addAttribute("user", user));
        }

        return "course_details";
    }

    @PostMapping("/courses/{id}/review")
    @PreAuthorize("hasRole('STUDENT')")
    public String addReview(@PathVariable Long id,
                            @RequestParam String content,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User student = userService.findById(userDetails.getId()).orElse(null);
        if (student == null) {
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {
            return "redirect:/courses/" + id + "?error=emptyReview";
        }

        courseReviewService.addReview(id, student, content);
        return "redirect:/courses/" + id + "?reviewAdded=true";
    }

    @PostMapping("/courses/{id}/grades/{itemId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String updateGrade(@PathVariable Long id,
                              @PathVariable Long itemId,
                              @RequestParam Integer grade,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Fixed error codes only - never echo exception messages into the URL
        try {
            courseService.updateRegistrationItemGrade(itemId, grade, user);
            return "redirect:/courses/" + id + "?gradeUpdated=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/courses/" + id + "?error=invalidGrade";
        } catch (IllegalStateException e) {
            return "redirect:/courses/" + id + "?error=notAuthorized";
        }
    }

    @PostMapping("/courses/{id}/assignments")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String addAssignment(@PathVariable Long id,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String dueDate,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            LocalDate parsedDate = LocalDate.parse(dueDate);
            courseService.addAssignment(id, title, description, parsedDate, user);
            return "redirect:/courses/" + id + "?assignmentAdded=true";
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return "redirect:/courses/" + id + "?error=invalidAssignment";
        } catch (IllegalStateException e) {
            return "redirect:/courses/" + id + "?error=notAuthorized";
        }
    }
}
