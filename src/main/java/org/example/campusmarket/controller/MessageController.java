package org.example.campusmarket.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import jakarta.validation.Valid;
import org.example.campusmarket.dto.ApiResponse;
import org.example.campusmarket.dto.MessageCreateDto;
import org.example.campusmarket.entity.Message;
import org.example.campusmarket.entity.User;
import org.example.campusmarket.security.UserPrincipal;
import org.example.campusmarket.service.MessageService;
import org.example.campusmarket.service.UserService;
import org.example.campusmarket.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;    /**
     * 创建留言
     */
    @PostMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Message>> createMessage(
            @PathVariable Long productId,
            @Valid @RequestBody MessageCreateDto messageDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户不存在", 400));
            }

            Message message = messageService.createMessage(productId, messageDto, userOptional.get());
            return ResponseEntity.ok(ApiResponse.success("留言发送成功", message));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("留言发送失败：" + e.getMessage()));
        }
    }

    /**
     * 直接发送消息给用户
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Message>> sendDirectMessage(
            @Valid @RequestBody MessageCreateDto messageDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<User> senderOptional = userService.findByUsername(userPrincipal.getUsername());
            if (senderOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("发送者不存在", 400));
            }

            Message message = messageService.sendDirectMessage(messageDto, senderOptional.get());
            return ResponseEntity.ok(ApiResponse.success("消息发送成功", message));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("消息发送失败：" + e.getMessage()));
        }
    }

    /**
     * 获取商品的所有留言
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<Message>>> getProductMessages(@PathVariable Long productId) {
        try {
            List<Message> messages = messageService.getProductMessages(productId);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取留言失败：" + e.getMessage()));
        }
    }

    /**
     * 获取我的留言
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Message>>> getMyMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户不存在", 400));
            }

            List<Message> messages = messageService.getUserMessages(userOptional.get());
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取我的留言失败：" + e.getMessage()));
        }
    }

    /**
     * 获取我的留言 - JSON格式
     */
    @GetMapping("/my/json")
    public ResponseEntity<String> getMyMessagesJson(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<User> userOptional = userService.findByUsername(userPrincipal.getUsername());
            if (userOptional.isEmpty()) {
                JSONObject error = new JSONObject();
                error.set("success", false);
                error.set("message", "用户不存在");
                error.set("code", 400);
                return ResponseEntity.badRequest().body(error.toString());
            }

            List<Message> messages = messageService.getUserMessages(userOptional.get());
            JSONArray messagesJson = JsonUtils.messagesToJson(messages);
            
            JSONObject response = new JSONObject();
            response.set("success", true);
            response.set("message", "获取成功");
            response.set("data", messagesJson);
            
            return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(response.toString());
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.set("success", false);
            error.set("message", "获取我的留言失败：" + e.getMessage());
            error.set("code", 500);
            return ResponseEntity.internalServerError().body(error.toString());
        }
    }

    /**
     * 删除留言
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            boolean success = messageService.deleteMessage(id, userPrincipal.getUsername());
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("留言删除成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("删除失败，请检查留言是否存在或您是否有权限", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("删除失败：" + e.getMessage()));
        }
    }
}
