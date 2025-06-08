package org.example.campusmarket.service;

import org.example.campusmarket.dto.MessageCreateDto;
import org.example.campusmarket.entity.Message;
import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;
import org.example.campusmarket.repository.MessageRepository;
import org.example.campusmarket.repository.ProductRepository;
import org.example.campusmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;    /**
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
     * 发送直接消息给指定用户
     */
    public Message sendDirectMessage(MessageCreateDto messageDto, User sender) {
        if (messageDto.getRecipientUsername() == null || messageDto.getRecipientUsername().trim().isEmpty()) {
            throw new RuntimeException("接收者用户名不能为空");
        }
        
        Optional<User> recipientOptional = userRepository.findByUsername(messageDto.getRecipientUsername());
        if (recipientOptional.isEmpty()) {
            throw new RuntimeException("接收者用户不存在");
        }
        
        User recipient = recipientOptional.get();
        if (sender.getId().equals(recipient.getId())) {
            throw new RuntimeException("不能给自己发送消息");
        }
        
        Message message = new Message();
        message.setUser(sender);
        message.setRecipient(recipient);
        message.setContent(messageDto.getContent());
        // product 为 null，表示这是直接消息
        return messageRepository.save(message);
    }

    /**
     * 获取商品的所有留言
     */
    public List<Message> getProductMessages(Long productId) {
        return messageRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }    /**
     * 获取用户的所有留言（发送的和接收的）
     */
    public List<Message> getUserMessages(User user) {
        return messageRepository.findAllUserRelatedMessages(user);
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
