-- ========================================
-- 线上购物与交易系统数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS demo1_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE demo1_db;

-- ========================================
-- 用户相关表
-- ========================================

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认管理员账户
INSERT INTO users (username, password) VALUES ('admin', '123456') 
ON DUPLICATE KEY UPDATE username=username;

-- ========================================
-- 商品相关表
-- ========================================

-- 创建分类表
CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(500) COMMENT '分类描述',
    parent_id INT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 创建商品表
CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    original_price DECIMAL(10, 2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存数量',
    category_id INT COMMENT '分类ID',
    image_url VARCHAR(500) COMMENT '商品图片URL',
    images TEXT COMMENT '商品多图，JSON格式',
    brand VARCHAR(100) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    weight DECIMAL(8, 2) COMMENT '重量(kg)',
    dimensions VARCHAR(100) COMMENT '尺寸',
    status TINYINT DEFAULT 1 COMMENT '状态：1-上架，0-下架',
    sales_count INT DEFAULT 0 COMMENT '销量',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ========================================
-- 购物车相关表
-- ========================================

-- 创建购物车表
CREATE TABLE IF NOT EXISTS cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    selected TINYINT DEFAULT 1 COMMENT '是否选中：1-选中，0-未选中',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ========================================
-- 订单相关表
-- ========================================

-- 创建订单表
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id INT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10, 2) NOT NULL COMMENT '实际支付金额',
    shipping_fee DECIMAL(10, 2) DEFAULT 0.00 COMMENT '运费',
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
    status TINYINT DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款',
    payment_method VARCHAR(50) COMMENT '支付方式：alipay-支付宝，wechat-微信，card-银行卡',
    payment_time DATETIME COMMENT '支付时间',
    shipping_address VARCHAR(500) COMMENT '收货地址',
    receiver_name VARCHAR(100) COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) COMMENT '收货人电话',
    remark VARCHAR(500) COMMENT '订单备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建订单项表
CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL COMMENT '订单ID',
    product_id INT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(500) COMMENT '商品图片',
    price DECIMAL(10, 2) NOT NULL COMMENT '商品单价',
    quantity INT NOT NULL COMMENT '购买数量',
    total_price DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ========================================
-- 插入示例数据
-- ========================================

-- 插入示例分类数据
INSERT INTO category (name, description, parent_id, sort_order) VALUES
('电子产品', '各类电子设备', 0, 1),
('服装服饰', '男女服装配饰', 0, 2),
('图书音像', '书籍、音乐、影视', 0, 3),
('家居用品', '家具、家纺、厨具', 0, 4),
('美妆个护', '化妆品、护肤品', 0, 5);

-- 插入子分类示例
INSERT INTO category (name, description, parent_id, sort_order) VALUES
('手机通讯', '智能手机、配件', 1, 1),
('电脑办公', '笔记本、台式机', 1, 2),
('男装', '男士服装', 2, 1),
('女装', '女士服装', 2, 2),
('小说文学', '各类小说作品', 3, 1);

-- 插入示例商品数据
INSERT INTO product (name, description, price, original_price, stock, category_id, image_url, brand, status, sales_count) VALUES
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB 深空黑色', 8999.00, 9999.00, 100, 6, '/images/iphone15pro.jpg', 'Apple', 1, 1500),
('MacBook Pro 14', 'Apple MacBook Pro 14英寸 M3芯片', 14999.00, 16999.00, 50, 7, '/images/macbookpro.jpg', 'Apple', 1, 800),
('男士休闲衬衫', '纯棉商务休闲长袖衬衫', 199.00, 299.00, 200, 8, '/images/menshirt.jpg', '雅戈尔', 1, 2000),
('女士连衣裙', '夏季新款雪纺连衣裙', 299.00, 399.00, 150, 9, '/images/womandress.jpg', '欧时力', 1, 1200),
('Java编程思想', 'Bruce Eckel著，经典Java学习书籍', 89.00, 108.00, 300, 10, '/images/javabook.jpg', '机械工业出版社', 1, 5000);

-- ========================================
-- 查询验证
-- ========================================

-- 查询所有用户
SELECT * FROM users;

-- 查询所有分类
SELECT * FROM category;

-- 查询所有商品
SELECT * FROM product;
