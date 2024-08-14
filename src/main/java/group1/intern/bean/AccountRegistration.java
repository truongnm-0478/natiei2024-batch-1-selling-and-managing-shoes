package group1.intern.bean;

import group1.intern.annotation.PasswordsMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordsMatch
public class AccountRegistration {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có 10 chữ số")
    private String phoneNumber;

    @Size(min = 8, message = "Họ và tên phải có ít nhất 8 ký tự")
    private String fullName;

    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @Size(min = 10, message = "Địa chỉ phải có ít nhất 10 ký tự")
    private String address;
}