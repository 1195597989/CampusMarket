package org.example.campusmarket.controller;

import jakarta.validation.Valid;
import org.example.campusmarket.dto.ApiResponse;
import org.example.campusmarket.dto.ProductCreateDto;
import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;
import org.example.campusmarket.security.UserPrincipal;
import org.example.campusmarket.service.ProductService;
import org.example.campusmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    /**
     * 发布商品
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @Valid @ModelAttribute ProductCreateDto productDto,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户不存在", 400));
            }

            Product product = productService.createProduct(productDto, userOptional.get(), image);
            return ResponseEntity.ok(ApiResponse.success("商品发布成功", product));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("商品发布失败：" + e.getMessage()));
        }
    }

    /**
     * 获取商品列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        try {
            Page<Product> products;
            if (category != null && !category.isEmpty()) {
                products = productService.getProductsByCategory(category, page, size);
            } else {
                products = productService.getAvailableProducts(page, size);
            }
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取商品列表失败：" + e.getMessage()));
        }
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Product> products = productService.searchProducts(keyword, page, size);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("搜索商品失败：" + e.getMessage()));
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        try {
            Optional<Product> productOptional = productService.getProductById(id);
            if (productOptional.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(productOptional.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取商品详情失败：" + e.getMessage()));
        }
    }

    /**
     * 获取我的商品
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Product>>> getMyProducts(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户不存在", 400));
            }

            List<Product> products = productService.getUserProducts(userOptional.get());
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取我的商品失败：" + e.getMessage()));
        }
    }

    /**
     * 标记商品为已售出
     */
    @PutMapping("/{id}/sold")
    public ResponseEntity<ApiResponse<String>> markAsSold(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            boolean success = productService.markAsSold(id, userPrincipal.getUsername());
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("商品已标记为已售出"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("操作失败，请检查商品是否存在或您是否有权限", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("操作失败：" + e.getMessage()));
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            boolean success = productService.deleteProduct(id, userPrincipal.getUsername());
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("商品删除成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("删除失败，请检查商品是否存在或您是否有权限", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("删除失败：" + e.getMessage()));
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductCreateDto productDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDto, userPrincipal.getUsername());
            if (updatedProduct != null) {
                return ResponseEntity.ok(ApiResponse.success("商品更新成功", updatedProduct));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("更新失败，请检查商品是否存在或您是否有权限", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("更新失败：" + e.getMessage()));
        }
    }
}
