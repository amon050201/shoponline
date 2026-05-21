package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
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
@DisplayName("UserService")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("correct password succeeds")
        void loginSucceeds() {
            Result<User> r = userService.login("admin", "123456");
            assertTrue(r.isSuccess());
            assertEquals("admin", r.getData().getUsername());
            assertEquals(2, r.getData().getRoleType());
        }

        @Test
        @DisplayName("wrong password fails")
        void loginFailsOnWrongPwd() {
            Result<User> r = userService.login("admin", "wrongpwd");
            assertFalse(r.isSuccess());
        }

        @Test
        @DisplayName("empty fields fail")
        void loginFailsOnEmpty() {
            assertFalse(userService.login("", "123456").isSuccess());
        }

        @Test
        @DisplayName("nonexistent user fails")
        void loginFailsOnUnknownUser() {
            assertFalse(userService.login("not_exist_xyz", "123456").isSuccess());
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("valid info succeeds")
        void registerSucceeds() {
            User u = new User("reg_tester", "pass123", 0, 1);
            assertTrue(userService.register(u).isSuccess());
            assertTrue(userService.login("reg_tester", "pass123").isSuccess());
        }

        @Test
        @DisplayName("duplicate username fails")
        void duplicateFails() {
            userService.register(new User("dup_usr", "pass123", 0, 1));
            Result<Void> r = userService.register(new User("dup_usr", "pass456", 0, 1));
            assertFalse(r.isSuccess());
        }

        @Test
        @DisplayName("short password fails")
        void shortPwdFails() {
            assertFalse(userService.register(new User("sp_user", "12", 0, 1)).isSuccess());
        }

        @Test
        @DisplayName("empty username fails")
        void emptyNameFails() {
            assertFalse(userService.register(new User("", "pass123", 0, 1)).isSuccess());
        }
    }

    @Nested
    @DisplayName("validateRegistration")
    class Validation {

        @Test
        @DisplayName("blank username")
        void blankName() {
            assertNotNull(userService.validateRegistration("  ", "123456", "123456"));
        }

        @Test
        @DisplayName("too long username")
        void longName() {
            String s = "a".repeat(21);
            assertNotNull(userService.validateRegistration(s, "123456", "123456"));
        }

        @Test
        @DisplayName("short password")
        void shortPwd() {
            String e = userService.validateRegistration("okuser", "12", "12");
            assertNotNull(e);
            assertTrue(e.contains("密码"));
        }

        @Test
        @DisplayName("mismatched passwords")
        void mismatch() {
            String e = userService.validateRegistration("okuser", "123456", "654321");
            assertNotNull(e);
            assertTrue(e.contains("不一致"));
        }

        @Test
        @DisplayName("all valid")
        void allValid() {
            assertNull(userService.validateRegistration("new_user", "pass123", "pass123"));
        }
    }

    @Nested
    @DisplayName("CRUD")
    class Crud {

        @Test
        @DisplayName("list returns users")
        void listUsers() {
            List<User> users = userService.list();
            assertNotNull(users);
            assertTrue(users.size() > 0);
        }

        @Test
        @DisplayName("find by id")
        void findById() {
            assertEquals("admin", userService.getUserById(1L).getUsername());
        }

        @Test
        @DisplayName("id not found returns null")
        void notFound() {
            assertNull(userService.getUserById(99999L));
        }

        @Test
        @DisplayName("add user")
        void addUser() {
            User u = new User("added_usr", "pass123", 0, 1);
            assertTrue(userService.addUser(u).isSuccess());
        }

        private Long resolveId(String username) {
            User u = userService.login(username, "pass123").getData();
            if (u != null) return u.getId();
            // fallback: scan all users for the username
            for (User user : userService.list()) {
                if (username.equals(user.getUsername())) return user.getId();
            }
            return null;
        }

        @Test
        @DisplayName("update user")
        void updateUser() {
            userService.addUser(new User("upd_usr", "pass123", 0, 1));
            Long id = resolveId("upd_usr");
            User u = userService.getUserById(id);
            u.setEmail("new@test.com");
            u.setPasswordHash("");
            assertTrue(userService.updateUser(u).isSuccess());
            assertEquals("new@test.com", userService.getUserById(id).getEmail());
        }

        @Test
        @DisplayName("delete user")
        void deleteUser() {
            userService.addUser(new User("del_me", "pass123", 0, 1));
            Long id = resolveId("del_me");
            assertTrue(userService.deleteUser(id).isSuccess());
            assertNull(userService.getUserById(id));
        }

        @Test
        @DisplayName("cannot delete admin")
        void cannotDeleteAdmin() {
            Result<Void> r = userService.deleteUser(1L);
            assertFalse(r.isSuccess());
        }

        @Test
        @DisplayName("delete nonexistent fails")
        void deleteNotFound() {
            assertFalse(userService.deleteUser(99999L).isSuccess());
        }

        @Test
        @DisplayName("update role")
        void updateRole() {
            userService.addUser(new User("role_usr", "pass123", 0, 1));
            Long id = resolveId("role_usr");
            assertTrue(userService.updateUserRole(id, 1));
            assertEquals(1, (int) userService.getUserById(id).getRoleType());
        }
    }
}
