package com.example.sms_backend.repository;

import com.example.sms_backend.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Free-text search over name and email, with a credits floor.
     * Callers pass "" for q to match everything, so there are no nullable
     * bind parameters to type-cast on the Postgres side.
     */
    @Query("""
            select s from Student s
            where (lower(s.firstName) like lower(concat('%', :q, '%'))
                or lower(s.lastName)  like lower(concat('%', :q, '%'))
                or lower(s.email)     like lower(concat('%', :q, '%')))
              and s.credits >= :minCredits
            """)
    Page<Student> search(@Param("q") String q,
                         @Param("minCredits") int minCredits,
                         Pageable pageable);
}
