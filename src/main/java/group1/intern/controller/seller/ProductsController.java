package group1.intern.controller.seller;

import group1.intern.annotation.PreAuthorizeAllWithoutCustomer;
import group1.intern.bean.ProductDetailColors;
import group1.intern.bean.ProductDetailInfoSeller;
import group1.intern.bean.ProductFilterInfo;
import group1.intern.bean.ToastMessage;
import group1.intern.service.ConstantService;
import group1.intern.service.FilterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import group1.intern.annotation.PreAuthorizeSeller;
import group1.intern.bean.*;
import group1.intern.model.ProductDetail;
import group1.intern.model.ProductImage;
import group1.intern.model.ProductQuantity;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            return "screens/seller/products/index";
        }
    }

    @GetMapping("/{id}/edit")
    public String editProductDetail(@PathVariable("id") Integer id, Model model) {
        try {
            ProductDetailEdit productInfo = productService.getProductDetailEditById(id);
            List<ProductDetailColors> productDetailColors = productService.getProductDetailColors(id);
            List<ProductImage> productImages = productService.getProductImagesByProductDetailId(id);

            model.addAttribute("currentPage", "product-management");
            model.addAttribute("productInfo", productInfo);
            model.addAttribute("productDetailColors", productDetailColors);
            model.addAttribute("productImages", productImages);

            model.addAttribute("availableCategories", productService.findAllCategories());
            model.addAttribute("availableStyles", productService.findAllStyles());
            model.addAttribute("availableMaterials", productService.findAllMaterials());

            return "screens/seller/products/edit";
        } catch (NotFoundObjectException ex) {
            model.addAttribute("toastMessages", new ToastMessage("error", ex.getMessage()));
            return "screens/products/index";
        }
    }

    @GetMapping
    public String getProductManagement(
            @RequestParam(name = "filterStyle", required = false, defaultValue = "-1") String filterStyle,
            @RequestParam(name = "filterMaterial", required = false, defaultValue = "-1") String filterMaterial,
            @RequestParam(name = "queryProduct", required = false, defaultValue = "") String query,
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
                query,
                PageRequest.of(page - 1, size));

        PaginationUtil paginationUtil = new PaginationUtil(
                (int) productFilterPage.getTotalElements(),
                size,
                page,
                5, // Number of pages to show around the current page
                buildQueryString(filterStyle, filterMaterial, query));

        model.addAttribute("totalProducts", (int) productFilterPage.getTotalElements());
        model.addAttribute("productDetails", productFilterPage.getContent());
        model.addAttribute("pagination", paginationUtil);
        model.addAttribute("currentPagination", page);

        model.addAttribute("filterStyle", filterStyle);
        model.addAttribute("filterMaterial", filterMaterial);
        model.addAttribute("queryProduct", query);

        model.addAttribute("styles", constantService.getListConstantsByType("Style"));
        model.addAttribute("materials", constantService.getListConstantsByType("Material"));

        model.addAttribute("currentPage", "product-management");
        return "screens/seller/products/index";
    }

    private String buildQueryString(String filterStyle, String filterMaterial, String query) {
        return UriComponentsBuilder.fromUriString("/seller/products")
                .queryParam("filterStyle", filterStyle.equals("-1") ? null : filterStyle)
                .queryParam("filterMaterial", filterMaterial.equals("-1") ? null : filterMaterial)
                .queryParam("queryProduct", query)
                .build()
                .encode()
                .toUriString();
    }
    @PostMapping("/{id}/update-images")
    public String updateProductImages(
        @PathVariable("id") Integer id,
        @RequestParam("imagesToAdd") List<MultipartFile> imagesToAdd,
        @RequestParam("imagesToRemove") String imagesToRemove,
        Model model) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> imagesToRemoveList;
        try {
            imagesToRemoveList = objectMapper.readValue(imagesToRemove, new TypeReference<List<Integer>>() {});
            productService.updateProductImages(id, imagesToAdd, imagesToRemoveList);
            model.addAttribute("toastMessages", new ToastMessage("success", "Cập nhật hình ảnh thành công!"));
        } catch (Exception e) {
            model.addAttribute("toastMessages", new ToastMessage("error", e.getMessage()));
        }
        return "redirect:/seller/products/"+ id +"/edit";
    }

    @PostMapping("/{id}/update-info")
    public String saveProductEdit(
        @PathVariable("id") Integer id,
        @ModelAttribute("productInfo") ProductDetailEdit productDetailEdit,
        Model model
    ) {
        try {
            ProductDetail productDetail = productService.updateProductInfo(productDetailEdit);
        } catch (Exception e) {
            model.addAttribute("toastMessages", new ToastMessage("error", e.getMessage()));
        }
        return "redirect:/seller/products/"+ id +"/edit";
    }


}
