package com.example.batch.mapper;

import com.example.batch.domain.Customer;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper // ← ここ重要！
public interface CustomerMapper {
    List<Customer> findAll();
    int insert(Customer customer);
}
