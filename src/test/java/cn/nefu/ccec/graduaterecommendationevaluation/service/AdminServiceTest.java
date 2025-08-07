package cn.nefu.ccec.graduaterecommendationevaluation.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class AdminServiceTest {
    @Autowired
    private AdminService adminService;

    @Test
    void listAdmins() {
        adminService.listAdmins(1397954199536336896L, "Rcz9N")
                .block()
                .forEach(admin -> log.info("admin={}", admin));
    }
}