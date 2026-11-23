package com.example.sms_backend.controller;

import com.example.sms_backend.dto.PageResponse;
import com.example.sms_backend.dto.StudentDto;
import com.example.sms_backend.dto.StudentRequest;
import com.example.sms_backend.service.CreditsService;
import com.example.sms_backend.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    // No @Validated on this class on purpose. Since Spring Framework 6.1,
    // constraints on controller method parameters are validated natively and
    // raise HandlerMethodValidationException, which carries the parameter
    // metadata. Adding @Validated would instead wrap this bean in an AOP proxy
    // that raises ConstraintViolationException with method-prefixed paths like
    // "searchStudents.sortBy", and would suppress the native path entirely.


    private final StudentService studentService;
    private final CreditsService creditsService;

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentDto saved = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable("id") Long studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    /** Unpaginated list, kept for compatibility with the original API. */
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    /**
     * GET /api/students/search?q=cat&minCredits=0&page=0&size=20&sortBy=lastName&direction=asc
     *
     * sortBy is pattern-checked against an allowlist rather than passed
     * through: an arbitrary string here becomes a property reference in the
     * generated order-by clause.
     */
    @GetMapping("search")
    public ResponseEntity<PageResponse<StudentDto>> searchStudents(
            @RequestParam(name = "q", required = false, defaultValue = "") String q,
            @RequestParam(name = "minCredits", defaultValue = "0") @Min(0) int minCredits,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", defaultValue = "id")
            @Pattern(regexp = "id|firstName|lastName|email|credits",
                    message = "sortBy must be one of id, firstName, lastName, email, credits")
            String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc")
            @Pattern(regexp = "(?i)asc|desc", message = "direction must be asc or desc")
            String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageResponse<StudentDto> result =
                studentService.searchStudents(q, minCredits, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable("id") Long studentId,
                                                    @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(studentId, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable("id") Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Read-modify-write on credits at a chosen isolation level.
     * POST /api/students/1/credits?delta=1&mode=read-committed&thinkTimeMillis=1000
     */
    @PostMapping("{id}/credits")
    public ResponseEntity<StudentDto> adjustCredits(
            @PathVariable("id") Long studentId,
            @RequestParam("delta") @Min(-1000) @Max(1000) int delta,
            @RequestParam(name = "mode", defaultValue = "read-committed")
            @Pattern(regexp = "read-committed|repeatable-read|pessimistic-lock",
                    message = "mode must be read-committed, repeatable-read or pessimistic-lock")
            String mode,
            @RequestParam(name = "thinkTimeMillis", defaultValue = "0") @Min(0) @Max(10_000)
            long thinkTimeMillis) {

        StudentDto result = switch (mode) {
            case "repeatable-read" -> creditsService.adjustRepeatableRead(studentId, delta, thinkTimeMillis);
            case "pessimistic-lock" -> creditsService.adjustWithPessimisticLock(studentId, delta, thinkTimeMillis);
            default -> creditsService.adjustReadCommitted(studentId, delta, thinkTimeMillis);
        };
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates propagation: returns 500 by design, after which the
     * REQUIRES_NEW audit row is present and the REQUIRED one is not.
     */
    @PostMapping("{id}/rollback-demo")
    public ResponseEntity<Void> rollbackDemo(@PathVariable("id") Long studentId,
                                             @Valid @RequestBody StudentRequest request) {
        studentService.updateThenFail(studentId, request);
        return ResponseEntity.noContent().build();
    }
}
