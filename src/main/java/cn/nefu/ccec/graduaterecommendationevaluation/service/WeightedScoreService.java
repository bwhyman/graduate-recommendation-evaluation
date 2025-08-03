package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScore;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.WeightedScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WeightedScoreService {

    private final WeightedScoreRepository weightedScoreRepository;

    public Mono<WeightedScore> getWeightedScore(long uid) {
        return weightedScoreRepository.findById(uid);
    }

    @Transactional
    public Mono<WeightedScore> addWeightedScore(WeightedScore weightedScore) {
        weightedScore.setNew();
        return weightedScoreRepository.save(weightedScore);
    }

    @Transactional
    public Mono<Void> updateWeightedScore(long uid, float score, int ranking) {
        return weightedScoreRepository.updateWeightedScore(uid, score, ranking).then();
    }
}
