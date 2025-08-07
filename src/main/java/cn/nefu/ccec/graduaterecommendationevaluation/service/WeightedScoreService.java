package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScore;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScoreLog;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.WeightedScoreLogRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.WeightedScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WeightedScoreService {

    private final WeightedScoreRepository weightedScoreRepository;
    private final TransactionalOperator transactionalOperator;
    private final WeightedScoreLogRepository weightedScoreLogRepository;

    public Mono<WeightedScore> getWeightedScore(long uid) {
        return weightedScoreRepository.findById(uid);
    }


    public Mono<WeightedScore> addWeightedScore(WeightedScore weightedScore) {
        weightedScore.setNew();
        return weightedScoreRepository.save(weightedScore)
                .as(transactionalOperator::transactional);
    }

    public Mono<Void> updateWeightedScore(long uid, float score, int ranking, int verified) {
        return weightedScoreRepository.updateWeightedScore(uid, score, ranking, verified)
                .then()
                .as(transactionalOperator::transactional);
    }

   public Mono<Void> updateWeightedScore(long sid, float score, int ranking, int verified, WeightedScoreLog log) {
        return weightedScoreRepository.updateWeightedScore(sid, score, ranking, verified)
                .flatMap(r -> weightedScoreLogRepository.save(log))
                .then()
                .as(transactionalOperator::transactional);
   }

}
