package cn.nefu.ccec.graduaterecommendationevaluation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class GraduateRecommendationEvaluationApplicationTests {

	@Test
	void contextLoads() {
		var random = System.currentTimeMillis() ;
		log.debug("{}", random);
	}

}
