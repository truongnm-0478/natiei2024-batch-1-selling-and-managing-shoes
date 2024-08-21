package group1.intern.controller.seller;

import group1.intern.annotation.PreAuthorizeAllWithoutCustomer;
import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfoSeller;
import group1.intern.bean.ProductFilterInfo;
import group1.intern.bean.ToastMessage;
import group1.intern.service.ConstantService;
import group1.intern.service.FilterService;
import group1.intern.service.ProductService;
import group1.intern.util.exception.NotFoundObjectException;
import group1.intern.util.util.PaginationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Controller("sellerProductsController")
@PreAuthorizeAllWithoutCustomer
@RequestMapping("/seller/products")
public class ProductsController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ConstantService constantService;

    @Autowired
    private FilterService filterService;

    @GetMapping("/{id}")
    public String showProductDetailSeller(@PathVariable("id") Integer id, Model model) {
        try {
            ProductDetailInfoSeller productInfo = productService.getProductDetailByIdForManager(id);
            List<ProductDetailColors> productDetailColors = productService.getProductDetailColors(id);
            model.addAttribute("currentPage", "product-management");

            model.addAttribute("productInfo", productInfo);
            model.addAttribute("productDetailColors", productDetailColors);
            return "screens/seller/products/show";
        } catch (NotFoundObjectException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", ex.getMessage()));
            return "screens/products/index";
        }
    }

    @GetMapping
    public String getProductManagement(
            @RequestParam(name = "filterStyle", required = false, defaultValue = "-1") String filterStyle,
            @RequestParam(name = "filterMaterial", required = false, defaultValue = "-1") String filterMaterial,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "5") int size,
            Model model) {

        List<Integer> filterStyles = new ArrayList<>();
        List<Integer> filterMaterials = new ArrayList<>();
        
        if(!filterStyle.isEmpty() && Integer.parseInt(filterStyle) != -1){
            filterStyles.add(Integer.parseInt(filterStyle));
        }
        if(!filterMaterial.isEmpty() && Integer.parseInt(filterMaterial) != -1){
            filterMaterials.add(Integer.parseInt(filterMaterial));
        }

        Page<ProductFilterInfo> productFilterPage = filterService.getProductFilterInfos(
                filterStyles,
                new ArrayList<>(),
                filterMaterials,
                new ArrayList<>(),
                1,
                PageRequest.of(page - 1, size));

        PaginationUtil paginationUtil = new PaginationUtil(
                (int) productFilterPage.getTotalElements(),
                size,
                page,
                5, // Number of pages to show around the current page
                buildQueryString(filterStyle, filterMaterial));

        System.out.println(filterStyles);

        model.addAttribute("totalPages", (int) productFilterPage.getTotalElements());
        model.addAttribute("productDetails", productFilterPage.getContent());
        model.addAttribute("pagination", paginationUtil);
        model.addAttribute("currentPagination", page);

        model.addAttribute("filterStyle", filterStyle);
        model.addAttribute("filterMaterial", filterMaterial);

        model.addAttribute("styles", constantService.getListConstantsByType("Style"));
        model.addAttribute("materials", constantService.getListConstantsByType("Material"));

        model.addAttribute("currentPage", "product-management");
        return "screens/seller/products/index";
    }

    private String buildQueryString(String filterStyle, String filterMaterial) {
        return UriComponentsBuilder.fromUriString("/seller/products")
                .queryParam("filterStyle", filterStyle.equals("-1") ? null : filterStyle)
                .queryParam("filterMaterial", filterMaterial.equals("-1") ? null : filterMaterial)
                .build()
                .encode()
                .toUriString();
    }
}
