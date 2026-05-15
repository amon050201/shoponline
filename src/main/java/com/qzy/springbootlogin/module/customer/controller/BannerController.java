package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BannerController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/banners")
    public Result listBanners() {
        String sql = "SELECT id, title, image_url as imageUrl, link_url as linkUrl, " +
                "sort_order as sortOrder FROM banner WHERE status = 1 ORDER BY sort_order";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return Result.success("获取成功", rows);
    }
}
