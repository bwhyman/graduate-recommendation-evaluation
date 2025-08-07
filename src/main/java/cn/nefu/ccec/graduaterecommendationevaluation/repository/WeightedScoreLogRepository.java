package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScoreLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightedScoreLogRepository extends ReactiveCrudRepository<WeightedScoreLog, Long> {
}
