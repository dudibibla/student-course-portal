package com.studentportal.portal.repository;

import com.studentportal.portal.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Get chat messages for a specific course (course group chat)
    List<ChatMessage> findByCourseIdOrderByTimestampAsc(Long courseId);

    // Get private messages sent to a specific user
    List<ChatMessage> findByReceiverIdOrderByTimestampAsc(Long receiverId);
    
    // Get all private messages between two users (we can add a custom query later if needed)
}
