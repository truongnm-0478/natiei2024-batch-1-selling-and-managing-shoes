package group1.intern.model.Enum;

import java.util.Arrays;

public enum OrderStatus {
    WAIT, CONFIRM, REJECT, CANCEL, RECEIVED, DONE;

    public static OrderStatus fromString(String status) {
        return Arrays.stream(OrderStatus.values()).filter(orderStatus -> orderStatus.name().equalsIgnoreCase(status)).findFirst().orElse(null);
    }
}
