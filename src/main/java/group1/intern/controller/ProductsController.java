package group1.intern.controller;

import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfo;
import group1.intern.bean.ToastMessage;
import group1.intern.bean.CartForm;
import group1.intern.service.ProductService;
import group1.intern.util.exception.NotFoundObjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import group1.intern.bean.ProductFilterInfo;
import group1.intern.service.ConstantService;
import group1.intern.service.FilterService;
import group1.intern.util.util.PaginationUtil;

@Controller
public class ProductsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private FilterService filterService;

    @GetMapping("/products")
    public String index(
            @RequestParam(name = "filterStyleString", required = false, defaultValue = "") String filterStyleString,
            @RequestParam(name = "filterCategoryString", required = false, defaultValue = "") String filterCategoryString,
            @RequestParam(name = "filterMaterialString", required = false, defaultValue = "") String filterMaterialString,
            @RequestParam(name = "gender", required = false, defaultValue = "1") Integer filterGender,
            @RequestParam(name = "filterColorString", required = false, defaultValue = "") String filterColorString,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size,
            Model model) {

        List<Integer> filterStyles = new ArrayList<>();
        List<Integer> filterCategories = new ArrayList<>();
        List<Integer> filterMaterial = new ArrayList<>();
        List<Integer> filterColors = new ArrayList<>();

        if (!filterStyleString.isEmpty()) {
            filterStyles = Arrays.stream(filterStyleString.split(",\\s*"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        if (!filterCategoryString.isEmpty()) {
            filterCategories = Arrays.stream(filterCategoryString.split(",\\s*"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        if (!filterMaterialString.isEmpty()) {
            filterMaterial = Arrays.stream(filterMaterialString.split(",\\s*"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        if (!filterColorString.isEmpty()) {
            filterColors = Arrays.stream(filterColorString.split(",\\s*"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        Page<ProductFilterInfo> productFilterPage = filterService.getProductFilterInfos(
                filterStyles,
                filterCategories,
                filterMaterial,
                filterColors,
                filterGender,
                PageRequest.of(page - 1, size));

        PaginationUtil paginationUtil = new PaginationUtil(
                (int) productFilterPage.getTotalElements(),
                size,
                page,
                5, // Number of pages to show around the current page
                buildQueryString(filterStyleString, filterCategoryString, filterMaterialString,
                        filterColorString, filterGender));

        model.addAttribute("productDetails", productFilterPage.getContent());
        model.addAttribute("pagination", paginationUtil);
        model.addAttribute("currentPage", page);

        model.addAttribute("filterStyles", filterStyleString);
        model.addAttribute("filterCategories", filterCategoryString);
        model.addAttribute("filterMaterials", filterMaterialString);
        model.addAttribute("filterColors", filterColorString);
        model.addAttribute("filterGender", filterGender);

        model.addAttribute("styles", constantService.getListConstantsByType("Style"));
        model.addAttribute("colors", constantService.getListConstantsByType("Color"));
        model.addAttribute("materials", constantService.getListConstantsByType("Material"));
        model.addAttribute("categories", constantService.getListConstantsByType("Category"));

        return "screens/products/index";
    }

    private String buildQueryString(String filterStyleString, String filterCategoryString, String filterMaterialString, String filterColorString,
            Integer gender) {
        return UriComponentsBuilder.fromUriString("/products")
                .queryParam("filterStyleString", filterStyleString)
                .queryParam("filterCategoryString", filterCategoryString)
                .queryParam("filterMaterialString", filterMaterialString)
                .queryParam("filterColorString", filterColorString)
                .queryParam("gender", gender)
                .build()
                .encode()
                .toUriString();
    }

    @GetMapping("/products/{id}")
    public String showProductDetail(@PathVariable("id") Integer id, Model model) {
        try {
            ProductDetailInfo productInfo = productService.getProductDetailById(id);
            List<ProductDetailColors> productDetailColors = productService.getProductDetailColors(id);
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("productDetailInfoList", productDetailColors);
            model.addAttribute("cartForm", new CartForm());
            return "screens/products/show";
        } catch (NotFoundObjectException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", ex.getMessage()));
            return "screens/products/index";
        }
    }
}
