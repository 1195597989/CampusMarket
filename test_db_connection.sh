#!/bin/bash
# 数据库连接测试脚本

echo "开始测试数据库连接..."

# 测试MySQL连接
mysql -u root -p789546 -e "SELECT 'MySQL连接成功!' as status;"

if [ $? -eq 0 ]; then
    echo "✅ MySQL连接正常"
    
    # 检查数据库是否存在
    mysql -u root -p789546 -e "SHOW DATABASES LIKE 'campus_market';"
    
    if [ $? -eq 0 ]; then
        echo "✅ campus_market 数据库已存在"
        
        # 检查表是否存在
        mysql -u root -p789546 campus_market -e "SHOW TABLES;"
        
        echo "📊 数据库表结构："
        mysql -u root -p789546 campus_market -e "
        SELECT 
            table_name as '表名',
            table_comment as '说明'
        FROM information_schema.tables 
        WHERE table_schema = 'campus_market';"
        
    else
        echo "❌ campus_market 数据库不存在，请先运行 database_init.sql"
    fi
    
else
    echo "❌ MySQL连接失败，请检查："
    echo "1. MySQL服务是否启动"
    echo "2. 用户名密码是否正确"
    echo "3. 端口是否正确(默认3306)"
fi
