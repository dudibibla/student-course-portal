package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.service.CourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CourseService courseService;

    @Autowired
    public GlobalControllerAdvice(CourseService courseService) {
        this.courseService = courseService;
    }

    @ModelAttribute("cartCourses")
    public List<Course> getCartCourses(HttpSession session) {
        List<Long> cartIds = (List<Long>) session.getAttribute("courseCart");
        List<Course> cartCourses = new ArrayList<>();

        if (cartIds != null && !cartIds.isEmpty()) {
            for (Long courseId : cartIds) {
                Optional<Course> courseOpt = courseService.getCourseById(courseId);
                courseOpt.ifPresent(cartCourses::add);
            }
        }
        return cartCourses;
    }
}
