package com.studentportal.portal.config;

import com.studentportal.portal.entity.Course;
import com.studentportal.portal.entity.Role;
import com.studentportal.portal.entity.User;
import com.studentportal.portal.repository.CourseRepository;
import com.studentportal.portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Only seed if the database is empty
            if (userRepository.count() == 0) {
                
                System.out.println("Seeding database with initial data...");

                // Create Admin
                User admin = new User("System Admin", "admin@portal.com", passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                // Create Teachers
                User teacher1 = new User("Dr. Alan Turing", "turing@portal.com", passwordEncoder.encode("password"));
                teacher1.setRole(Role.TEACHER);
                userRepository.save(teacher1);

                User teacher2 = new User("Prof. Grace Hopper", "hopper@portal.com", passwordEncoder.encode("password"));
                teacher2.setRole(Role.TEACHER);
                userRepository.save(teacher2);

                User teacher3 = new User("Joan Clarke", "clarke@portal.com", passwordEncoder.encode("password"));
                teacher3.setRole(Role.TEACHER);
                userRepository.save(teacher3);

                User teacher4 = new User("Gordon Welchman", "welchman@portal.com", passwordEncoder.encode("password"));
                teacher4.setRole(Role.TEACHER);
                userRepository.save(teacher4);

                // Create Students (Classic Cryptography & CS placeholder names)
                User student1 = new User("David", "david@student.com", passwordEncoder.encode("password"));
                student1.setRole(Role.STUDENT);
                userRepository.save(student1);

                User student2 = new User("Alice Smith", "alice@student.com", passwordEncoder.encode("password"));
                student2.setRole(Role.STUDENT);
                userRepository.save(student2);

                User student3 = new User("Bob Jones", "bob@student.com", passwordEncoder.encode("password"));
                student3.setRole(Role.STUDENT);
                userRepository.save(student3);

                User student4 = new User("Eve Eavesdropper", "eve@student.com", passwordEncoder.encode("password"));
                student4.setRole(Role.STUDENT);
                userRepository.save(student4);

                User student5 = new User("Mallory Attacker", "mallory@student.com", passwordEncoder.encode("password"));
                student5.setRole(Role.STUDENT);
                userRepository.save(student5);

                User student6 = new User("Trent Trusted", "trent@student.com", passwordEncoder.encode("password"));
                student6.setRole(Role.STUDENT);
                userRepository.save(student6);

                User student7 = new User("Carol Communicator", "carol@student.com", passwordEncoder.encode("password"));
                student7.setRole(Role.STUDENT);
                userRepository.save(student7);

                User student8 = new User("Dave Data", "dave@student.com", passwordEncoder.encode("password"));
                student8.setRole(Role.STUDENT);
                userRepository.save(student8);

                User student9 = new User("Craig Cracker", "craig@student.com", passwordEncoder.encode("password"));
                student9.setRole(Role.STUDENT);
                userRepository.save(student9);

                User student10 = new User("Peggy Prover", "peggy@student.com", passwordEncoder.encode("password"));
                student10.setRole(Role.STUDENT);
                userRepository.save(student10);

                User student11 = new User("Victor Verifier", "victor@student.com", passwordEncoder.encode("password"));
                student11.setRole(Role.STUDENT);
                userRepository.save(student11);

                User student12 = new User("Sybil Pseudonym", "sybil@student.com", passwordEncoder.encode("password"));
                student12.setRole(Role.STUDENT);
                userRepository.save(student12);

                User student13 = new User("Judy Judge", "judy@student.com", passwordEncoder.encode("password"));
                student13.setRole(Role.STUDENT);
                userRepository.save(student13);

                // Create Courses
                // Create Courses
                Course course1 = new Course("Introduction to Computer Science", "Learn the basics of programming, algorithms, and data structures.", 30, teacher1);
                Course course1b = new Course("Introduction to Computer Science", "Learn the basics of programming, algorithms, and data structures.", 30, teacher2); // Parallel group
                
                Course course2 = new Course("Advanced Java Programming", "Deep dive into Spring Boot, microservices, and enterprise architecture.", 25, teacher2);
                Course course3 = new Course("Database Systems", "SQL, NoSQL, and relational database design principles.", 40, teacher1);
                Course course3b = new Course("Database Systems", "SQL, NoSQL, and relational database design principles.", 40, teacher3); // Parallel group

                Course course4 = new Course("Web Development", "HTML, CSS, JavaScript, and modern frontend frameworks.", 35, teacher2);
                Course course5 = new Course("Artificial Intelligence", "Introduction to machine learning, neural networks, and AI ethics.", 20, teacher3);
                Course course5b = new Course("Artificial Intelligence", "Introduction to machine learning, neural networks, and AI ethics.", 20, teacher4); // Parallel group
                
                Course course6 = new Course("Cryptography & Security", "Study of encryption, decryption, Enigma, and modern cyber security.", 25, teacher3);
                Course course7 = new Course("Traffic Analysis & Intercepts", "Advanced methodologies in network traffic decryption and flow analysis.", 15, teacher4);
                
                courseRepository.saveAll(List.of(course1, course1b, course2, course3, course3b, course4, course5, course5b, course6, course7));

                System.out.println("Database seeded successfully!");
            }
        };
    }
}
