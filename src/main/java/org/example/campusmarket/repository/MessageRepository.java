package org.example.campusmarket.repository;

import org.example.campusmarket.entity.Message;
import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 根据商品查找留言
     */
    List<Message> findByProductOrderByCreatedAtDesc(Product product);
    
    /**
     * 根据用户查找留言
     */
    List<Message> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * 根据商品ID查找留言
     */
    List<Message> findByProductIdOrderByCreatedAtDesc(Long productId);
}
