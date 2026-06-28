package com.studentportal.portal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    // A registration contains multiple specific courses (items)
    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistrationItem> items = new ArrayList<>();

    // Constructors
    public Registration() {
        this.registrationDate = LocalDateTime.now();
    }

    public Registration(User student) {
        this.student = student;
        this.registrationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public List<RegistrationItem> getItems() { return items; }
    public void setItems(List<RegistrationItem> items) { this.items = items; }
    
    // Helper method to add an item
    public void addItem(RegistrationItem item) {
        items.add(item);
        item.setRegistration(this);
    }
}
