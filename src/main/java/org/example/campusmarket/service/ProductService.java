package org.example.campusmarket.service;

import org.example.campusmarket.dto.ProductCreateDto;
import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;
import org.example.campusmarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private final String uploadDir = "uploads/products/";    /**
     * 创建商品
     */
    public Product createProduct(ProductCreateDto productDto, User user, MultipartFile imageFile) throws IOException {
        Product product = new Product();
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCategory(productDto.getCategory());
        product.setUser(user);
        product.setStatus(Product.ProductStatus.AVAILABLE);

        // 处理图片上传
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            product.setImageUrl(imageUrl);
        } else if (productDto.getImageUrl() != null && !productDto.getImageUrl().trim().isEmpty()) {
            // 如果没有上传文件但提供了图片URL，则使用URL
            product.setImageUrl(productDto.getImageUrl().trim());
        }

        return productRepository.save(product);
    }

    /**
     * 保存图片文件
     */
    private String saveImage(MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/products/" + filename;
    }

    /**
     * 获取所有可用商品（分页）
     */
    public Page<Product> getAvailableProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByStatusOrderByCreatedAtDesc(Product.ProductStatus.AVAILABLE, pageable);
    }

    /**
     * 根据分类获取商品
     */
    public Page<Product> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryAndStatusOrderByCreatedAtDesc(
            category, Product.ProductStatus.AVAILABLE, pageable);
    }

    /**
     * 搜索商品
     */
    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByTitleContainingAndStatus(
            keyword, Product.ProductStatus.AVAILABLE, pageable);
    }

    /**
     * 根据ID获取商品
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 获取用户的商品
     */
    public List<Product> getUserProducts(User user) {
        return productRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * 更新商品状态为已售出
     */
    public boolean markAsSold(Long productId, String username) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getUser().getUsername().equals(username)) {
                product.setStatus(Product.ProductStatus.SOLD);
                productRepository.save(product);
                return true;
            }
        }
        return false;
    }

    /**
     * 删除商品
     */
    public boolean deleteProduct(Long productId, String username) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getUser().getUsername().equals(username)) {
                product.setStatus(Product.ProductStatus.REMOVED);
                productRepository.save(product);
                return true;
            }
        }
        return false;
    }

    /**
     * 更新商品信息
     */
    public Product updateProduct(Long productId, ProductCreateDto productDto, String username) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getUser().getUsername().equals(username)) {
                product.setTitle(productDto.getTitle());
                product.setDescription(productDto.getDescription());
                product.setPrice(productDto.getPrice());
                product.setCategory(productDto.getCategory());
                return productRepository.save(product);
            }
        }
        return null;
    }
}
