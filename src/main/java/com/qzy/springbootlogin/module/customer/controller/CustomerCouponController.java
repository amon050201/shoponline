package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/coupons")
public class CustomerCouponController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public Result listAvailableCoupons() {
        String sql = "SELECT id, name, description, type, value, min_amount as minAmount, " +
                "start_time as startTime, end_time as endTime, total_count as totalCount, " +
                "received_count as receivedCount " +
                "FROM coupon WHERE status = 1 AND start_time <= NOW() AND end_time >= NOW() " +
                "AND received_count < total_count ORDER BY value DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return Result.success("获取成功", rows);
    }

    @PostMapping("/receive/{couponId}")
    public Result receiveCoupon(@PathVariable Long couponId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        String couponSql = "SELECT id, total_count, received_count FROM coupon WHERE id = ? AND status = 1";
        List<Map<String, Object>> coupons = jdbcTemplate.queryForList(couponSql, couponId);
        if (coupons.isEmpty()) return Result.error("优惠券不存在或已失效");
        Map<String, Object> coupon = coupons.get(0);
        int totalCount = ((Number) coupon.get("total_count")).intValue();
        int receivedCount = ((Number) coupon.get("received_count")).intValue();
        if (receivedCount >= totalCount) return Result.error("优惠券已被领完");
        String checkSql = "SELECT COUNT(*) FROM user_coupon WHERE user_id = ? AND coupon_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, couponId);
        if (count != null && count > 0) return Result.error("已领取过该优惠券");
        jdbcTemplate.update("INSERT INTO user_coupon (user_id, coupon_id, status) VALUES (?, ?, 0)", userId, couponId);
        jdbcTemplate.update("UPDATE coupon SET received_count = received_count + 1 WHERE id = ?", couponId);
        return Result.success("领取成功");
    }

    @GetMapping("/my")
    public Result myCoupons(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        String sql = "SELECT uc.id, uc.coupon_id as couponId, c.name as couponName, c.type as couponType, " +
                "c.value as couponValue, c.min_amount as minAmount, uc.status, " +
                "uc.received_time as receivedTime, uc.used_time as usedTime " +
                "FROM user_coupon uc JOIN coupon c ON uc.coupon_id = c.id " +
                "WHERE uc.user_id = ? ORDER BY uc.received_time DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
        return Result.success("获取成功", rows);
    }
}
