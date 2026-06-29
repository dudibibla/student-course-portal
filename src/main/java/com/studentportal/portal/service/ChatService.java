package com.studentportal.portal.service;

import com.studentportal.portal.entity.ChatMessage;
import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional
    public ChatMessage sendMessage(User sender, User receiver, Course course, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver); // Can be null if it's a course group message
        message.setCourse(course);     // Can be null if it's a direct message
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getCourseMessages(Course course) {
        return chatMessageRepository.findByCourseOrderByTimestampAsc(course);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getDirectMessages(User user1, User user2) {
        // We might need to add a custom query in ChatMessageRepository for this
        // For now, this assumes we have or will have a way to find DMs between two users.
        // Assuming ChatMessageRepository will be updated if needed.
        return chatMessageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(user1, user2, user1, user2);
    }
}
