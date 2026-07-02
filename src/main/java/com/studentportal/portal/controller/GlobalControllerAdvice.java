package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.security.CustomUserDetails;
import com.studentportal.portal.service.CartService;
import com.studentportal.portal.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public GlobalControllerAdvice(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @ModelAttribute("cartCourses")
    public List<Course> getCartCourses(HttpSession session) {
        return cartService.getCartCourses(session);
    }

    @ModelAttribute("cartSize")
    public int getCartSize(HttpSession session) {
        return cartService.getCartIds(session).size();
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userService.findById(userDetails.getId()).orElse(null);
    }
}
