package org.example.campusmarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 接收者字段，用于直接消息
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = true)
    private User recipient;

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 200, message = "留言内容不能超过200个字符")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 构造函数
    public Message() {}

    public Message(Product product, User user, String content) {
        this.product = product;
        this.user = user;
        this.content = content;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // 为JSON序列化提供发送者信息，避免循环引用
    @JsonProperty("sender")
    public Map<String, Object> getSender() {
        if (user == null) return null;
        
        Map<String, Object> sender = new HashMap<>();
        sender.put("id", user.getId());
        sender.put("username", user.getUsername());
        return sender;
    }
      // 为JSON序列化提供产品信息，避免循环引用
    @JsonProperty("productInfo")
    public Map<String, Object> getProductInfo() {
        if (product == null) return null;
        
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("id", product.getId());
        productInfo.put("title", product.getTitle());
        return productInfo;
    }
    
    // 为JSON序列化提供接收者信息，避免循环引用
    @JsonProperty("recipient")
    public Map<String, Object> getRecipientInfo() {
        if (recipient == null) return null;
        
        Map<String, Object> recipientInfo = new HashMap<>();
        recipientInfo.put("id", recipient.getId());
        recipientInfo.put("username", recipient.getUsername());
        return recipientInfo;
    }
}
