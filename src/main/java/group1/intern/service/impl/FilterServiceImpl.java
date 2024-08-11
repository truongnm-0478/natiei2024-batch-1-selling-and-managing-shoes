package group1.intern.service.impl;

import group1.intern.bean.ProductFilterInfo;
import group1.intern.model.Constant;
import group1.intern.model.ProductDetail;
import group1.intern.repository.ProductsCustomRepository;
import group1.intern.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterServiceImpl implements FilterService {
    @Autowired
    private ProductsCustomRepository repo;

    @Override
    public List<ProductFilterInfo> getProductFilterInfos(List<Constant> constants) {
        List<ProductDetail> productDetails = repo.findProductByFilter(constants);
        return productDetails.stream()
            .map(productDetail -> ProductFilterInfo.builder()
                .id(productDetail.getId())
                .productId(productDetail.getProduct().getId())
                .name(productDetail.getProduct().getName())
                .discount(productDetail.getDiscount())
                .gender(productDetail.getGender().toString())
                .description(productDetail.getDescription())
                .category(productDetail.getProduct().getCategory().getValue())
                .style(productDetail.getStyle().getValue())
                .material(productDetail.getProduct().getMaterial().getValue())
                .price(productDetail.getPrice())
                .color(productDetail.getColor().getValue())
                .images(productDetail.getImages())
                .sizeQuantity(productDetail.getQuantities())
                .build())
            .collect(Collectors.toList());
    }
}
