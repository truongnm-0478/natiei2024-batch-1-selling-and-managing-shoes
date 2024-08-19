package group1.intern.service;

import group1.intern.bean.ProductFilterInfo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterService {
    Page<ProductFilterInfo> getProductFilterInfos(
            List<Integer> listStyleId,
            List<Integer> listCategoryId,
            List<Integer> listMaterialId,
            List<Integer> listColorId,
            int genderFilter,
            Pageable pageable);
}
