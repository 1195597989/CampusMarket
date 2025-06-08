package org.example.campusmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageCreateDto {
    
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 200, message = "留言内容不能超过200个字符")
    private String content;

    // 构造函数
    public MessageCreateDto() {}

    public MessageCreateDto(String content) {
        this.content = content;
    }

    // Getter和Setter方法
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
