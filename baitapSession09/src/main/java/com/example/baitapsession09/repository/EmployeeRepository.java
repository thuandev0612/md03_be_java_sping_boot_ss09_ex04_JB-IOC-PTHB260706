package com.example.baitapsession09.repository;

import com.example.baitapsession09.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
