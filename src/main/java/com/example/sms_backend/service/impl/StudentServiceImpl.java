package com.example.sms_backend.service.impl;

import com.example.sms_backend.dto.PageResponse;
import com.example.sms_backend.dto.StudentDto;
import com.example.sms_backend.dto.StudentRequest;
import com.example.sms_backend.entity.Student;
import com.example.sms_backend.exception.ResourceNotFoundException;
import com.example.sms_backend.mapper.StudentMapper;
import com.example.sms_backend.repository.StudentRepository;
import com.example.sms_backend.service.AuditService;
import com.example.sms_backend.service.StudentService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final AuditService auditService;
    private final Counter studentsCreated;

    public StudentServiceImpl(StudentRepository studentRepository,
                              AuditService auditService,
                              MeterRegistry meterRegistry) {
        this.studentRepository = studentRepository;
        this.auditService = auditService;
        this.studentsCreated = Counter.builder("sms.students.created")
                .description("Students created through the API")
                .register(meterRegistry);
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentRequest request) {
        Student saved = studentRepository.save(StudentMapper.toEntity(request));
        auditService.recordJoiningCaller(saved.getId(), "CREATE", saved.getEmail());
        studentsCreated.increment();
        log.info("Created student id={}", saved.getId());
        return StudentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.student(studentId));
        return StudentMapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentDto> searchStudents(String q, int minCredits, Pageable pageable) {
        return PageResponse.from(
                studentRepository.search(q, minCredits, pageable),
                StudentMapper::toDto);
    }

    /**
     * The read and the write now share one transaction. Because the entity is
     * managed for the whole method, there is no save() call: Hibernate's dirty
     * checking flushes the change at commit. Previously this method ran
     * findById and save in two separate transactions, so a concurrent delete
     * between them would resurrect the row.
     */
    @Override
    @Transactional
    public StudentDto updateStudent(Long studentId, StudentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.student(studentId));
        StudentMapper.applyTo(request, student);
        auditService.recordJoiningCaller(studentId, "UPDATE", request.email());
        return StudentMapper.toDto(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.student(studentId));
        auditService.recordInOwnTransaction(studentId, "DELETE", student.getEmail());
        studentRepository.delete(student);
    }

    @Override
    @Transactional
    public void updateThenFail(Long studentId, StudentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.student(studentId));
        StudentMapper.applyTo(request, student);

        auditService.recordJoiningCaller(studentId, "WILL_ROLL_BACK", request.email());
        auditService.recordInOwnTransaction(studentId, "WILL_SURVIVE", request.email());

        // Unchecked exception, so the surrounding transaction rolls back. A
        // checked exception would commit it unless rollbackFor said otherwise.
        throw new IllegalStateException("Deliberate failure after the audit writes");
    }
}
