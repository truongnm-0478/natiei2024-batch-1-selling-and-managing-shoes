package group1.intern.service.impl;

import group1.intern.bean.ProductFilterInfo;
import group1.intern.model.ProductDetail;
import group1.intern.repository.customization.ProductDetailsCustomRepository;
import group1.intern.service.FilterService;
import lombok.RequiredArgsConstructor;
import group1.intern.model.ProductQuantity;
import group1.intern.util.util.CommonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {
        @Autowired
        private ProductDetailsCustomRepository repo;

        @Override
        public Page<ProductFilterInfo> getProductFilterInfos(
                        List<Integer> listStyleId,
                        List<Integer> listCategoryId,
                        List<Integer> listMaterialId,
                        List<Integer> listColorId,
                        int genderFilter,
                        Pageable pageable) {

                Page<ProductDetail> productDetailsPage = repo.findProductByFilter(
                                listStyleId,
                                listCategoryId,
                                listMaterialId,
                                listColorId,
                                genderFilter,
                                pageable);

                return productDetailsPage.map(productDetail -> ProductFilterInfo.builder()
                                .id(productDetail.getId())
                                .productId(productDetail.getProduct().getId())
                                .name(productDetail.getProduct().getName())
                                .discount(productDetail.getDiscount())
                                .gender(productDetail.getGender().toString())
                                .description(productDetail.getDescription())
                                .category(productDetail.getProduct().getCategory().getValue())
                                .style(productDetail.getStyle().getValue())
                                .material(productDetail.getProduct().getMaterial().getValue())
                                .price(CommonUtils.formatToVND(productDetail.getPrice()))
                                .discountPrice(CommonUtils.formatToVND(productDetail.getPrice()
                                                * (100 - productDetail.getDiscount()) / 100))
                                .color(productDetail.getColor().getValue())
                                .images(productDetail.getImages())
                                .sumQuantity(productDetail.getQuantities().stream()
                                                .mapToInt(ProductQuantity::getQuantity)
                                                .sum())
                                .build());
        }
}
