package com.qzy.springbootlogin.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * 验证码工具类
 */
public class CaptchaUtil {
    
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    /**
     * 生成验证码图片和文本
     * @return 包含验证码图片和文本的对象
     */
    public static CaptchaResult generateCaptcha() {
        // 创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        
        // 设置背景色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 设置字体
        Font font = new Font("Arial", Font.BOLD, 24);
        graphics.setFont(font);
        
        // 生成随机验证码
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            String charStr = String.valueOf(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            codeBuilder.append(charStr);
            
            // 为每个字符设置随机颜色
            graphics.setColor(new Color(
                random.nextInt(100),
                random.nextInt(100),
                random.nextInt(100)
            ));
            
            // 绘制字符，带随机位置偏移
            int x = 20 + i * 25;
            int y = 28 + random.nextInt(5);
            graphics.drawString(charStr, x, y);
        }
        
        String code = codeBuilder.toString();
        
        // 添加干扰线
        for (int i = 0; i < 5; i++) {
            graphics.setColor(new Color(
                random.nextInt(150) + 50,
                random.nextInt(150) + 50,
                random.nextInt(150) + 50
            ));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            graphics.drawLine(x1, y1, x2, y2);
        }
        
        // 添加噪点
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            graphics.setColor(new Color(
                random.nextInt(200),
                random.nextInt(200),
                random.nextInt(200)
            ));
            graphics.fillRect(x, y, 2, 2);
        }
        
        graphics.dispose();
        
        // 转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", baos);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
        
        return new CaptchaResult(code, baos.toByteArray());
    }
    
    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        private final String code;
        private final byte[] imageData;
        
        public CaptchaResult(String code, byte[] imageData) {
            this.code = code;
            this.imageData = imageData;
        }
        
        public String getCode() {
            return code;
        }
        
        public byte[] getImageData() {
            return imageData;
        }
    }
}
