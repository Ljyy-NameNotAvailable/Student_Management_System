package com.example.sms_backend.controller;

import com.example.sms_backend.dto.StudentDto;
import com.example.sms_backend.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private StudentService studentService;

    // build add student rest api
    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto studentDto){
        StudentDto savedStudent = studentService.createStudent(studentDto);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    // buiid get student rest api
    @GetMapping("{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable("id") Long studentId){
        StudentDto studentDto = studentService.getStudentById(studentId);
        return ResponseEntity.ok(studentDto);
    }

    //Build get all student rest api
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents(){
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // update student api
    @PutMapping("{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable("id") Long studentId,
                                                    @RequestBody StudentDto updatedStudent){

        StudentDto studentDto = studentService.updateStudent(studentId, updatedStudent);
        return ResponseEntity.ok(studentDto);
    }

    //delete
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable("id") Long studentId){
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok("deleted");
    }
}
