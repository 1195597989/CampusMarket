package org.example.campusmarket.controller;

import jakarta.validation.Valid;
import org.example.campusmarket.dto.ApiResponse;
import org.example.campusmarket.dto.UserLoginDto;
import org.example.campusmarket.dto.UserRegistrationDto;
import org.example.campusmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            Map<String, Object> result = userService.registerUser(registrationDto);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(result.get("message").toString(), 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("注册失败：" + e.getMessage()));
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            Map<String, Object> result = userService.authenticateUser(loginDto);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success(result.get("message").toString(), result));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(result.get("message").toString(), 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("登录失败：" + e.getMessage()));
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String username) {
        try {
            boolean success = userService.updatePassword(username, oldPassword, newPassword);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("原密码错误", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("密码修改失败：" + e.getMessage()));
        }
    }
}
