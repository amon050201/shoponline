package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.mapper.AddressMapper;
import com.qzy.springbootlogin.pojo.Address;
import com.qzy.springbootlogin.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> findByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address findById(Long id) {
        return addressMapper.findById(id);
    }

    @Override
    @Transactional
    public Address addOrUpdate(Address address) {
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(address.getUserId());
        }
        if (address.getId() != null && address.getId() > 0) {
            addressMapper.update(address);
        } else {
            addressMapper.insert(address);
        }
        return address;
    }

    @Override
    @Transactional
    public boolean delete(Long id, Long userId) {
        Address addr = addressMapper.findById(id);
        if (addr == null || !addr.getUserId().equals(userId)) {
            return false;
        }
        addressMapper.delete(id);
        return true;
    }

    @Override
    public Address findDefault(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }
}
