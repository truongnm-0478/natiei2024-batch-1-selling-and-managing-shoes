package group1.intern.service.impl;

import group1.intern.bean.ProductInfo;
import group1.intern.model.Product;
import group1.intern.model.ProductDetail;
import group1.intern.repository.ProductRepository;
import group1.intern.service.ProductService;
import group1.intern.util.CurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

//    @Override
//    public ProductInfo getProductById(Integer id) {
//        Optional<Product> productOpt = productRepository.findById(id);
//
//        if (productOpt.isPresent()) {
//            Product product = productOpt.get();
//            ProductDetail productDetail = product.getProductDetails().get(0);
//
//            return ProductInfo.builder()
//                .name(product.getName())
//                .price(CurrencyUtil.formatCurrency(productDetail.getPrice()))
//                .size(productDetail.getSize().getValue())
//                .quantity(productDetail.getQuantity())
//                .discount(product.getDiscount())
//                .gender(product.getGender().toString())
//                .description(product.getDescription())
//                .category(product.getCategory().getValue())
//                .style(product.getStyle().getValue())
//                .material(product.getMaterial().getValue())
//                .color(productDetail.getColor().getValue())
//                .images(product.getImages())
//                .build();
//        }
//        return null;
//    }

    @Override
    public ProductInfo getProductById(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            List<ProductDetail> productDetails = product.getProductDetails();

            Map<String, Integer> sizeQuantity = productDetails.stream()
                .collect(Collectors.groupingBy(
                    detail -> detail.getSize().getValue(),
                    Collectors.summingInt(ProductDetail::getQuantity)
                ));

            // Get distinct colors
            List<String> colors = productDetails.stream()
                .map(detail -> detail.getColor().getValue())
                .distinct()
                .collect(Collectors.toList());

            ProductDetail productDetail = productDetails.get(0);

            return ProductInfo.builder()
                .id(product.getId())
                .name(product.getName())
                .price(CurrencyUtil.formatCurrency(productDetail.getPrice()))
                .sizeQuantity(sizeQuantity)
                .colors(colors)
                .discount(product.getDiscount())
                .gender(product.getGender().toString())
                .description(product.getDescription())
                .category(product.getCategory().getValue())
                .style(product.getStyle().getValue())
                .material(product.getMaterial().getValue())
                .images(product.getImages())
                .build();
        }
        return null;
    }

}
