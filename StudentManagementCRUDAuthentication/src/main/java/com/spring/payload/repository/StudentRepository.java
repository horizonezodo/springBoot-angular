package com.spring.payload.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.models.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
