package com.studentportal.portal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "registration_items")
public class RegistrationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Constructors
    public RegistrationItem() {}

    public RegistrationItem(Registration registration, Course course) {
        this.registration = registration;
        this.course = course;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Registration getRegistration() { return registration; }
    public void setRegistration(Registration registration) { this.registration = registration; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
