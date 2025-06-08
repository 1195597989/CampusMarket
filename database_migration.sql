-- 数据库迁移脚本：添加直接消息支持
-- 为messages表添加recipient_id字段和修改product_id为可空

USE campus_market;

-- 1. 添加recipient_id字段
ALTER TABLE messages 
ADD COLUMN recipient_id BIGINT NULL COMMENT '接收者用户ID（用于直接消息）';

-- 2. 修改product_id字段为可空
ALTER TABLE messages 
MODIFY COLUMN product_id BIGINT NULL COMMENT '商品ID（留言时使用，直接消息时为空）';

-- 3. 添加外键约束
ALTER TABLE messages 
ADD CONSTRAINT fk_messages_recipient 
FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE;

-- 4. 添加索引
ALTER TABLE messages 
ADD INDEX idx_recipient_id (recipient_id);

-- 5. 添加约束：确保product_id和recipient_id至少有一个不为空
ALTER TABLE messages 
ADD CONSTRAINT chk_message_target 
CHECK (product_id IS NOT NULL OR recipient_id IS NOT NULL);

-- 6. 插入一些测试直接消息数据
INSERT INTO messages (user_id, recipient_id, content) VALUES
(2, 3, '你好，请问你的自行车还在出售吗？'),
(3, 2, '是的，还在出售，你什么时候有时间看车？'),
(1, 2, '你好，我对你的iPhone很感兴趣');

SELECT 'Migration completed successfully!' as status;
