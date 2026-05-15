package com.qzy.springbootlogin.config;

import com.qzy.springbootlogin.ai.service.ProductSearchService;
import com.qzy.springbootlogin.service.ExchangeOrderService;
import com.qzy.springbootlogin.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化配置
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private ExchangeOrderService exchangeOrderService;

    @Autowired(required = false)
    private ProductSearchService productSearchService;

    @Override
    public void run(String... args) throws Exception {
        initDatabase();
        // 初始化交换订单表
        exchangeOrderService.initTable();
        // 初始化Elasticsearch索引
        initElasticsearch();
    }

    private void initElasticsearch() {
        if (productSearchService == null) {
            log.warn("ProductSearchService not available, skipping ES indexing");
            return;
        }
        try {
            productSearchService.indexAll();
            log.info("ES product indexing completed");
        } catch (Exception e) {
            log.warn("ES indexing failed (ES may not be running): {}", e.getMessage());
        }
    }

    /**
     * 初始化数据库表结构
     */
    private void initDatabase() {
        try {
            // 创建用户表（如果不存在）
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(50), " +
                    "phone VARCHAR(20), " +
                    "role_type TINYINT NOT NULL DEFAULT 0, " +
                    "status TINYINT DEFAULT 1, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            jdbcTemplate.execute(createTableSQL);
            System.out.println("用户表检查/创建成功");

            // 创建分类表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS category (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description VARCHAR(500), " +
                    "parent_id INT DEFAULT 0, " +
                    "sort_order INT DEFAULT 0, " +
                    "status TINYINT DEFAULT 1, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("分类表检查/创建成功");

            // 创建商品表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(200) NOT NULL, " +
                    "description TEXT, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "original_price DECIMAL(10,2), " +
                    "stock INT DEFAULT 0, " +
                    "category_id INT, " +
                    "merchant_id BIGINT, " +
                    "image_url VARCHAR(500), " +
                    "images TEXT, " +
                    "brand VARCHAR(100), " +
                    "model VARCHAR(100), " +
                    "weight DECIMAL(8,2), " +
                    "dimensions VARCHAR(100), " +
                    "status TINYINT DEFAULT 1, " +
                    "sales_count INT DEFAULT 0, " +
                    "view_count INT DEFAULT 0, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (category_id) REFERENCES category(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("商品表检查/创建成功");

            // 创建购物车表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS cart (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id BIGINT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "quantity INT DEFAULT 1, " +
                    "selected TINYINT DEFAULT 1, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "UNIQUE KEY uk_user_product (user_id, product_id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (product_id) REFERENCES product(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("购物车表检查/创建成功");

            // 创建订单表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "order_no VARCHAR(50) NOT NULL UNIQUE, " +
                    "user_id BIGINT NOT NULL, " +
                    "total_amount DECIMAL(10,2) NOT NULL, " +
                    "actual_amount DECIMAL(10,2) NOT NULL, " +
                    "shipping_fee DECIMAL(10,2) DEFAULT 0.00, " +
                    "discount_amount DECIMAL(10,2) DEFAULT 0.00, " +
                    "status TINYINT DEFAULT 0, " +
                    "payment_method VARCHAR(50), " +
                    "payment_time DATETIME, " +
                    "shipping_address VARCHAR(500), " +
                    "receiver_name VARCHAR(100), " +
                    "receiver_phone VARCHAR(20), " +
                    "remark VARCHAR(500), " +
                    "shipping_company VARCHAR(100), " +
                    "tracking_number VARCHAR(100), " +
                    "delivery_time DATETIME, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("订单表检查/创建成功");

            // 创建订单项表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS order_item (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "order_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "product_name VARCHAR(200) NOT NULL, " +
                    "product_image VARCHAR(500), " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "total_price DECIMAL(10,2) NOT NULL, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id), " +
                    "FOREIGN KEY (product_id) REFERENCES product(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("订单项表检查/创建成功");

            // 检查并插入默认管理员账户
            String checkAdminSQL = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            Integer count = jdbcTemplate.queryForObject(checkAdminSQL, Integer.class);
            if (count != null && count == 0) {
                String hashedPassword = PasswordUtil.hash("123456");
                String insertAdminSQL = "INSERT INTO users (username, password_hash, role_type, status) VALUES ('admin', '" + hashedPassword + "', 2, 1)";
                jdbcTemplate.execute(insertAdminSQL);
                System.out.println("默认管理员账户创建成功 (用户名: admin, 密码: 123456)");
            } else {
                System.out.println("默认管理员账户已存在");
            }

            // 添加支付交易号字段
            safeAddColumn("orders", "transaction_id", "VARCHAR(100) DEFAULT NULL AFTER payment_time");

            // 添加发货相关字段（兼容旧表）
            safeAddColumn("orders", "shipping_company", "VARCHAR(100) DEFAULT NULL AFTER remark");
            safeAddColumn("orders", "tracking_number", "VARCHAR(100) DEFAULT NULL AFTER shipping_company");
            safeAddColumn("orders", "delivery_time", "DATETIME DEFAULT NULL AFTER tracking_number");

            // 创建用户行为表（推荐系统用）
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_behavior (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "product_id INT DEFAULT NULL, " +
                    "action VARCHAR(20) NOT NULL COMMENT 'view/search/add_cart/purchase', " +
                    "keyword VARCHAR(200) DEFAULT NULL, " +
                    "duration INT DEFAULT NULL COMMENT '停留时长(秒)', " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "INDEX idx_user_action (user_id, action), " +
                    "INDEX idx_product_action (product_id, action), " +
                    "INDEX idx_created (created_time)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("用户行为表检查/创建成功");

            // 创建价格历史表（AI价格预测用）
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS price_history (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "product_id INT NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "INDEX idx_product_date (product_id, recorded_at)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("价格历史表检查/创建成功");

            // 创建欺诈告警表（AI欺诈检测用）
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS fraud_alert (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "alert_type VARCHAR(50) NOT NULL, " +
                    "description VARCHAR(500) NOT NULL, " +
                    "risk_score DOUBLE DEFAULT 0.0, " +
                    "severity TINYINT DEFAULT 1, " +
                    "resolved TINYINT DEFAULT 0, " +
                    "reference_id VARCHAR(100), " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "INDEX idx_user (user_id), " +
                    "INDEX idx_resolved (resolved, created_time)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("欺诈告警表检查/创建成功");

            // 创建交换估值表（AI估值缓存）
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS exchange_valuation (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "exchange_order_id INT, " +
                    "product_id INT NOT NULL, " +
                    "scenario VARCHAR(50) NOT NULL, " +
                    "ai_estimated_value DECIMAL(10,2), " +
                    "market_range_low DECIMAL(10,2), " +
                    "market_range_high DECIMAL(10,2), " +
                    "condition_assessment VARCHAR(100), " +
                    "market_analysis TEXT, " +
                    "provider VARCHAR(50), " +
                    "factors JSON, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("交换估值表检查/创建成功");

            // 创建收藏表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS favorite (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE KEY uk_user_product (user_id, product_id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (product_id) REFERENCES product(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("收藏表检查/创建成功");

            // 创建评价表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS review (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "order_id INT DEFAULT NULL, " +
                    "rating TINYINT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "images TEXT DEFAULT NULL, " +
                    "status TINYINT DEFAULT 1, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (product_id) REFERENCES product(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("评价表检查/创建成功");

            // 创建优惠券表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS coupon (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description VARCHAR(500), " +
                    "type VARCHAR(20) NOT NULL DEFAULT 'fixed' COMMENT 'fixed=满减 discount=折扣', " +
                    "value DECIMAL(10,2) NOT NULL, " +
                    "min_amount DECIMAL(10,2) DEFAULT 0.00, " +
                    "start_time DATETIME NOT NULL, " +
                    "end_time DATETIME NOT NULL, " +
                    "total_count INT DEFAULT 1000, " +
                    "received_count INT DEFAULT 0, " +
                    "status TINYINT DEFAULT 1, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("优惠券表检查/创建成功");

            // 创建用户优惠券表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_coupon (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "coupon_id BIGINT NOT NULL, " +
                    "status TINYINT DEFAULT 0 COMMENT '0=未使用 1=已使用 2=已过期', " +
                    "received_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "used_time DATETIME DEFAULT NULL, " +
                    "UNIQUE KEY uk_user_coupon (user_id, coupon_id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (coupon_id) REFERENCES coupon(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("用户优惠券表检查/创建成功");

            // 创建通知表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS notification (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "content VARCHAR(1000) NOT NULL, " +
                    "type VARCHAR(50) DEFAULT 'system' COMMENT 'system/order/promotion', " +
                    "reference_id VARCHAR(100) DEFAULT NULL, " +
                    "is_read TINYINT DEFAULT 0, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "INDEX idx_user_read (user_id, is_read, created_time)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("通知表检查/创建成功");

            // 创建Banner表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS banner (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(100) NOT NULL, " +
                    "image_url VARCHAR(500) NOT NULL, " +
                    "link_url VARCHAR(500) DEFAULT NULL, " +
                    "sort_order INT DEFAULT 0, " +
                    "status TINYINT DEFAULT 1, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("Banner表检查/创建成功");

            // 创建搜索历史表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS search_history (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "keyword VARCHAR(200) NOT NULL, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "INDEX idx_user_time (user_id, created_time)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("搜索历史表检查/创建成功");

            // 创建反馈表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS feedback (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "contact VARCHAR(100) DEFAULT NULL, " +
                    "images TEXT DEFAULT NULL, " +
                    "status TINYINT DEFAULT 0 COMMENT '0=待处理 1=已处理 2=已回复', " +
                    "reply TEXT DEFAULT NULL, " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("反馈表检查/创建成功");

            // 创建收货地址表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS address (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL, " +
                    "receiver_name VARCHAR(50) NOT NULL, " +
                    "receiver_phone VARCHAR(20) NOT NULL, " +
                    "region VARCHAR(200) NOT NULL, " +
                    "detail_address VARCHAR(500) NOT NULL, " +
                    "is_default TINYINT DEFAULT 0, " +
                    "label VARCHAR(50), " +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("收货地址表检查/创建成功");

            // 为product表添加AI相关列（兼容旧表）
            safeAddColumn("product", "product_condition", "VARCHAR(50) DEFAULT NULL AFTER images");
            safeAddColumn("product", "usage_years", "DECIMAL(4,1) DEFAULT NULL AFTER product_condition");
            safeAddColumn("product", "accessories", "TEXT DEFAULT NULL AFTER usage_years");
            safeAddColumn("product", "ai_tags", "TEXT DEFAULT NULL AFTER accessories");

            // 为exchange_order表添加AI估值相关列
            safeAddColumn("exchange_order", "ai_initiator_valuation", "DECIMAL(10,2) DEFAULT NULL AFTER remark");
            safeAddColumn("exchange_order", "ai_receiver_valuation", "DECIMAL(10,2) DEFAULT NULL AFTER ai_initiator_valuation");
            safeAddColumn("exchange_order", "recommended_difference", "DECIMAL(10,2) DEFAULT NULL AFTER ai_receiver_valuation");
            safeAddColumn("exchange_order", "fraud_risk_score", "DOUBLE DEFAULT 0.0 AFTER recommended_difference");

            // 插入默认分类数据
            Integer categoryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category", Integer.class);
            if (categoryCount != null && categoryCount == 0) {
                jdbcTemplate.execute("INSERT INTO category (name, description, parent_id, sort_order) VALUES " +
                        "('电子产品', '手机、电脑、数码配件等', 0, 1), " +
                        "('家居用品', '家具、厨具、日用品等', 0, 2), " +
                        "('服装鞋帽', '男装、女装、童装、鞋类等', 0, 3), " +
                        "('图书文具', '书籍、文具、办公用品等', 0, 4), " +
                        "('运动户外', '运动器材、户外装备等', 0, 5), " +
                        "('美妆护肤', '化妆品、护肤品、个人护理等', 0, 6)");
                System.out.println("默认分类数据插入成功");
            } else {
                System.out.println("分类数据已存在");
            }

            // 插入默认优惠券数据
            Integer couponCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM coupon", Integer.class);
            if (couponCount != null && couponCount == 0) {
                jdbcTemplate.execute("INSERT INTO coupon (name, description, type, value, min_amount, start_time, end_time, total_count, status) VALUES " +
                        "('新用户专享券', '新用户首单满99减20', 'fixed', 20.00, 99.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1000, 1), " +
                        "('满200减30', '全场通用满减券', 'fixed', 30.00, 200.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 500, 1), " +
                        "('满500减80', '全场通用满减券', 'fixed', 80.00, 500.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 300, 1), " +
                        "('9折优惠券', '全场通用折扣券，最高减100', 'discount', 10.00, 0.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 200, 1)");
                System.out.println("默认优惠券数据插入成功");
            }

            // 插入默认Banner数据
            Integer bannerCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM banner", Integer.class);
            if (bannerCount != null && bannerCount == 0) {
                jdbcTemplate.execute("INSERT INTO banner (title, image_url, link_url, sort_order, status) VALUES " +
                        "('新品首发', '/images/default-product.svg', '/product/list', 1, 1), " +
                        "('限时特惠', '/images/default-product.svg', '/product/list', 2, 1), " +
                        "('以物易物', '/images/default-product.svg', '/exchange/market', 3, 1)");
                System.out.println("默认Banner数据插入成功");
            }

            // 插入默认商品数据
            Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product", Integer.class);
            if (productCount != null && productCount == 0) {
                jdbcTemplate.execute("INSERT INTO product (name, description, price, original_price, stock, category_id, brand, status, sales_count, view_count) VALUES " +
                        "('iPhone 15 Pro', 'Apple最新旗舰手机，A17 Pro芯片，钛金属设计', 8999.00, 9999.00, 50, 1, 'Apple', 1, 120, 1580), " +
                        "('MacBook Air M3', '轻薄笔记本电脑，M3芯片，13.6英寸Liquid Retina显示屏', 10999.00, 12499.00, 30, 1, 'Apple', 1, 85, 2100), " +
                        "('索尼降噪耳机 WH-1000XM5', '行业领先的降噪技术，30小时续航', 2499.00, 2999.00, 100, 1, 'Sony', 1, 200, 3200), " +
                        "('北欧风格书桌', '实木打造，简约设计，1.2米大桌面', 1299.00, 1599.00, 20, 2, '北欧家居', 1, 45, 890), " +
                        "('智能温控咖啡杯', '304不锈钢内胆，智能温控，长效保温', 199.00, 259.00, 200, 2, '小米生态链', 1, 310, 4500), " +
                        "('纯棉T恤', '100%纯棉面料，舒适透气，多色可选', 89.00, 129.00, 500, 3, '优衣库', 1, 560, 8900), " +
                        "('《深入理解Java虚拟机》', '第三版，全面解析JVM技术', 129.00, 149.00, 80, 4, '机械工业出版社', 1, 420, 6700), " +
                        "('瑜伽垫', '加厚防滑，环保TPE材质，送收纳绑带', 99.00, 149.00, 150, 5, 'Keep', 1, 280, 3400), " +
                        "('雅诗兰黛小棕瓶精华', '修护肌肤，淡化细纹，50ml', 699.00, 899.00, 60, 6, '雅诗兰黛', 1, 380, 5600)");
                System.out.println("默认商品数据插入成功");
            } else {
                System.out.println("商品数据已存在");
            }
        } catch (Exception e) {
            System.err.println("数据库初始化失败！");
            e.printStackTrace();
        }
    }

    /**
     * 安全添加列（兼容空表场景）
     */
    private void safeAddColumn(String table, String column, String definition) {
        try {
            jdbcTemplate.queryForList("SELECT " + column + " FROM " + table + " LIMIT 1");
        } catch (Exception e) {
            try {
                jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                System.out.println("添加列 " + table + "." + column + " 成功");
            } catch (Exception ex) {
                System.err.println("添加列 " + table + "." + column + " 失败: " + ex.getMessage());
            }
        }
    }
}
