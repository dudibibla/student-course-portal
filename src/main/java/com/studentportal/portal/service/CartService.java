package com.studentportal.portal.service;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.repository.CourseRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralizes all access to the session-based registration cart so the
 * unchecked session cast lives in exactly one place.
 */
@Service
public class CartService {

    public static final String CART_SESSION_KEY = "courseCart";

    private final CourseRepository courseRepository;

    @Autowired
    public CartService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @SuppressWarnings("unchecked")
    public List<Long> getCartIds(HttpSession session) {
        List<Long> cart = (List<Long>) session.getAttribute(CART_SESSION_KEY);
        return cart != null ? cart : new ArrayList<>();
    }

    public void addToCart(HttpSession session, Long courseId) {
        List<Long> cart = getCartIds(session);
        if (!cart.contains(courseId)) {
            cart.add(courseId);
        }
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeFromCart(HttpSession session, Long courseId) {
        List<Long> cart = getCartIds(session);
        cart.remove(courseId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public List<Course> getCartCourses(HttpSession session) {
        List<Long> ids = getCartIds(session);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return courseRepository.findAllByIdWithTeacher(ids);
    }
}
