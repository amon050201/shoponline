-- ============================================
-- 线上购物与交易系统 - 完整数据库Schema
-- 对应 DatabaseInitializer.java 中的表结构
-- ============================================

CREATE DATABASE IF NOT EXISTS demo1_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE demo1_db;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    email VARCHAR(50),
    phone VARCHAR(20),
    role_type TINYINT NOT NULL DEFAULT 0 COMMENT '0-顾客 1-商家 2-管理员',
    status TINYINT DEFAULT 1 COMMENT '1-启用 0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认管理员账号 (密码: 123456, BCrypt加密)
-- 由 DatabaseInitializer 自动创建

-- ============================================
-- 2. 商品分类表
-- ============================================
CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    parent_id INT DEFAULT 0 COMMENT '0=顶级分类',
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================
-- 3. 商品表
-- ============================================
CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    stock INT DEFAULT 0,
    category_id INT,
    merchant_id BIGINT COMMENT '商家用户ID',
    image_url VARCHAR(500),
    images TEXT COMMENT '多图JSON',
    product_condition VARCHAR(50) COMMENT '商品成色',
    usage_years DECIMAL(4,1) COMMENT '使用年限',
    accessories TEXT COMMENT '配件信息',
    ai_tags TEXT COMMENT 'AI标签',
    brand VARCHAR(100),
    model VARCHAR(100),
    weight DECIMAL(8,2),
    dimensions VARCHAR(100),
    status TINYINT DEFAULT 1 COMMENT '1-上架 0-下架',
    sales_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================
-- 4. 购物车表
-- ============================================
CREATE TABLE IF NOT EXISTS cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 1,
    selected TINYINT DEFAULT 1 COMMENT '1-选中 0-未选中',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================
-- 5. 订单表
-- ============================================
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    actual_amount DECIMAL(10,2) NOT NULL,
    shipping_fee DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    status TINYINT DEFAULT 0 COMMENT '0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-退款中 6-已退款',
    payment_method VARCHAR(50) COMMENT 'alipay/wechat/card',
    payment_time DATETIME,
    transaction_id VARCHAR(100) COMMENT '支付交易号',
    shipping_address VARCHAR(500),
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(20),
    remark VARCHAR(500),
    shipping_company VARCHAR(100),
    tracking_number VARCHAR(100),
    delivery_time DATETIME,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================
-- 6. 订单项表
-- ============================================
CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ============================================
-- 7. 收货地址表
-- ============================================
CREATE TABLE IF NOT EXISTS address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    region VARCHAR(200) NOT NULL,
    detail_address VARCHAR(500) NOT NULL,
    is_default TINYINT DEFAULT 0,
    label VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ============================================
-- 8. 交换订单表
-- ============================================
CREATE TABLE IF NOT EXISTS exchange_order (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    initiator_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    initiator_product_id INT NOT NULL,
    receiver_product_id INT NOT NULL,
    price_difference DECIMAL(10,2) DEFAULT 0.00,
    status TINYINT DEFAULT 0 COMMENT '0-待确认 1-已确认 2-已完成 3-已取消 4-已拒绝',
    payment_status TINYINT DEFAULT 0,
    payment_method VARCHAR(50),
    payment_time DATETIME,
    confirm_time DATETIME,
    complete_time DATETIME,
    initiator_address VARCHAR(500),
    receiver_address VARCHAR(500),
    remark VARCHAR(500),
    ai_initiator_valuation DECIMAL(10,2),
    ai_receiver_valuation DECIMAL(10,2),
    recommended_difference DECIMAL(10,2),
    fraud_risk_score DOUBLE DEFAULT 0.0,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (initiator_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (initiator_product_id) REFERENCES product(id),
    FOREIGN KEY (receiver_product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交换订单表';

-- ============================================
-- 9. 用户行为表（推荐系统）
-- ============================================
CREATE TABLE IF NOT EXISTS user_behavior (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id INT DEFAULT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'view/search/add_cart/purchase',
    keyword VARCHAR(200) DEFAULT NULL,
    duration INT DEFAULT NULL COMMENT '停留时长(秒)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_action (user_id, action),
    INDEX idx_product_action (product_id, action),
    INDEX idx_created (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

-- ============================================
-- 10. 价格历史表（AI价格预测）
-- ============================================
CREATE TABLE IF NOT EXISTS price_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_date (product_id, recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格历史表';

-- ============================================
-- 11. 欺诈告警表（AI欺诈检测）
-- ============================================
CREATE TABLE IF NOT EXISTS fraud_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    risk_score DOUBLE DEFAULT 0.0,
    severity TINYINT DEFAULT 1,
    resolved TINYINT DEFAULT 0,
    reference_id VARCHAR(100),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_resolved (resolved, created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='欺诈告警表';

-- ============================================
-- 12. 交换估值表（AI估值缓存）
-- ============================================
CREATE TABLE IF NOT EXISTS exchange_valuation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exchange_order_id INT,
    product_id INT NOT NULL,
    scenario VARCHAR(50) NOT NULL,
    ai_estimated_value DECIMAL(10,2),
    market_range_low DECIMAL(10,2),
    market_range_high DECIMAL(10,2),
    condition_assessment VARCHAR(100),
    market_analysis TEXT,
    provider VARCHAR(50),
    factors JSON,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交换估值表';

-- ============================================
-- 示例数据
-- ============================================

-- 分类数据
INSERT INTO category (name, description, parent_id, sort_order) VALUES
('电子产品', '手机、电脑、数码配件等', 0, 1),
('家居用品', '家具、厨具、日用品等', 0, 2),
('服装鞋帽', '男装、女装、童装、鞋类等', 0, 3),
('图书文具', '书籍、文具、办公用品等', 0, 4),
('运动户外', '运动器材、户外装备等', 0, 5),
('美妆护肤', '化妆品、护肤品、个人护理等', 0, 6);

-- 商品数据
INSERT INTO product (name, description, price, original_price, stock, category_id, brand, status, sales_count, view_count) VALUES
('iPhone 15 Pro', 'Apple最新旗舰手机，A17 Pro芯片，钛金属设计', 8999.00, 9999.00, 50, 1, 'Apple', 1, 120, 1580),
('MacBook Air M3', '轻薄笔记本电脑，M3芯片，13.6英寸Liquid Retina显示屏', 10999.00, 12499.00, 30, 1, 'Apple', 1, 85, 2100),
('索尼降噪耳机 WH-1000XM5', '行业领先的降噪技术，30小时续航', 2499.00, 2999.00, 100, 1, 'Sony', 1, 200, 3200),
('北欧风格书桌', '实木打造，简约设计，1.2米大桌面', 1299.00, 1599.00, 20, 2, '北欧家居', 1, 45, 890),
('智能温控咖啡杯', '304不锈钢内胆，智能温控，长效保温', 199.00, 259.00, 200, 2, '小米生态链', 1, 310, 4500),
('纯棉T恤', '100%纯棉面料，舒适透气，多色可选', 89.00, 129.00, 500, 3, '优衣库', 1, 560, 8900),
('《深入理解Java虚拟机》', '第三版，全面解析JVM技术', 129.00, 149.00, 80, 4, '机械工业出版社', 1, 420, 6700),
('瑜伽垫', '加厚防滑，环保TPE材质，送收纳绑带', 99.00, 149.00, 150, 5, 'Keep', 1, 280, 3400),
('雅诗兰黛小棕瓶精华', '修护肌肤，淡化细纹，50ml', 699.00, 899.00, 60, 6, '雅诗兰黛', 1, 380, 5600);
