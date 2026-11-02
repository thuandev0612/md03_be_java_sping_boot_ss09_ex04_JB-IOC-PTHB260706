package com.example.baitapsession09.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeCreateDTO {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên không được quá 255 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được quá 255 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không hợp lệ")
    @Pattern(regexp = "^0[35789][0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotNull(message = "Lương không được để trống")
    @Min(value = 5000000, message = "Lương phải từ 5.000.000 đồng")
    private BigDecimal salary;

    @NotNull(message = "Mã phòng ban không được để trống")
    private Long departmentId;
}
