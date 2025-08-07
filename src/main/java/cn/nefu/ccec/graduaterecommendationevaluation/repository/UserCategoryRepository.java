package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.UserCategory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserCategoryRepository extends ReactiveCrudRepository<UserCategory,Long> {

    @Query("""
            select count(*) from user_category t1 join user_category t2
            on t1.cat_id=t2.cat_id
            where t1.user_id=:sid and t2.user_id=:adminid;
            """)
    Mono<Integer> checkUserCategory(long sid, long adminid);
}
