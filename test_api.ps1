# 校园二手交易平台API测试脚本
# 使用PowerShell和Invoke-RestMethod测试各个API接口

$baseUrl = "http://localhost:8080"
$token = ""

Write-Host "🚀 开始测试校园二手交易平台API..." -ForegroundColor Green
Write-Host "基础URL: $baseUrl" -ForegroundColor Cyan

# 测试1: 检查API状态
Write-Host "`n1️⃣ 测试API状态..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/status" -Method GET
    Write-Host "✅ API状态正常" -ForegroundColor Green
    Write-Host "响应: $($response | ConvertTo-Json)" -ForegroundColor White
} catch {
    Write-Host "❌ API状态检查失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 测试2: 用户注册
Write-Host "`n2️⃣ 测试用户注册..." -ForegroundColor Yellow
$registerData = @{
    username = "testuser$(Get-Random -Maximum 1000)"
    password = "123456"
    email = "test$(Get-Random -Maximum 1000)@campus.com"
    phone = "13800138000"
} | ConvertTo-Json

try {
    $headers = @{ "Content-Type" = "application/json" }
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" -Method POST -Body $registerData -Headers $headers
    Write-Host "✅ 用户注册成功" -ForegroundColor Green
    Write-Host "响应: $($response | ConvertTo-Json)" -ForegroundColor White
    $testUsername = ($registerData | ConvertFrom-Json).username
} catch {
    Write-Host "❌ 用户注册失败: $($_.Exception.Message)" -ForegroundColor Red
    # 如果注册失败，使用默认用户名
    $testUsername = "admin"
}

# 测试3: 用户登录
Write-Host "`n3️⃣ 测试用户登录..." -ForegroundColor Yellow
$loginData = @{
    username = $testUsername
    password = "123456"
} | ConvertTo-Json

try {
    $headers = @{ "Content-Type" = "application/json" }
    $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginData -Headers $headers
    Write-Host "✅ 用户登录成功" -ForegroundColor Green
    $token = $response.data.token
    Write-Host "Token: $token" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 用户登录失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "尝试使用测试数据中的默认用户..." -ForegroundColor Yellow
    
    # 尝试使用默认用户
    $defaultLoginData = @{
        username = "admin"
        password = "123456"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $defaultLoginData -Headers $headers
        Write-Host "✅ 默认用户登录成功" -ForegroundColor Green
        $token = $response.data.token
        Write-Host "Token: $token" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ 默认用户登录也失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "请检查数据库中是否有测试用户数据" -ForegroundColor Yellow
        exit 1
    }
}

# 如果有token，继续测试需要认证的接口
if ($token) {
    $authHeaders = @{ 
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json" 
    }
    
    # 测试4: 获取商品列表
    Write-Host "`n4️⃣ 测试获取商品列表..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/products?page=0&size=10" -Method GET -Headers $authHeaders
        Write-Host "✅ 获取商品列表成功" -ForegroundColor Green
        Write-Host "商品数量: $($response.data.content.Count)" -ForegroundColor Cyan
        if ($response.data.content.Count -gt 0) {
            Write-Host "第一个商品: $($response.data.content[0].title)" -ForegroundColor White
        }
    } catch {
        Write-Host "❌ 获取商品列表失败: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    # 测试5: 发布商品
    Write-Host "`n5️⃣ 测试发布商品..." -ForegroundColor Yellow
    $productData = @{
        title = "测试商品 - PowerShell发布"
        description = "这是通过API测试脚本发布的测试商品"
        price = 99.99
        category = "数码电子"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/products" -Method POST -Body $productData -Headers $authHeaders
        Write-Host "✅ 商品发布成功" -ForegroundColor Green
        $productId = $response.data.id
        Write-Host "商品ID: $productId" -ForegroundColor Cyan
        
        # 测试6: 获取商品详情
        if ($productId) {
            Write-Host "`n6️⃣ 测试获取商品详情..." -ForegroundColor Yellow
            try {
                $response = Invoke-RestMethod -Uri "$baseUrl/api/products/$productId" -Method GET -Headers $authHeaders
                Write-Host "✅ 获取商品详情成功" -ForegroundColor Green
                Write-Host "商品标题: $($response.data.title)" -ForegroundColor White
            } catch {
                Write-Host "❌ 获取商品详情失败: $($_.Exception.Message)" -ForegroundColor Red
            }
            
            # 测试7: 添加留言
            Write-Host "`n7️⃣ 测试添加留言..." -ForegroundColor Yellow
            $messageData = @{
                content = "这是通过API测试脚本添加的留言"
            } | ConvertTo-Json
            
            try {
                $response = Invoke-RestMethod -Uri "$baseUrl/api/products/$productId/messages" -Method POST -Body $messageData -Headers $authHeaders
                Write-Host "✅ 留言添加成功" -ForegroundColor Green
                Write-Host "留言ID: $($response.data.id)" -ForegroundColor Cyan
            } catch {
                Write-Host "❌ 留言添加失败: $($_.Exception.Message)" -ForegroundColor Red
            }
        }
        
    } catch {
        Write-Host "❌ 商品发布失败: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    # 测试8: 搜索商品
    Write-Host "`n8️⃣ 测试搜索商品..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/products/search?keyword=测试&page=0&size=10" -Method GET -Headers $authHeaders
        Write-Host "✅ 商品搜索成功" -ForegroundColor Green
        Write-Host "搜索结果数量: $($response.data.content.Count)" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ 商品搜索失败: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    # 测试9: 获取我的商品
    Write-Host "`n9️⃣ 测试获取我的商品..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/products/my" -Method GET -Headers $authHeaders
        Write-Host "✅ 获取我的商品成功" -ForegroundColor Green
        Write-Host "我的商品数量: $($response.data.Count)" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ 获取我的商品失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n🎉 API测试完成！" -ForegroundColor Green
Write-Host "你可以访问以下地址查看更多信息：" -ForegroundColor Yellow
Write-Host "- API文档: http://localhost:8080/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host "- 主页: http://localhost:8080" -ForegroundColor Cyan
Write-Host "- 状态检查: http://localhost:8080/api/status" -ForegroundColor Cyan
