package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dto.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Test
    void find() {
        //var id = 1;
        //var id = 2;
        var id = 3;
        UserInfoDTO userInfoDTO = userRepository.find(1399271139387179008L).block();
        log.info(userInfoDTO.toString());
    }
}