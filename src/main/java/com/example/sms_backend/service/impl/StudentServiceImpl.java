package com.example.sms_backend.service.impl;

import com.example.sms_backend.dto.StudentDto;
import com.example.sms_backend.entity.Student;
import com.example.sms_backend.exception.ResourceNotFoundException;
import com.example.sms_backend.mapper.StudentMapper;
import com.example.sms_backend.repository.StudentRepository;
import com.example.sms_backend.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;
    @Override
    public StudentDto createStudent(StudentDto studentDto) {
        Student student = StudentMapper.mapToStudent(studentDto);
        Student savedStudent = studentRepository.save(student);
        return StudentMapper.mapToStudentDto(savedStudent);

    }

    @Override
    public StudentDto getStudentById(Long studentId){

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("student not exist with: "+studentId));

        return StudentMapper.mapToStudentDto(student);
    }
    @Override
    public List<StudentDto> getAllStudents(){

        List<Student> students = studentRepository.findAll();
        return students.stream().map(student -> StudentMapper.mapToStudentDto(student))
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto updateStudent(Long studentId, StudentDto updatedStudent) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new ResourceNotFoundException("student idn not found: " + studentId)
        );
        student.setFirstName(updatedStudent.getFirstName());
        student.setLastName(updatedStudent.getLastName());
        student.setEmail(updatedStudent.getEmail());
        Student updatedStudentObj = studentRepository.save(student);

        return StudentMapper.mapToStudentDto(updatedStudentObj);
    }

    @Override
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new ResourceNotFoundException("student idn not found: " + studentId)
        );


        studentRepository.deleteById(studentId);
    }


}
