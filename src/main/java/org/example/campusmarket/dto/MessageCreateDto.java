package org.example.campusmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageCreateDto {
    
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 200, message = "留言内容不能超过200个字符")
    private String content;
    
    // 用于直接消息的接收者用户名
    private String recipientUsername;    // 构造函数
    public MessageCreateDto() {}

    public MessageCreateDto(String content) {
        this.content = content;
    }

    public MessageCreateDto(String content, String recipientUsername) {
        this.content = content;
        this.recipientUsername = recipientUsername;
    }

    // Getter和Setter方法
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
}
