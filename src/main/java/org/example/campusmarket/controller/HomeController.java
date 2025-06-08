package org.example.campusmarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "欢迎使用校园二手交易平台 API");
        response.put("version", "1.0.0");
        response.put("docs", "/swagger-ui/index.html");
        return response;
    }

    @GetMapping("/api/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "running");
        response.put("service", "CampusMarket API");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
