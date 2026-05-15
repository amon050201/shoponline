package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    List<Address> findByUserId(Long userId);

    Address findById(Long id);

    int insert(Address address);

    int update(Address address);

    int delete(Long id);

    void clearDefaultByUserId(Long userId);

    Address findDefaultByUserId(Long userId);
}
