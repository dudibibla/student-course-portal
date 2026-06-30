package com.studentportal.portal.repository;

import com.studentportal.portal.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Get all global messages (no course, no receiver)
    List<ChatMessage> findByCourseIsNullAndReceiverIsNullOrderByTimestampAsc();

    // Get chat messages for a specific course (course group chat)
    List<ChatMessage> findByCourseIdOrderByTimestampAsc(Long courseId);
    
    List<ChatMessage> findByCourseOrderByTimestampAsc(com.studentportal.portal.entity.Course course);

    // Get private messages sent to a specific user
    List<ChatMessage> findByReceiverIdOrderByTimestampAsc(Long receiverId);
    
    // Get all private messages between two users
    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            com.studentportal.portal.entity.User sender1, com.studentportal.portal.entity.User receiver1,
            com.studentportal.portal.entity.User receiver2, com.studentportal.portal.entity.User sender2
    );
}
