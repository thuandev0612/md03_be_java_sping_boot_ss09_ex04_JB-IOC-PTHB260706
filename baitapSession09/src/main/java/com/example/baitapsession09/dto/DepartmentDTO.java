package com.example.baitapsession09.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentDTO {
    @NotBlank(message = "Tên phòng ban không được để trống")
    @Size(min = 5, max = 50, message = "Tên phòng ban phải có từ 5 đến 50 ký tự")
    private String name;

    @Size(max = 100, message = "Mô tả không được quá 100 ký tự")
    private String description;
}
