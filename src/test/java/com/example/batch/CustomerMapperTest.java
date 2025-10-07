package com.example.batch;

import com.example.batch.domain.Customer;
import com.example.batch.mapper.CustomerMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")  // src/test/resources/application-test.yml を使用
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2 などへの差替え禁止＝MySQLを使う
@Transactional // 各テスト後にロールバック（DBを汚さない）
class CustomerMapperTest {

    @Autowired
    CustomerMapper mapper;

    @Test
    void testInsertAndFindAll() {
        // INSERT
        Customer c1 = new Customer();
        c1.setName("Rion");
        c1.setCountry("Japan");
        mapper.insert(c1);

        Customer c2 = new Customer();
        c2.setName("Alice");
        c2.setCountry("US");
        mapper.insert(c2);

        // SELECT
        List<Customer> list = mapper.findAll();
        assertTrue(list.size() >= 2);

        // 値の一部確認（順序が保証されない場合もあるので contains 的に確認）
        assertTrue(list.stream().anyMatch(c -> "Rion".equals(c.getName()) && "Japan".equals(c.getCountry())));
        assertTrue(list.stream().anyMatch(c -> "Alice".equals(c.getName()) && "US".equals(c.getCountry())));
    }
}
