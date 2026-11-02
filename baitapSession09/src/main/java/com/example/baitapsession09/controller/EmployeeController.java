package com.example.baitapsession09.controller;

import com.example.baitapsession09.dto.EmployeeCreateDTO;
import com.example.baitapsession09.model.Employee;
import com.example.baitapsession09.response.ApiResponse;
import com.example.baitapsession09.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Employee>> createEmployee(@Valid @RequestBody EmployeeCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("SUCCESS", "Thêm nhân viên thành công", employeeService.createEmployee(dto)));
    }
}
