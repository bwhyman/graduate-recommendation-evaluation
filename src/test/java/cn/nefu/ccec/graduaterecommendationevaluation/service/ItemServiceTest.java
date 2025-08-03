package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Test
    void listChildrenItems() {
        //itemService.listChildrenItems(1397992599949737984L, 1398322693683609600L)
    }
    @Test
    void test2() {

    }

}