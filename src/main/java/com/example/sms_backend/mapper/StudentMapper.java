package com.example.sms_backend.mapper;

import com.example.sms_backend.dto.StudentDto;
import com.example.sms_backend.dto.StudentRequest;
import com.example.sms_backend.entity.Student;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentDto toDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getCredits()
        );
    }

    /** New entities start at zero credits; id is assigned by the database. */
    public static Student toEntity(StudentRequest request) {
        return Student.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .credits(0)
                .build();
    }

    /** Copies the mutable fields onto a managed entity, leaving id and credits alone. */
    public static void applyTo(StudentRequest request, Student student) {
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
    }
}
