package org.example.campusmarket.service;

import org.example.campusmarket.dto.MessageCreateDto;
import org.example.campusmarket.entity.Message;
import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;
import org.example.campusmarket.repository.MessageRepository;
import org.example.campusmarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 创建留言
     */
    public Message createMessage(Long productId, MessageCreateDto messageDto, User user) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Message message = new Message();
            message.setProduct(product);
            message.setUser(user);
            message.setContent(messageDto.getContent());
            return messageRepository.save(message);
        }
        throw new RuntimeException("商品不存在");
    }

    /**
     * 获取商品的所有留言
     */
    public List<Message> getProductMessages(Long productId) {
        return messageRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    /**
     * 获取用户的所有留言
     */
    public List<Message> getUserMessages(User user) {
        return messageRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * 删除留言（只能删除自己的留言）
     */
    public boolean deleteMessage(Long messageId, String username) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            if (message.getUser().getUsername().equals(username)) {
                messageRepository.delete(message);
                return true;
            }
        }
        return false;
    }
}
