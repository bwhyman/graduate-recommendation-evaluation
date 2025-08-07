package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByCatIds() {
        categoryRepository.findCatIdsByCollId(1397992599949737984L).collectList()
                .block()
                .forEach(System.out::println);
    }
}