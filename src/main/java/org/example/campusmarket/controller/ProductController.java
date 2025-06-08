package org.example.campusmarket.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import jakarta.validation.Valid;
import org.example.campusmarket.dto.ApiResponse;
import org.example.campusmarket.dto.ProductCreateDto;
import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;
import org.example.campusmarket.security.UserPrincipal;
import org.example.campusmarket.service.ProductService;
import org.example.campusmarket.service.UserService;
import org.example.campusmarket.util.JsonUtils;
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
    private UserService userService;    /**
     * 发布商品（支持文件上传）
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
     * 发布商品（JSON格式）- 使用Hutool处理JSON
     */
    @PostMapping("/json")
    public ResponseEntity<String> createProductJson(
            @RequestBody String jsonRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            // 使用Hutool解析JSON
            JSONObject requestJson = JsonUtils.parseJson(jsonRequest);
            
            // 验证必需字段
            if (!requestJson.containsKey("title") || 
                !requestJson.containsKey("price") || 
                !requestJson.containsKey("category")) {
                JSONObject errorResponse = JsonUtils.createApiResponse(false, "缺少必需字段", null);
                return ResponseEntity.badRequest().body(errorResponse.toString());
            }
            
            Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
            if (userOptional.isEmpty()) {
                JSONObject errorResponse = JsonUtils.createApiResponse(false, "用户不存在", null);
                return ResponseEntity.badRequest().body(errorResponse.toString());
            }

            // 创建ProductCreateDto
            ProductCreateDto productDto = new ProductCreateDto();
            productDto.setTitle(requestJson.getStr("title"));
            productDto.setDescription(requestJson.getStr("description"));
            productDto.setPrice(requestJson.getBigDecimal("price"));
            productDto.setCategory(requestJson.getStr("category"));
            productDto.setImageUrl(requestJson.getStr("imageUrl"));

            Product product = productService.createProduct(productDto, userOptional.get(), null);
            
            // 使用Hutool生成响应JSON
            JSONObject productJson = JsonUtils.productToJson(product);
            JSONObject response = JsonUtils.createApiResponse(true, "商品发布成功", productJson);
            
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            JSONObject errorResponse = JsonUtils.createApiResponse(false, "商品发布失败：" + e.getMessage(), null);
            return ResponseEntity.internalServerError().body(errorResponse.toString());
        }
    }    /**
     * 获取所有商品（JSON格式）- 使用Hutool处理JSON
     */
    @GetMapping("/json")
    public ResponseEntity<String> getAllProductsJson(
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Product> productsPage;
            if (category.isEmpty()) {
                productsPage = productService.getAvailableProducts(page, size);
            } else {
                productsPage = productService.getProductsByCategory(category, page, size);
            }
            
            // 使用Hutool转换为JSON
            JSONArray productsJson = JsonUtils.productsToJson(productsPage.getContent());
            
            JSONObject responseData = new JSONObject();
            responseData.set("products", productsJson);
            responseData.set("totalElements", productsPage.getTotalElements());
            responseData.set("totalPages", productsPage.getTotalPages());
            responseData.set("currentPage", page);
            responseData.set("size", size);
            
            JSONObject response = JsonUtils.createApiResponse(true, "获取商品列表成功", responseData);
            
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            JSONObject errorResponse = JsonUtils.createApiResponse(false, "获取商品列表失败：" + e.getMessage(), null);
            return ResponseEntity.internalServerError().body(errorResponse.toString());
        }
    }    /**
     * 获取商品详情（JSON格式）- 使用Hutool处理JSON
     */
    @GetMapping("/{id}/json")
    public ResponseEntity<String> getProductJson(@PathVariable Long id) {
        try {
            Optional<Product> productOptional = productService.getProductById(id);
            if (productOptional.isPresent()) {
                JSONObject productJson = JsonUtils.productToJson(productOptional.get());
                JSONObject response = JsonUtils.createApiResponse(true, "获取商品详情成功", productJson);
                return ResponseEntity.ok(response.toString());
            } else {
                JSONObject errorResponse = JsonUtils.createApiResponse(false, "商品不存在", null);
                return ResponseEntity.status(404).body(errorResponse.toString());
            }
        } catch (Exception e) {
            JSONObject errorResponse = JsonUtils.createApiResponse(false, "获取商品详情失败：" + e.getMessage(), null);
            return ResponseEntity.internalServerError().body(errorResponse.toString());
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
