package group1.intern.service;

import group1.intern.bean.PayForm;
import group1.intern.bean.ShoppingCartInfo;
import group1.intern.model.Account;

import java.util.List;

public interface PaymentsService {
    public void processPayments(List<ShoppingCartInfo> shoppingCartInfos, PayForm payForm, Account currentAccount, Integer totalPrice);

    public void singlePayment(int quantityID, Account currrentAccount, int quantity);
}
