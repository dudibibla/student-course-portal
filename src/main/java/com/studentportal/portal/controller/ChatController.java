package com.studentportal.portal.controller;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.service.ChatService;
import com.studentportal.portal.service.CourseService;
import com.studentportal.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final CourseService courseService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService, CourseService courseService) {
        this.chatService = chatService;
        this.userService = userService;
        this.courseService = courseService;
    }

    @PostMapping("/chat/send")
    public String sendMessage(@RequestParam String content,
                              @RequestParam(required = false) Long courseId,
                              @RequestParam(required = false) Long receiverId,
                              @org.springframework.security.core.annotation.AuthenticationPrincipal com.studentportal.portal.security.CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long currentUserId = userDetails.getId(); 
        User sender = userService.findById(currentUserId).orElseThrow();

        Course course = null;
        if (courseId != null) {
            course = courseService.getCourseById(courseId).orElse(null);
        }

        User receiver = null;
        if (receiverId != null) {
            receiver = userService.findById(receiverId).orElse(null);
        }

        chatService.sendMessage(sender, receiver, course, content);

        return "redirect:/dashboard"; // Realistically, redirect back to the specific chat view or anchor
    }
}
