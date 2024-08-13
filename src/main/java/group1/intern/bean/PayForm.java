package group1.intern.bean;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayForm {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có 10 chữ số")
    private String phoneNumber;

    @Pattern(regexp = ".+@.+\\..+", message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Tỉnh/ Thành phố không được để trống")
    private String province;

    @NotBlank(message = "Quận/ Huyện không được để trống")
    private String district;

    @NotBlank(message = "Phường/ Xã không được để trống")
    private String ward;

    private Boolean isNotification;

    @NotBlank(message = "Vui lòng chọn phương thức giao hàng")
    private String shippingType;

    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    private String paymentType;
}
