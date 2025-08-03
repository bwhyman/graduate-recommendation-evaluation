package cn.nefu.ccec.graduaterecommendationevaluation.component;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

@SpringBootTest
@Slf4j
public class FileTest {

    @Test
    void test() {
        var path = "计控学院/计算机类/软件工程/张雅楠-2022223893/外语能力10-settings.xml";
        var x = Path.of(path);
        log.debug(x.getParent().toString());
        log.debug(x.getFileName().toString());
    }

}
