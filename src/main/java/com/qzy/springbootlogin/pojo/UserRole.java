package com.qzy.springbootlogin.pojo;

/**
 * 用户角色枚举
 */
public enum UserRole {
    CUSTOMER(0, "顾客"),
    MERCHANT(1, "商家"),
    ADMIN(2, "管理员");
    
    private final int code;
    private final String name;
    
    UserRole(int code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * 根据代码获取角色
     */
    public static UserRole fromCode(int code) {
        for (UserRole role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return CUSTOMER; // 默认为顾客
    }
    
    /**
     * 根据代码获取角色名称
     */
    public static String getNameByCode(int code) {
        return fromCode(code).getName();
    }
}
