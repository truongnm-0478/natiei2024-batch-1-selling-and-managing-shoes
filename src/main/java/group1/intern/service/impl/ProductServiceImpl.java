package group1.intern.service.impl;

import group1.intern.bean.*;
import group1.intern.model.*;
import group1.intern.model.Embeddables.ProductDescription;
import group1.intern.model.Enum.ProductGender;
import group1.intern.repository.*;
import group1.intern.repository.customization.ProductDetailsCustomRepository;
import group1.intern.repository.customization.ProductsCustomRepository;
import group1.intern.service.CloudinaryService;
import group1.intern.service.ExcelService;
import group1.intern.service.ProductService;
import group1.intern.util.CurrencyUtil;
import group1.intern.util.exception.NotFoundObjectException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductDetailRepository productDetailRepository;
    private final ProductDetailsCustomRepository productDetailCustomRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductImagesRepository productImagesRepository;
    private final ConstantsRepository constantsRepository;
    private final ProductQuantitiesRepository productQuantitiesRepository;
    private final ProductsRepository productsRepository;
    private final ProductsCustomRepository productsCustomRepository;
    private final ExcelService excelService;

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

    @Override
    public ProductDetailEdit getProductDetailEditById(Integer id) {
        Optional<ProductDetail> productDetailOptional = productDetailCustomRepository.findByIdWithRelationship(id);

        if (productDetailOptional.isEmpty()) {
            throw new NotFoundObjectException("Không tìm thấy sản phẩm với id: " + id);
        }

        ProductDetail productDetail = productDetailOptional.get();

        return ProductDetailEdit.builder()
            .id(productDetail.getId())
            .productId(productDetail.getProduct().getId())
            .name(productDetail.getProduct().getName())
            .discount(productDetail.getDiscount())
            .gender(productDetail.getGender().toString())
            .category(productDetail.getProduct().getCategory().getValue())
            .style(productDetail.getStyle().getValue())
            .material(productDetail.getProduct().getMaterial().getValue())
            .price(productDetail.getPrice())
            .originPrice(productDetail.getOriginPrice())
            .color(productDetail.getColor().getValue())
            .sizeQuantity(productDetail.getQuantities())
            .build();
    }

    @Override
    public List<ProductImage> getProductImagesByProductDetailId(Integer id) {
        Optional<ProductDetail> productDetailOptional = productDetailCustomRepository.findByIdWithRelationship(id);

        if (productDetailOptional.isEmpty()) {
            throw new NotFoundObjectException("Không tìm thấy sản phẩm với id: " + id);
        }

        return productDetailOptional.get().getImages();
    }

    @Override
    public void updateProductImages(Integer productDetailId, List<MultipartFile> imagesToAdd, List<Integer> imagesToRemove) {
        Optional<ProductDetail> productDetailOptional = productDetailCustomRepository.findByIdWithRelationship(productDetailId);

        if (productDetailOptional.isEmpty()) {
            throw new NotFoundObjectException("Không tìm thấy sản phẩm với id: " + productDetailId);
        }

        ProductDetail productDetail = productDetailOptional.get();


        for (Integer imageId : imagesToRemove) {
            Optional<ProductImage> imageOptional = productImagesRepository.findById(imageId);
            if (imageOptional.isPresent()) {
                ProductImage image = imageOptional.get();

                // Nếu URL chứa "cloudinary" thì xoá img ở Cloudinary
                if (image.getUrl().contains("cloudinary")) {
                    cloudinaryService.deleteFileByUrl(image.getUrl());
                }

                productImagesRepository.delete(image);
            }
        }

        for (MultipartFile image : imagesToAdd) {
            if (!image.isEmpty()) {

                Map<String, Object> uploadResult = cloudinaryService.uploadFile(image);
                String imageUrl = (String) uploadResult.get("url");
                String publicId = (String) uploadResult.get("public_id");

                ProductImage productImage = ProductImage.builder()
                    .productDetail(productDetail)
                    .url(imageUrl)
                    .build();

                productImagesRepository.save(productImage);

            }
        }

    }

    @Override
    public List<String> findAllCategories() {
        return constantsRepository.findAllCategories();
    }

    @Override
    public List<String> findAllStyles() {
        return constantsRepository.findAllStyles();
    }

    @Override
    public List<String> findAllMaterials() {
        return constantsRepository.findAllMaterials();
    }

    @Override
    public ProductDetail updateProductInfo(ProductDetailEdit productDetailEdit) {
        Optional<ProductDetail> optionalProductDetail = productDetailCustomRepository.findByIdWithRelationship(productDetailEdit.getId());
        if (optionalProductDetail.isEmpty()) {
            throw new NotFoundObjectException("Không tìm thấy sản phẩm với id: " + productDetailEdit.getId());
        }
        ProductDetail productDetail = optionalProductDetail.get();

        // Update style
        Constant style = constantsRepository.findByValue(productDetailEdit.getStyle());
        if (style == null) {
            style = Constant.builder()
                .type("Style")
                .value(productDetailEdit.getStyle())
                .build();
            constantsRepository.save(style);
        }

        // Update category
        Constant category = constantsRepository.findByValue(productDetailEdit.getCategory());
        if (category == null) {
            category = Constant.builder()
                .type("Category")
                .value(productDetailEdit.getCategory())
                .build();
            constantsRepository.save(category);
        }

        // Update color
        Constant color = constantsRepository.findByValue(productDetailEdit.getColor());
        if (color == null) {
            color = Constant.builder()
                .type("Color")
                .value(productDetailEdit.getColor())
                .build();
            constantsRepository.save(color);
        }

        // Update material
        Constant material = constantsRepository.findByValue(productDetailEdit.getMaterial());
        if (material == null) {
            material = Constant.builder()
                .type("Material")
                .value(productDetailEdit.getMaterial())
                .build();
            constantsRepository.save(material);
        }

        // Update Product Info
        Product product = productDetail.getProduct();
        product.setName(productDetailEdit.getName());
        product.setCategory(category);
        product.setMaterial(material);
        productsRepository.save(product);

        // Update Product Detail Info
        productDetail.setProduct(product);
        productDetail.setOriginPrice(productDetailEdit.getOriginPrice());
        productDetail.setPrice(productDetailEdit.getPrice());
        productDetail.setDiscount(productDetailEdit.getDiscount());
        productDetail.setGender(ProductGender.valueOf(productDetailEdit.getGender()));
        productDetail.setStyle(style);
        productDetail.setColor(color);

        // Update quantity without altering size ID
        List<ProductQuantity> existingProductQuantities = productQuantitiesRepository.findAllByProductDetail(productDetail);
        List<ProductQuantity> editProductQuantities = productDetailEdit.getSizeQuantity();

        for (ProductQuantity existingQuantity : existingProductQuantities) {
            ProductQuantity matchingQuantity = editProductQuantities.stream()
                .filter(pq -> pq.getSize().getId().equals(existingQuantity.getSize().getId()))
                .findFirst()
                .orElse(null);

            if (matchingQuantity != null) {
                existingQuantity.setQuantity(matchingQuantity.getQuantity());
                productQuantitiesRepository.save(existingQuantity);

                editProductQuantities.remove(matchingQuantity);
            }
        }

        return productDetailRepository.save(productDetail);
    }

    @Override
    @Transactional
    public List<ProductExcel> importProducts(MultipartFile file) throws Exception {

        List<ProductExcel> productExcels = excelService.readerExcelFile(file, ProductExcel.class);
        List<ProductDetail> productDetails = productDetailCustomRepository.findAllWithRelationship();

        // Duyệt qua từng sản phẩm trong danh sách productExcels
        for (ProductExcel productExcel : productExcels) {
            // Tìm kiếm product detail tương ứng
            ProductDetail existingProductDetail = productDetails.stream()
                .filter(productDetail -> {
                    Product product = productDetail.getProduct();
                    return product.getName().equalsIgnoreCase(productExcel.getName()) &&
                        product.getCategory().getValue().equalsIgnoreCase(productExcel.getCategory()) &&
                        product.getMaterial().getValue().equalsIgnoreCase(productExcel.getMaterial()) &&
                        productDetail.getGender().toString().equalsIgnoreCase(productExcel.getGender()) &&
                        productDetail.getColor().getValue().equalsIgnoreCase(productExcel.getColor()) &&
                        productDetail.getStyle().getValue().equalsIgnoreCase(productExcel.getStyle());
                })
                .findFirst()
                .orElse(null);

            if (existingProductDetail != null) {
                String[] quantities = productExcel.getQuantity().split(", ");
                List<Integer> quantityList = Arrays.stream(quantities)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
                updateProductQuantities(existingProductDetail, quantityList);
            } else {
                addNewProductDetail(productExcel);
            }
        }

        return productExcels;
    }

    private void updateProductQuantities(ProductDetail existingProductDetail, List<Integer> quantityList) {
        List<ProductQuantity> existingQuantities = existingProductDetail.getQuantities();

        // Kiểm tra nếu danh sách quantityList lớn hơn số lượng hiện có thì điều chỉnh lại
        if (quantityList.size() != existingQuantities.size()) {
            throw new IllegalArgumentException("Kích thước danh sách quantityList không khớp với số lượng kích thước hiện có.");
        }

        // Cập nhật số lượng cho từng ProductQuantity
        for (int i = 0; i < existingQuantities.size(); i++) {
            ProductQuantity productQuantity = existingQuantities.get(i);
            int newQuantity = quantityList.get(i);
            int updatedQuantity = productQuantity.getQuantity() + newQuantity;
            productQuantity.setQuantity(updatedQuantity);
        }

        // Lưu thay đổi vào database
        productQuantitiesRepository.saveAll(existingQuantities);
    }

    private void addNewProductDetail(ProductExcel productExcel) {

        List<Constant> constants = constantsRepository.findAll();

        List<Constant> sizeConstants = constants.stream()
            .filter(constant -> "Size".equalsIgnoreCase(constant.getType()))
            .collect(Collectors.toList());

        List<Constant> colorConstants = constants.stream()
            .filter(constant -> "Color".equalsIgnoreCase(constant.getType()))
            .collect(Collectors.toList());


        List<Constant> styleConstants = constants.stream()
            .filter(constant -> "Style".equalsIgnoreCase(constant.getType()))
            .collect(Collectors.toList());


        List<Constant> categoryConstants = constants.stream()
            .filter(constant -> "Category".equalsIgnoreCase(constant.getType()))
            .collect(Collectors.toList());


        List<Constant> materialConstants = constants.stream()
            .filter(constant -> "Material".equalsIgnoreCase(constant.getType()))
            .collect(Collectors.toList());

        List<String> imageDescriptionUrls = List.of("https://ananas.vn/wp-content/uploads/Ananas_SizeChart.jpg");
        ProductDescription productDescription = ProductDescription.builder()
            .images(getImageUrls(imageDescriptionUrls))
            .build();

        // Tìm hoặc tạo sản phẩm
        List<Product> products = productsCustomRepository.findAllWithRelationship();
        Product existingProduct = products.stream()
            .filter(p -> p.getName().equalsIgnoreCase(productExcel.getName()) &&
                p.getCategory().getValue().equalsIgnoreCase(productExcel.getCategory()) &&
                p.getMaterial().getValue().equalsIgnoreCase(productExcel.getMaterial()))
            .findFirst()
            .orElse(null);

        if (existingProduct == null) {
            existingProduct = Product.builder()
                .name(productExcel.getName())
                .category(categoryConstants.stream().filter(c -> c.getValue().equalsIgnoreCase(productExcel.getCategory())).findFirst().orElse(null))
                .material(materialConstants.stream().filter(c -> c.getValue().equalsIgnoreCase(productExcel.getMaterial())).findFirst().orElse(null))
                .build();
            productsRepository.save(existingProduct);
        }

        // Xử lý màu sắc
        Constant color = colorConstants.stream()
            .filter(c -> c.getValue().equalsIgnoreCase(productExcel.getColor()))
            .findFirst()
            .orElseGet(() -> {
                // Nếu không tìm thấy, tạo một Constant mới
                Constant newColor = Constant.builder()
                    .type("Color")
                    .value(productExcel.getColor())
                    .build();
                constantsRepository.save(newColor);
                return newColor;
            });

        // Xử lý kiểu dáng
        Constant style = styleConstants.stream()
            .filter(c -> c.getValue().equalsIgnoreCase(productExcel.getStyle()))
            .findFirst()
            .orElseGet(() -> {
                // Nếu không tìm thấy, tạo một Constant mới
                Constant newStyle = Constant.builder()
                    .type("Style")
                    .value(productExcel.getStyle())
                    .build();
                constantsRepository.save(newStyle);
                return newStyle;
            });

        // Tạo ProductDetail
        ProductDetail newProductDetail = ProductDetail.builder()
            .originPrice(productExcel.getOriginPrice())
            .discount(0)
            .price(productExcel.getOriginPrice() * 10 / 100 + productExcel.getOriginPrice())
            .gender(ProductGender.valueOf(productExcel.getGender().toUpperCase()))
            .description(productDescription)
            .product(existingProduct)
            .color(color)
            .style(style)
            .build();
        productDetailRepository.save(newProductDetail);


        // Xử lý số lượng và kích thước
        List<Integer> quantities = Arrays.stream(productExcel.getQuantity().split(", "))
            .map(Integer::parseInt)
            .collect(Collectors.toList());

        // Duyệt qua tất cả các size để lưu quantity tương ứng
        for (int i = 0; i < sizeConstants.size(); i++) {
            Constant size = sizeConstants.get(i);
            int quantity = quantities.size() > i ? quantities.get(i) : 0;

            ProductQuantity productQuantity = ProductQuantity.builder()
                .quantity(quantity)
                .productDetail(newProductDetail)
                .size(size)
                .build();

            productQuantitiesRepository.save(productQuantity);
        }

        // Xử lý hình ảnh
        List<String> imageUrls = Arrays.asList(productExcel.getUrl().split(", "));
        List<ProductImage> productImages = new ArrayList<>();

        for (String imageUrl : imageUrls) {
             // Nếu hình ảnh chưa tồn tại, tạo mới
            ProductImage productImage = ProductImage.builder()
                .url(imageUrl)
                .productDetail(newProductDetail)
                .build();
            productImages.add(productImage);
        }

        // Lưu danh sách hình ảnh mới vào cơ sở dữ liệu
        if (!productImages.isEmpty()) {
            productImagesRepository.saveAll(productImages);
        }
    }

    private static List<String> getImageUrls(List<String> imageUrls) {
        return imageUrls;
    }
    public ProductDetail deleteProductDetailById(Integer id) {
        Optional<ProductDetail> productDetail = productDetailRepository.findById(id);

        if(productDetail.isPresent())
        {
            ProductDetail productDetail2 = productDetail.get();
            productDetail2.setDeletedAt(LocalDateTime.now());
            return productDetailRepository.save(productDetail2);
        }
        return null;
    }
}