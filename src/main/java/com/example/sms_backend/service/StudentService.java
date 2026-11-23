package com.example.sms_backend.service;

import com.example.sms_backend.dto.PageResponse;
import com.example.sms_backend.dto.StudentDto;
import com.example.sms_backend.dto.StudentRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {

    StudentDto createStudent(StudentRequest request);

    StudentDto getStudentById(Long studentId);

    List<StudentDto> getAllStudents();

    PageResponse<StudentDto> searchStudents(String q, int minCredits, Pageable pageable);

    StudentDto updateStudent(Long studentId, StudentRequest request);

    void deleteStudent(Long studentId);

    /**
     * Updates the student, then fails on purpose. The audit row written with
     * REQUIRES_NEW survives; the one written with REQUIRED is rolled back
     * along with the name change.
     */
    void updateThenFail(Long studentId, StudentRequest request);
}
