-- 校园二手交易平台数据库创建脚本
-- 数据库：campus_market

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS campus_market 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE campus_market;

-- 2. 创建用户表 (users)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(11) COMMENT '手机号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 3. 创建商品表 (products)
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    title VARCHAR(100) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    image_url VARCHAR(500) COMMENT '商品图片URL',
    category VARCHAR(50) NOT NULL COMMENT '商品分类',
    status ENUM('AVAILABLE', 'SOLD', 'REMOVED') NOT NULL DEFAULT 'AVAILABLE' COMMENT '商品状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_title (title),
    FULLTEXT(title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 4. 创建留言表 (messages)
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '留言ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '留言用户ID',
    content TEXT NOT NULL COMMENT '留言内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

-- 5. 插入一些测试数据

-- 插入测试用户
INSERT IGNORE INTO users (username, password, email, phone) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIFi', 'admin@campus.com', '13800138000'),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIFi', 'student1@campus.com', '13800138001'),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIFi', 'student2@campus.com', '13800138002');

-- 插入测试商品分类数据
INSERT IGNORE INTO products (user_id, title, description, price, category, status) VALUES
(2, '全新iPhone 14', '刚买的iPhone 14，因为换了新手机所以出售，99新', 4999.00, '数码电子', 'AVAILABLE'),
(2, '二手MacBook Pro', 'MacBook Pro 13寸，2020款，8G内存256G硬盘，成色很好', 6999.00, '数码电子', 'AVAILABLE'),
(3, '大学物理教材', '高等教育出版社，大学物理上下册，几乎全新', 45.00, '图书教材', 'AVAILABLE'),
(3, '自行车出售', '捷安特山地车，骑了两年，保养很好，适合校园代步', 800.00, '运动户外', 'AVAILABLE'),
(2, '护肤品套装', '兰蔻小黑瓶套装，朋友送的用不完，保质期还很长', 299.00, '美妆个护', 'SOLD');

-- 插入测试留言数据
INSERT IGNORE INTO messages (product_id, user_id, content) VALUES
(1, 3, '手机还在保修期内吗？'),
(1, 1, '可以便宜一点吗？'),
(2, 3, '电池健康度怎么样？'),
(3, 2, '书有画线或者笔记吗？'),
(4, 1, '自行车在哪个校区？可以看车吗？');

-- 6. 创建一些常用的视图（可选）

-- 商品详情视图（包含用户信息）
CREATE OR REPLACE VIEW product_details AS
SELECT 
    p.id,
    p.title,
    p.description,
    p.price,
    p.image_url,
    p.category,
    p.status,
    p.created_at,
    u.username as seller_name,
    u.phone as seller_phone,
    (SELECT COUNT(*) FROM messages m WHERE m.product_id = p.id) as message_count
FROM products p
JOIN users u ON p.user_id = u.id;

-- 用户统计视图
CREATE OR REPLACE VIEW user_stats AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.created_at,
    COUNT(p.id) as product_count,
    COUNT(CASE WHEN p.status = 'SOLD' THEN 1 END) as sold_count,
    COUNT(m.id) as message_count
FROM users u
LEFT JOIN products p ON u.id = p.user_id
LEFT JOIN messages m ON u.id = m.user_id
GROUP BY u.id, u.username, u.email, u.created_at;

-- 显示创建结果
SELECT '数据库创建完成!' as result;
SELECT '表结构:' as info;
SHOW TABLES;

SELECT '用户表数据:' as info;
SELECT id, username, email, phone, created_at FROM users;

SELECT '商品表数据:' as info;
SELECT id, title, price, category, status, created_at FROM products;

SELECT '留言表数据:' as info;
SELECT id, product_id, user_id, content, created_at FROM messages;
