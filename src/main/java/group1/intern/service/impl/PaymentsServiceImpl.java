package group1.intern.service.impl;

import group1.intern.bean.PayForm;
import group1.intern.bean.ShoppingCartInfo;
import group1.intern.bean.ShoppingCartWrapper;
import group1.intern.model.Account;
import group1.intern.model.Enum.OrderStatus;
import group1.intern.model.Order;
import group1.intern.model.OrderDetail;
import group1.intern.model.ProductQuantity;
import group1.intern.repository.OrderDetailsRepository;
import group1.intern.repository.OrdersRepository;
import group1.intern.repository.ProductQuantitiesRepository;
import group1.intern.repository.ShoppingCartsRepository;
import group1.intern.service.PaymentsService;
import group1.intern.util.util.WebUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PaymentsServiceImpl implements PaymentsService {
    @Autowired
    private ShoppingCartsRepository shoppingCartRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private ProductQuantitiesRepository productQuantitiesRepository;


    @Override
    public void processPayments(List<ShoppingCartInfo> shoppingCartInfos, PayForm payForm, Account currentAccount, Integer totalPrice) {

        try {
            // Tạo mới order
            Order order = Order.builder()
                .account(currentAccount)
                .totalPrice(totalPrice)
                .phoneNumber(payForm.getPhoneNumber())
                .address(payForm.getAddress() + ", " + payForm.getProvince() + ", " + payForm.getDistrict() + ", " + payForm.getWard())
                .status(OrderStatus.WAIT)
                .build();
            ordersRepository.save(order);
            for (ShoppingCartInfo cartInfo : shoppingCartInfos) {
                // Tìm quantities product
                ProductQuantity productQuantity = productQuantitiesRepository.findById(cartInfo.getProductQuantityId())
                    .orElseThrow(() -> new RuntimeException("Product quantity not found"));

                // Tạo order details
                orderDetailsRepository.save(OrderDetail.builder()
                    .productQuantity(productQuantity)
                    .price(cartInfo.getPrice())
                    .quantity(cartInfo.getQuantity())
                    .order(order)
                    .build());

                // Cập nhật số lượng sản phẩm
                if (productQuantity.getQuantity() < cartInfo.getQuantity()) {
                    throw new RuntimeException("Insufficient product quantity");
                }
                productQuantity.setQuantity(productQuantity.getQuantity() - cartInfo.getQuantity());
                productQuantitiesRepository.save(productQuantity);

                // Xóa trong shopping cart
                if (cartInfo.getId() != null) {
                    shoppingCartRepository.deleteById(cartInfo.getId());
                }
            }
        } catch (Exception e) {
            // Nếu có lỗi, ném ra một runtime exception để Spring rollback transaction
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void singlePayment(int quantityID, Account currrentAccount, int quantity) {
        //Tim quantity product
        ProductQuantity productQuantity = productQuantitiesRepository.findById(quantityID).orElse(null);
        List<ShoppingCartInfo> shoppingCartInfos = new ArrayList<>();
        shoppingCartInfos.add(ShoppingCartInfo.builder()
            .size(productQuantity.getSize().getValue())
            .name(productQuantity.getProductDetail().getProduct().getName())
            .style(productQuantity.getProductDetail().getStyle().getValue())
            .price(productQuantity.getProductDetail().getPrice())
            .quantity(quantity)
            .productQuantityId(quantityID)
            .build());
        WebUtils.Sessions.setAttribute("shoppingCartWrapper", new ShoppingCartWrapper(shoppingCartInfos));
        WebUtils.Sessions.setAttribute("totalPrice", productQuantity.getProductDetail().getPrice());
    }
}
