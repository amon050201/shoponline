package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Address;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.AddressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/address")
public class AddressWebController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        List<Address> addresses = addressService.findByUserId(userId);
        model.addAttribute("addresses", addresses);
        return "pages/address/list";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam(required = false) Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        if (id != null) {
            Address addr = addressService.findById(id);
            if (addr != null && addr.getUserId().equals(userId)) {
                model.addAttribute("address", addr);
            }
        }
        return "pages/address/edit";
    }

    @PostMapping("/save")
    public String save(Address address, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        address.setUserId(userId);
        addressService.addOrUpdate(address);
        return "redirect:/address/list";
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Result delete(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        boolean ok = addressService.delete(id, userId);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/set-default/{id}")
    @ResponseBody
    public Result setDefault(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        Address addr = addressService.findById(id);
        if (addr == null || !addr.getUserId().equals(userId)) {
            return Result.error("地址不存在");
        }
        addr.setIsDefault(1);
        addressService.addOrUpdate(addr);
        return Result.success("设置成功");
    }

    @GetMapping("/select-json")
    @ResponseBody
    public Result listJson(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        return Result.success("获取成功", addressService.findByUserId(userId));
    }
}
