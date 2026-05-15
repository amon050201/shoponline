package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Address;
import java.util.List;

public interface AddressService {
    List<Address> findByUserId(Long userId);
    Address findById(Long id);
    Address addOrUpdate(Address address);
    boolean delete(Long id, Long userId);
    Address findDefault(Long userId);
}
