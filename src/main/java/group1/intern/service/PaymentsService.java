package group1.intern.service;

import group1.intern.bean.PayForm;
import group1.intern.bean.ShoppingCartInfo;
import group1.intern.model.Account;

import java.util.List;
import java.util.Map;

public interface PaymentsService {
    void processPayments(List<ShoppingCartInfo> shoppingCartInfos, PayForm payForm, Account currentAccount, Integer totalPrice);

    Map<String, ?> singlePayment(int quantityID, Account currrentAccount, int quantity);
}
