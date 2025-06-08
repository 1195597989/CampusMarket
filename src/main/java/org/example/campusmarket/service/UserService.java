package org.example.campusmarket.service;

import org.example.campusmarket.dto.UserLoginDto;
import org.example.campusmarket.dto.UserRegistrationDto;
import org.example.campusmarket.entity.User;
import org.example.campusmarket.repository.UserRepository;
import org.example.campusmarket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    public Map<String, Object> registerUser(UserRegistrationDto registrationDto) {
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            response.put("success", false);
            response.put("message", "邮箱已被注册");
            return response;
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setPhone(registrationDto.getPhone());

        User savedUser = userRepository.save(user);

        response.put("success", true);
        response.put("message", "注册成功");
        response.put("userId", savedUser.getId());
        return response;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> authenticateUser(UserLoginDto loginDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(loginDto.getUsername());

            Optional<User> userOptional = userRepository.findByUsername(loginDto.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("phone", user.getPhone());

                response.put("success", true);
                response.put("message", "登录成功");
                response.put("token", jwt);
                response.put("user", userInfo);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }

        return response;
    }

    /**
     * 根据ID获取用户
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名获取用户
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 更新用户密码
     */
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}
