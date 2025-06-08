package org.example.campusmarket.repository;

import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
      /**
     * 根据用户查找商品
     */
    List<Product> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * 根据用户查找有效商品（排除已删除状态）
     */
    List<Product> findByUserAndStatusNotOrderByCreatedAtDesc(User user, Product.ProductStatus status);
    
    /**
     * 根据分类查找商品
     */
    Page<Product> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);
    
    /**
     * 根据状态查找商品
     */
    Page<Product> findByStatusOrderByCreatedAtDesc(Product.ProductStatus status, Pageable pageable);
    
    /**
     * 根据标题模糊搜索商品
     */
    @Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Product> findByTitleContainingAndStatus(@Param("keyword") String keyword, 
                                                @Param("status") Product.ProductStatus status, 
                                                Pageable pageable);
      /**
     * 根据分类和状态查找商品
     */
    Page<Product> findByCategoryAndStatusOrderByCreatedAtDesc(String category, 
                                                            Product.ProductStatus status, 
                                                            Pageable pageable);
}
