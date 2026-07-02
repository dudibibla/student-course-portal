package com.studentportal.portal.controller;

import com.studentportal.portal.security.CustomUserDetails;
import com.studentportal.portal.service.CartService;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CourseCatalogController {

    private final CourseService courseService;
    private final RegistrationService registrationService;
    private final CartService cartService;

    @Autowired
    public CourseCatalogController(CourseService courseService, RegistrationService registrationService, CartService cartService) {
        this.courseService = courseService;
        this.registrationService = registrationService;
        this.cartService = cartService;
    }

    @GetMapping("/courses")
    public String catalog(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "catalog";
    }

    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('STUDENT')")
    public String addToCart(@RequestParam Long courseId, HttpSession session) {
        cartService.addToCart(session, courseId);
        return "redirect:/courses?added=true";
    }

    @GetMapping("/cart")
    @PreAuthorize("hasRole('STUDENT')")
    public String viewCart() {
        // cartCourses is supplied globally by GlobalControllerAdvice
        return "cart";
    }

    @PostMapping("/cart/remove")
    @PreAuthorize("hasRole('STUDENT')")
    public String removeFromCart(@RequestParam Long courseId, HttpSession session) {
        cartService.removeFromCart(session, courseId);
        return "redirect:/cart?removed=true";
    }

    @PostMapping("/cart/checkout")
    @PreAuthorize("hasRole('STUDENT')")
    public String checkout(HttpSession session, Model model,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        List<Long> cart = cartService.getCartIds(session);
        if (cart.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        try {
            registrationService.checkoutCart(userDetails.getId(), cart);
            cartService.clearCart(session);
            return "redirect:/dashboard?success=true";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "cart";
        }
    }
}
