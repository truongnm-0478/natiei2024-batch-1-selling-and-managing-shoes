package group1.intern.controller.seller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller("sellerProductsController")
@PreAuthorizeSeller
@RequestMapping("/seller/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

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
    public String showEditProductDetail(@PathVariable("id") Integer id, Model model) {
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
