package group1.intern.bean;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileInfo {
    private Integer accountId;

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = ".+@.+\\..+", message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Tên tài khoản không được để trống")
    private String displayName;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    @Size(min = 10, message = "Địa chỉ cần phải lớn hơn 10 kí tự")
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có 10 chữ số")
    private String phoneNumber;

    //    @NotBlank(message = "Giới tính không được để trống")
    private Boolean gender;

    private String avatarUrl;
}
