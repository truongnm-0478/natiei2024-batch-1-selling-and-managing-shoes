package group1.intern.controller.customer;

import group1.intern.annotation.CurrentAccount;
import group1.intern.annotation.PreAuthorizeCustomer;
import group1.intern.bean.OrderInfo;
import group1.intern.model.Account;
import group1.intern.model.Enum.OrderStatus;
import group1.intern.service.OrdersService;
import group1.intern.util.util.CommonUtils;
import group1.intern.util.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@PreAuthorizeCustomer
public class OrdersController {
    private final OrdersService ordersService;

    @GetMapping
    public String index(
        @RequestParam(value = "status", required = false) String status,
        Model model,
        @CurrentAccount Account account,
        @RequestParam(name = "page", required = false, defaultValue = "1") int page
    ) {
        var statusEnum = CommonUtils.isNotEmptyOrNullString(status)
            ? Arrays.stream(OrderStatus.values()).filter(e -> e.name().equalsIgnoreCase(status)).findFirst().orElse(null)
            : null;
        // create page request
        var pageRequest = PageRequest.of(page - 1, 20, Sort.by(Sort.Order.desc("createdAt")));
        var orderInfoPage = ordersService.getOrders(account.getId(), statusEnum, pageRequest);

        // create pagination helper
        List<OrderInfo> orderInfos = orderInfoPage.getContent();
        PaginationUtil paginationHelper = new PaginationUtil((int) orderInfoPage.getTotalElements(), 20, page, 5, buildQueryString());
        model.addAttribute("paginationHelper", paginationHelper);
        model.addAttribute("page", page);
        model.addAttribute("orderInfos", orderInfos);
        return "screens/customer/orders/index";
    }
    private String buildQueryString() {
        return UriComponentsBuilder.fromUriString("/order")
                .build()
                .encode()
                .toUriString();
    }
}
