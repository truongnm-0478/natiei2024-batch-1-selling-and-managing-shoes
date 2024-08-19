package group1.intern.bean;

import group1.intern.model.Enum.OrderStatus;
import group1.intern.model.Order;
import group1.intern.util.CurrencyUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInfo {
    private Integer id;
    private String totalPrice;
    private String phoneNumber;
    private String address;
    private String status;
    private AccountInfo account;
    private List<OrderDetailInfo> orderDetailInfos;

    public static OrderInfo fromEntity(Order order) {
        return OrderInfo.builder()
            .id(order.getId())
            .totalPrice(CurrencyUtil.formatCurrency(order.getTotalPrice()))
            .phoneNumber(order.getPhoneNumber())
            .address(order.getAddress())
            .status(OrderInfo.getStatus(order.getStatus()))
            .account(AccountInfo.fromAccount(order.getAccount()))
            .orderDetailInfos(order.getOrderDetails().stream().map(OrderDetailInfo::fromEntity).toList())
            .build();
    }

    private static String getStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case WAIT -> "Chờ xác nhận";
            case CONFIRM -> "Đang giao hàng";
            case REJECT -> "Bị từ chối";
            case CANCEL -> "Đã hủy";
            case RECEIVED -> "Đã nhận";
            case DONE -> "Giao hàng thành công";
        };
    }

    public boolean isDisabled() {
        return status.equals("Bị từ chối") || status.equals("Đã hủy");
    }

}
