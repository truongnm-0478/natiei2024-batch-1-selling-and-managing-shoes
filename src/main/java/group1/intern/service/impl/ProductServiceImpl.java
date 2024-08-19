package group1.intern.service.impl;

import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfo;
import group1.intern.bean.ProductDetailInfoSeller;
import group1.intern.model.ProductDetail;
import group1.intern.model.ProductQuantity;
import group1.intern.repository.ProductDetailRepository;
import group1.intern.repository.customization.ProductDetailsCustomRepository;
import group1.intern.service.ProductService;
import group1.intern.util.CurrencyUtil;
import group1.intern.util.exception.NotFoundObjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductDetailRepository productDetailRepository;
    private final ProductDetailsCustomRepository productDetailCustomRepository;

    @Override
    public ProductDetailInfo getProductDetailById(Integer id) {
        Optional<ProductDetail> productDetailOptional = productDetailCustomRepository.findByIdWithRelationship(id);

        if (productDetailOptional.isEmpty()) {
            throw new NotFoundObjectException("Không tìm thấy sản phẩm với id: " + id);
        }

        ProductDetail productDetail = productDetailOptional.get();

        double price = productDetail.getPrice();
        double discount = productDetail.getDiscount();
        double discountedPrice = price * (1 - discount / 100);

        return ProductDetailInfo.builder()
            .id(productDetail.getId())
            .productId(productDetail.getProduct().getId())
            .name(productDetail.getProduct().getName())
            .discount(productDetail.getDiscount())
            .gender(productDetail.getGender().toString())
            .description(productDetail.getDescription())
            .category(productDetail.getProduct().getCategory().getValue())
            .style(productDetail.getStyle().getValue())
            .material(productDetail.getProduct().getMaterial().getValue())
            .price(CurrencyUtil.formatCurrency(productDetail.getPrice()))
            .color(productDetail.getColor().getValue())
            .images(productDetail.getImages())
            .sizeQuantity(productDetail.getQuantities())
            .discountedPrice(CurrencyUtil.formatCurrency(discountedPrice))
            .build();
    }

    @Override
    public List<ProductDetailColors> getProductDetailColors(Integer id) {
        Optional<ProductDetail> productDetailOptional = productDetailRepository.findById(id);

        if (productDetailOptional.isPresent()) {
            ProductDetail productDetail = productDetailOptional.get();

            List<ProductDetail> relatedProductDetails = productDetailRepository.findByProductId(productDetail.getProduct().getId())
                .stream()
                .filter(pd -> !pd.getId().equals(productDetail.getId()))
                .toList();

            return relatedProductDetails.stream()
                .map(relatedProductDetail -> ProductDetailColors.builder()
                    .id(relatedProductDetail.getId())
                    .productId(relatedProductDetail.getProduct().getId())
                    .color(relatedProductDetail.getColor().getValue())
                    .build())
                .collect(Collectors.toList());
        }

        return null;
    }


    @Override
    public Page<ProductDetail> getProductsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // PageRequest is 0-based
        return productDetailCustomRepository.findByProductName(name, pageable);
    }

    @Override
    public ProductDetailInfoSeller getProductDetailByIdForManager(Integer id) {
        Optional<ProductDetail> productDetailOptional = productDetailCustomRepository.findByIdWithRelationship(id);

        if (productDetailOptional.isEmpty()) {
            throw new NotFoundObjectException("Không tìm thấy sản phẩm với id: " + id);
        }

        ProductDetail productDetail = productDetailOptional.get();

        int totalQuantity = productDetail.getQuantities().stream()
            .mapToInt(ProductQuantity::getQuantity)
            .sum();

        double price = productDetail.getPrice();
        double discount = productDetail.getDiscount();
        double discountedPrice = price * (1 - discount / 100);

        return ProductDetailInfoSeller.builder()
            .id(productDetail.getId())
            .productId(productDetail.getProduct().getId())
            .name(productDetail.getProduct().getName())
            .discount(productDetail.getDiscount())
            .gender(productDetail.getGender().toString())
            .description(productDetail.getDescription())
            .category(productDetail.getProduct().getCategory().getValue())
            .style(productDetail.getStyle().getValue())
            .material(productDetail.getProduct().getMaterial().getValue())
            .price(CurrencyUtil.formatCurrency(productDetail.getPrice()))
            .color(productDetail.getColor().getValue())
            .images(productDetail.getImages())
            .sizeQuantity(productDetail.getQuantities())
            .originPrice(CurrencyUtil.formatCurrency(productDetail.getOriginPrice()))
            .totalQuantity(totalQuantity)
            .discountedPrice(CurrencyUtil.formatCurrency(discountedPrice))
            .build();
    }

}