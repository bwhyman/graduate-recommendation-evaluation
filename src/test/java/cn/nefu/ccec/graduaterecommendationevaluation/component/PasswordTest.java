package cn.nefu.ccec.graduaterecommendationevaluation.component;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Slf4j
@SpringBootTest
public class PasswordTest {
    @Autowired
    private PasswordEncoder encoder;

    @Test
    void test() {
        var str = "1000002720";
        String encode = encoder.encode(str);
        log.debug(encode);
    }
    @Test
    void test1() {
        UUID uuid = UUID.randomUUID();
        log.debug(uuid.toString());
    }
}
