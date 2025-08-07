package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Test
    void existsByCatIdAndId() {

    }
}