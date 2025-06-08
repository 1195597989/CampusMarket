package org.example.campusmarket.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.example.campusmarket.entity.Message;
import org.example.campusmarket.entity.Product;
import org.example.campusmarket.entity.User;

import java.util.List;

/**
 * JSON工具类，使用Hutool处理JSON序列化，避免循环引用问题
 */
public class JsonUtils {
    
    /**
     * 将Product转换为JSON对象，避免循环引用
     */
    public static JSONObject productToJson(Product product) {
        if (product == null) return null;
        
        JSONObject json = new JSONObject();
        json.set("id", product.getId());
        json.set("title", product.getTitle());
        json.set("description", product.getDescription());
        json.set("price", product.getPrice());
        json.set("imageUrl", product.getImageUrl());
        json.set("category", product.getCategory());
        json.set("status", product.getStatus());
        json.set("createdAt", product.getCreatedAt());
        
        // 添加卖家信息，避免循环引用
        if (product.getUser() != null) {
            JSONObject seller = new JSONObject();
            seller.set("id", product.getUser().getId());
            seller.set("username", product.getUser().getUsername());
            seller.set("email", product.getUser().getEmail());
            json.set("seller", seller);
        }
        
        return json;
    }
    
    /**
     * 将Product列表转换为JSON数组
     */
    public static JSONArray productsToJson(List<Product> products) {
        if (products == null) return new JSONArray();
        
        JSONArray jsonArray = new JSONArray();
        for (Product product : products) {
            jsonArray.add(productToJson(product));
        }
        return jsonArray;
    }
    
    /**
     * 将Message转换为JSON对象，避免循环引用
     */
    public static JSONObject messageToJson(Message message) {
        if (message == null) return null;
        
        JSONObject json = new JSONObject();
        json.set("id", message.getId());
        json.set("content", message.getContent());
        json.set("createdAt", message.getCreatedAt());
        
        // 添加发送者信息
        if (message.getUser() != null) {
            JSONObject sender = new JSONObject();
            sender.set("id", message.getUser().getId());
            sender.set("username", message.getUser().getUsername());
            json.set("sender", sender);
        }
        
        // 添加产品信息
        if (message.getProduct() != null) {
            JSONObject productInfo = new JSONObject();
            productInfo.set("id", message.getProduct().getId());
            productInfo.set("title", message.getProduct().getTitle());
            json.set("productInfo", productInfo);
        }
        
        // 添加接收者信息
        if (message.getRecipient() != null) {
            JSONObject recipient = new JSONObject();
            recipient.set("id", message.getRecipient().getId());
            recipient.set("username", message.getRecipient().getUsername());
            json.set("recipient", recipient);
        }
        
        return json;
    }
    
    /**
     * 将Message列表转换为JSON数组
     */
    public static JSONArray messagesToJson(List<Message> messages) {
        if (messages == null) return new JSONArray();
        
        JSONArray jsonArray = new JSONArray();
        for (Message message : messages) {
            jsonArray.add(messageToJson(message));
        }
        return jsonArray;
    }
    
    /**
     * 将User转换为JSON对象，避免敏感信息
     */
    public static JSONObject userToJson(User user) {
        if (user == null) return null;
        
        JSONObject json = new JSONObject();
        json.set("id", user.getId());
        json.set("username", user.getUsername());
        json.set("email", user.getEmail());
        json.set("createdAt", user.getCreatedAt());
        // 不包含密码等敏感信息
        
        return json;
    }
    
    /**
     * 创建API响应对象
     */
    public static JSONObject createApiResponse(boolean success, String message, Object data) {
        JSONObject response = new JSONObject();
        response.set("success", success);
        response.set("message", message);
        if (data != null) {
            response.set("data", data);
        }
        return response;
    }
    
    /**
     * 从JSON字符串解析为JSONObject
     */
    public static JSONObject parseJson(String jsonStr) {
        try {
            return JSONUtil.parseObj(jsonStr);
        } catch (Exception e) {
            return new JSONObject();
        }
    }
    
    /**
     * 将对象转换为JSON字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return JSONUtil.toJsonStr(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
