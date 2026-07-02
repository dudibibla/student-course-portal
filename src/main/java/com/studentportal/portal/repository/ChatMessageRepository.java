package com.studentportal.portal.repository;

import com.studentportal.portal.entity.ChatMessage;
import com.studentportal.portal.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Global messages (no course, no receiver) with sender fetched eagerly
    @Query("select m from ChatMessage m join fetch m.sender where m.course is null and m.receiver is null order by m.timestamp asc")
    List<ChatMessage> findGlobalMessages();

    // Course group-chat messages with sender fetched eagerly
    @Query("select m from ChatMessage m join fetch m.sender where m.course = :course order by m.timestamp asc")
    List<ChatMessage> findCourseMessages(Course course);
}
