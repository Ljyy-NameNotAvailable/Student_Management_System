package com.example.sms_backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Mutable counter used to demonstrate transaction isolation. The explicit
     * columnDefinition carries a DEFAULT so that ddl-auto=update can add this
     * NOT NULL column to a table that already has rows.
     */
    @Column(name = "credits", nullable = false, columnDefinition = "integer not null default 0")
    private int credits;
}
