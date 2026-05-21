package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("CartService")
class CartServiceTest {

    private static final int TEST_USER = 1;
    private static final int PRODUCT_A = 1;
    private static final int PRODUCT_B = 2;
    private static final int PRODUCT_C = 3;

    @Autowired
    private CartService cartService;

    @Nested
    @DisplayName("addToCart")
    class AddToCart {

        @Test
        @DisplayName("new item inserted")
        void newItem() {
            cartService.clearCart(TEST_USER);

            Cart cart = new Cart();
            cart.setUserId(TEST_USER);
            cart.setProductId(PRODUCT_B);
            cart.setQuantity(2);
            assertEquals(1, cartService.addToCart(cart));

            List<Cart> items = cartService.getCartByUserId(TEST_USER);
            assertEquals(1, items.size());
            assertEquals(2, (int) items.get(0).getQuantity());
        }

        @Test
        @DisplayName("existing item merges quantity")
        void mergeQuantity() {
            cartService.clearCart(TEST_USER);

            Cart c = new Cart();
            c.setUserId(TEST_USER);
            c.setProductId(PRODUCT_A);
            c.setQuantity(1);
            cartService.addToCart(c);
            c.setQuantity(3);
            cartService.addToCart(c);

            List<Cart> items = cartService.getCartByUserId(TEST_USER);
            assertEquals(1, items.size());
            assertEquals(4, (int) items.get(0).getQuantity());
        }

        @Test
        @DisplayName("defaults selected to 1")
        void defaultSelected() {
            cartService.clearCart(TEST_USER);

            Cart cart = new Cart();
            cart.setUserId(TEST_USER);
            cart.setProductId(PRODUCT_C);
            cart.setQuantity(1);
            cartService.addToCart(cart);

            assertEquals(1, (int) cartService.getCartByUserId(TEST_USER).get(0).getSelected());
        }
    }

    @Nested
    @DisplayName("getCartByUserId")
    class GetCart {

        @Test
        @DisplayName("returns items with product data")
        void withProductData() {
            cartService.clearCart(TEST_USER);

            Cart c = new Cart();
            c.setUserId(TEST_USER);
            c.setProductId(PRODUCT_A);
            c.setQuantity(1);
            cartService.addToCart(c);
            c.setProductId(PRODUCT_B);
            cartService.addToCart(c);

            List<Cart> items = cartService.getCartByUserId(TEST_USER);
            assertTrue(items.size() >= 2);
            assertNotNull(items.get(0).getProduct());
        }

        @Test
        @DisplayName("empty for no items")
        void empty() {
            assertTrue(cartService.getCartByUserId(99999).isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("update quantity")
        void updateQty() {
            cartService.clearCart(TEST_USER);

            Cart c = new Cart();
            c.setUserId(TEST_USER);
            c.setProductId(PRODUCT_A);
            c.setQuantity(1);
            cartService.addToCart(c);

            int cartId = cartService.getCartByUserId(TEST_USER).get(0).getId();
            cartService.updateQuantity(cartId, 5);
            assertEquals(5, (int) cartService.getCartById(cartId).getQuantity());
        }

        @Test
        @DisplayName("zero quantity deletes cart item")
        void zeroDeletes() {
            cartService.clearCart(TEST_USER);

            Cart c = new Cart();
            c.setUserId(TEST_USER);
            c.setProductId(PRODUCT_A);
            c.setQuantity(1);
            cartService.addToCart(c);

            int cartId = cartService.getCartByUserId(TEST_USER).get(0).getId();
            cartService.updateQuantity(cartId, 0);
            assertNull(cartService.getCartById(cartId));
        }

        @Test
        @DisplayName("update selected")
        void updateSelected() {
            cartService.clearCart(TEST_USER);

            Cart c = new Cart();
            c.setUserId(TEST_USER);
            c.setProductId(PRODUCT_A);
            c.setQuantity(1);
            cartService.addToCart(c);

            int cartId = cartService.getCartByUserId(TEST_USER).get(0).getId();
            cartService.updateSelected(cartId, 0);
            assertEquals(0, (int) cartService.getCartById(cartId).getSelected());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove single item")
        void remove() {
            cartService.clearCart(TEST_USER);

            Cart c = new Cart();
            c.setUserId(TEST_USER);
            c.setProductId(PRODUCT_A);
            c.setQuantity(1);
            cartService.addToCart(c);

            int countBefore = cartService.getCartByUserId(TEST_USER).size();
            cartService.removeFromCart(cartService.getCartByUserId(TEST_USER).get(0).getId());
            assertEquals(countBefore - 1, cartService.getCartByUserId(TEST_USER).size());
        }

        @Test
        @DisplayName("clear all items")
        void clearAll() {
            Cart c = new Cart();
            c.setUserId(TEST_USER);
            c.setProductId(PRODUCT_A);
            c.setQuantity(1);
            cartService.addToCart(c);
            c.setProductId(PRODUCT_B);
            cartService.addToCart(c);

            cartService.clearCart(TEST_USER);
            assertTrue(cartService.getCartByUserId(TEST_USER).isEmpty());
        }
    }
}
