package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Category;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

    @Query("""
            select * from category t1 where t1.coll_id=:collid
            """)
    Flux<Long> findCatIdsByCollId(long collid);

    @Query("""
            select t1.cat_id from user_category t1 where t1.user_id=:uid
            """)
    Flux<Long> findCatIdsByUid(long uid);

    @Query("""
            select * from category t1, user_category t2
            where t1.id=t2.cat_id and t2.user_id=:uid
            """)
    Flux<Category> findByUid(long uid);
}
