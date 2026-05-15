package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerCategoryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/categories")
    public Result listCategories(@RequestParam(required = false) Integer parentId) {
        String sql;
        List<Map<String, Object>> rows;
        if (parentId != null) {
            sql = "SELECT id, name, description, parent_id as parentId, sort_order as sortOrder, status " +
                  "FROM category WHERE parent_id = ? AND status = 1 ORDER BY sort_order";
            rows = jdbcTemplate.queryForList(sql, parentId);
        } else {
            sql = "SELECT id, name, description, parent_id as parentId, sort_order as sortOrder, status " +
                  "FROM category WHERE status = 1 ORDER BY parent_id, sort_order";
            rows = jdbcTemplate.queryForList(sql);
        }
        return Result.success("获取成功", rows);
    }

    @GetMapping("/categories/{id}/subcategories")
    public Result getSubcategories(@PathVariable Integer id) {
        String sql = "SELECT id, name, description, parent_id as parentId, sort_order as sortOrder, status " +
                     "FROM category WHERE parent_id = ? AND status = 1 ORDER BY sort_order";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        return Result.success("获取成功", rows);
    }
}
