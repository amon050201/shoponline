package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("ProductService")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("returns non-empty product list")
        void returnsProducts() {
            List<Product> list = productService.findAll();
            assertNotNull(list);
            assertTrue(list.size() >= 9);
            list.forEach(p -> assertEquals(1, (int) p.getStatus()));
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("existing product found")
        void found() {
            Product p = productService.findById(1);
            assertNotNull(p);
            assertEquals("iPhone 15 Pro", p.getName());
        }

        @Test
        @DisplayName("nonexistent returns null")
        void notFound() {
            assertNull(productService.findById(99999));
        }
    }

    @Nested
    @DisplayName("findByCategoryId")
    class FindByCategory {

        @Test
        @DisplayName("returns products in category")
        void byCategory() {
            List<Product> list = productService.findByCategoryId(1);
            assertNotNull(list);
            assertFalse(list.isEmpty());
            list.forEach(p -> assertEquals(1, (int) p.getCategoryId()));
        }

        @Test
        @DisplayName("empty category returns empty list")
        void emptyCategory() {
            assertTrue(productService.findByCategoryId(999).isEmpty());
        }
    }

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("searchByName returns matches")
        void byName() {
            List<Product> list = productService.searchByName("iPhone");
            assertFalse(list.isEmpty());
            assertTrue(list.get(0).getName().contains("iPhone"));
        }

        @Test
        @DisplayName("searchByName no match returns empty")
        void noMatch() {
            assertTrue(productService.searchByName("xyzxyz_nomatch").isEmpty());
        }

        @Test
        @DisplayName("smartSearch ranks by relevance")
        void smartSearch() {
            List<Product> list = productService.smartSearch("手机");
            assertFalse(list.isEmpty());
            boolean hasIPhone = list.stream().anyMatch(p -> p.getName().contains("iPhone"));
            assertTrue(hasIPhone);
        }

        @Test
        @DisplayName("smartSearch empty for no match")
        void smartNoMatch() {
            assertTrue(productService.smartSearch("xyzxyz_nomatch").isEmpty());
        }
    }

    @Nested
    @DisplayName("CRUD")
    class Crud {

        @Test
        @DisplayName("add product")
        void add() {
            Product p = new Product();
            p.setName("Test_Add_Product");
            p.setPrice(new BigDecimal("99.99"));
            p.setStock(10);
            p.setCategoryId(1);

            assertEquals(1, productService.addProduct(p));
            assertNotNull(p.getId());
            assertEquals("Test_Add_Product", productService.findById(p.getId()).getName());
        }

        @Test
        @DisplayName("update product")
        void update() {
            Product p = new Product();
            p.setName("Before_Update");
            p.setPrice(new BigDecimal("50.00"));
            p.setStock(5);
            p.setCategoryId(1);
            productService.addProduct(p);

            p.setName("After_Update");
            p.setPrice(new BigDecimal("199.99"));
            assertEquals(1, productService.updateProduct(p));

            Product updated = productService.findById(p.getId());
            assertEquals("After_Update", updated.getName());
            assertEquals(0, new BigDecimal("199.99").compareTo(updated.getPrice()));
        }

        @Test
        @DisplayName("delete product")
        void delete() {
            Product p = new Product();
            p.setName("To_Delete");
            p.setPrice(new BigDecimal("10.00"));
            p.setStock(1);
            p.setCategoryId(1);
            productService.addProduct(p);

            assertEquals(1, productService.deleteProduct(p.getId()));
            assertNull(productService.findById(p.getId()));
        }

        @Test
        @DisplayName("add product defaults status to 1")
        void defaultStatus() {
            Product p = new Product();
            p.setName("Status_Default");
            p.setPrice(new BigDecimal("20.00"));
            p.setStock(10);
            p.setCategoryId(1);
            productService.addProduct(p);
            assertEquals(1, (int) productService.findById(p.getId()).getStatus());
        }
    }
}
