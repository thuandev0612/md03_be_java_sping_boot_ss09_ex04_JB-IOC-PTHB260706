package com.example.baitapsession09.controller;

import com.example.baitapsession09.dto.DepartmentDTO;
import com.example.baitapsession09.model.Department;
import com.example.baitapsession09.response.ApiResponse;
import com.example.baitapsession09.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Department>> createDepartment(@Valid @RequestBody DepartmentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("SUCCESS", "Tạo phòng ban thành công", departmentService.createDepartment(dto)));
    }
}
