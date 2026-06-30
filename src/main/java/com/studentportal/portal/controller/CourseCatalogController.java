package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CourseCatalogController {

    private final CourseService courseService;
    private final RegistrationService registrationService;

    @Autowired
    public CourseCatalogController(CourseService courseService, RegistrationService registrationService) {
        this.courseService = courseService;
        this.registrationService = registrationService;
    }

    @GetMapping("/courses")
    public String catalog(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "catalog";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long courseId, HttpSession session) {
        List<Long> cart = (List<Long>) session.getAttribute("courseCart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        if (!cart.contains(courseId)) {
            cart.add(courseId);
        }
        session.setAttribute("courseCart", cart);
        
        return "redirect:/courses?added=true";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<Long> cart = (List<Long>) session.getAttribute("courseCart");
        List<Course> cartCourses = new ArrayList<>();
        
        if (cart != null) {
            for (Long courseId : cart) {
                courseService.getCourseById(courseId).ifPresent(cartCourses::add);
            }
        }
        
        model.addAttribute("cartCourses", cartCourses);
        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long courseId, HttpSession session) {
        List<Long> cart = (List<Long>) session.getAttribute("courseCart");
        if (cart != null) {
            cart.remove(courseId);
            session.setAttribute("courseCart", cart);
        }
        return "redirect:/cart?removed=true";
    }

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session, Model model, @org.springframework.security.core.annotation.AuthenticationPrincipal com.studentportal.portal.security.CustomUserDetails userDetails) {
        List<Long> cart = (List<Long>) session.getAttribute("courseCart");
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long currentUserId = userDetails.getId(); 

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        try {
            registrationService.checkoutCart(currentUserId, cart);
            session.removeAttribute("courseCart"); // Clear the cart
            return "redirect:/dashboard?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            // Re-populate cart for the view
            List<Course> cartCourses = new ArrayList<>();
            for (Long courseId : cart) {
                courseService.getCourseById(courseId).ifPresent(cartCourses::add);
            }
            model.addAttribute("cartCourses", cartCourses);
            return "cart";
        }
    }
}
