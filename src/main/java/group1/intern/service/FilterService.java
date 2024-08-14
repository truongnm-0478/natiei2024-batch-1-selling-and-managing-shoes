package group1.intern.service;

import group1.intern.bean.ProductFilterInfo;
import group1.intern.model.Constant;

import java.util.List;

public interface FilterService {
    List<ProductFilterInfo> getProductFilterInfos(List<Constant> constants);
}
