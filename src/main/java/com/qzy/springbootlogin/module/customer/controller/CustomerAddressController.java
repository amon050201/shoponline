package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Address;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.AddressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/address")
public class CustomerAddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public Result list(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        List<Address> list = addressService.findByUserId(userId);
        return Result.success("获取成功", list);
    }

    @GetMapping("/default")
    public Result getDefault(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        Address addr = addressService.findDefault(userId);
        return Result.success("获取成功", addr);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        Address addr = addressService.findById(id);
        if (addr == null) return Result.error("地址不存在");
        return Result.success("获取成功", addr);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Address address, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        address.setUserId(userId);
        Address saved = addressService.addOrUpdate(address);
        return Result.success("保存成功", saved);
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        boolean ok = addressService.delete(id, userId);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }
}
