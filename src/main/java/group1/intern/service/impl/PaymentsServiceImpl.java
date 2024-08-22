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
import group1.intern.util.constant.CommonConstant;
import group1.intern.util.exception.BadRequestException;
import group1.intern.util.util.CommonUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {
    private final ShoppingCartsRepository shoppingCartRepository;
    private final OrdersRepository ordersRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final ProductQuantitiesRepository productQuantitiesRepository;


    @Override
    public void processPayments(List<ShoppingCartInfo> shoppingCartInfos, PayForm payForm, Account currentAccount, Integer totalPrice) {
        try {
            if (CommonUtils.isEmptyOrNullList(shoppingCartInfos))
                throw new BadRequestException("Không có sản phẩm trong giỏ hàng");

            // Query 1: save order
            var order = ordersRepository.save(Order.builder()
                .account(currentAccount)
                .totalPrice(totalPrice)
                .phoneNumber(payForm.getPhoneNumber())
                .address(payForm.getAddress() + ", " + payForm.getProvince() + ", " + payForm.getDistrict() + ", " + payForm.getWard())
                .status(OrderStatus.WAIT)
                .build());

            for (ShoppingCartInfo cartInfo : shoppingCartInfos) {
                ProductQuantity productQuantity = productQuantitiesRepository.findById(cartInfo.getProductQuantityId())
                    .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));

                // Check quantity
                if (productQuantity.getQuantity() < cartInfo.getQuantity())
                    throw new BadRequestException("Số lượng sản phẩm không đủ");

                // Query 2: save order detail
                orderDetailsRepository.save(OrderDetail.builder()
                    .productQuantity(productQuantity)
                    .price(cartInfo.getPrice())
                    .quantity(cartInfo.getQuantity())
                    .order(order)
                    .build());

                // Query 3: update product quantity
                productQuantity.setQuantity(productQuantity.getQuantity() - cartInfo.getQuantity());
                productQuantitiesRepository.save(productQuantity);

                // Query 4: delete shopping cart if exist
                if (cartInfo.getId() != null)
                    shoppingCartRepository.deleteById(cartInfo.getId());
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Thanh toán thất bại");
        }
    }

    @Override
    public Map<String, ?> singlePayment(int quantityID, Account currrentAccount, int quantity) {
        // Tim quantity product
        ProductQuantity productQuantity = productQuantitiesRepository.findById(quantityID).orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));
        Integer discount = productQuantity.getProductDetail().getDiscount();
        int price = discount != null && discount > 0
            ? (productQuantity.getProductDetail().getPrice() * (100 - discount)) / 100
            : productQuantity.getProductDetail().getPrice();

        List<ShoppingCartInfo> shoppingCartInfos = new ArrayList<>();
        shoppingCartInfos.add(ShoppingCartInfo.builder()
            .size(productQuantity.getSize().getValue())
            .name(productQuantity.getProductDetail().getProduct().getName())
            .style(productQuantity.getProductDetail().getStyle().getValue())
            .price(price)
            .discount(discount)
            .quantity(quantity)
            .productQuantityId(quantityID)
            .build());

        return Map.of(
            CommonConstant.SHOPPING_CART_WRAPPER, new ShoppingCartWrapper(shoppingCartInfos),
            CommonConstant.TOTAL_PRICE, price * quantity
        );
    }
}
