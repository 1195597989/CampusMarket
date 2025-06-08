# 数据库连接测试脚本 (PowerShell版本)

Write-Host "开始测试数据库连接..." -ForegroundColor Yellow

# 定义数据库连接参数
$server = "localhost"
$port = "3306"
$username = "root"
$password = "789546"  # 请根据实际情况修改
$database = "campus_market"

# 测试MySQL连接
try {
    Write-Host "正在测试MySQL连接..." -ForegroundColor Blue
    
    # 使用mysql命令测试连接
    $testConnection = mysql -u $username -p$password -e "SELECT 'MySQL连接成功!' as status;" 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ MySQL连接正常" -ForegroundColor Green
        
        # 检查数据库是否存在
        Write-Host "检查数据库是否存在..." -ForegroundColor Blue
        $dbExists = mysql -u $username -p$password -e "SHOW DATABASES LIKE 'campus_market';" 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ campus_market 数据库已存在" -ForegroundColor Green
            
            # 检查表是否存在
            Write-Host "检查数据库表..." -ForegroundColor Blue
            Write-Host "📊 数据库表结构：" -ForegroundColor Cyan
            mysql -u $username -p$password $database -e "SHOW TABLES;"
            
            # 显示每个表的记录数
            Write-Host "`n📈 表数据统计：" -ForegroundColor Cyan
            mysql -u $username -p$password $database -e "
            SELECT 'users' as 表名, COUNT(*) as 记录数 FROM users
            UNION ALL
            SELECT 'products' as 表名, COUNT(*) as 记录数 FROM products  
            UNION ALL
            SELECT 'messages' as 表名, COUNT(*) as 记录数 FROM messages;"
            
        } else {
            Write-Host "❌ campus_market 数据库不存在" -ForegroundColor Red
            Write-Host "请先运行以下命令创建数据库：" -ForegroundColor Yellow
            Write-Host "mysql -u $username -p$password < database_init.sql" -ForegroundColor White
        }
        
    } else {
        Write-Host "❌ MySQL连接失败" -ForegroundColor Red
        Write-Host "请检查以下项目：" -ForegroundColor Yellow
        Write-Host "1. MySQL服务是否启动" -ForegroundColor White
        Write-Host "2. 用户名密码是否正确 (当前: $username/$password)" -ForegroundColor White
        Write-Host "3. 端口是否正确 (默认: 3306)" -ForegroundColor White
        Write-Host "4. 是否已安装MySQL客户端工具" -ForegroundColor White
    }
    
} catch {
    Write-Host "❌ 执行失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请确保已安装MySQL客户端工具并添加到系统PATH中" -ForegroundColor Yellow
}

Write-Host "`n测试完成!" -ForegroundColor Green
