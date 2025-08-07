package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScore;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WeightedScoreRepository extends ReactiveCrudRepository<WeightedScore, Long> {

    @Modifying
    @Query("""
            update weighted_score t1
            set t1.score=:score, t1.ranking=:ranking, t1.verified=:verified
            where t1.id=:uid
            """)
    Mono<Integer> updateWeightedScore(long uid, float score, int ranking, int verified);

}
