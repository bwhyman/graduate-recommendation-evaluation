package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.UserCategory;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.AdminDO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserCategoryRepository extends ReactiveCrudRepository<UserCategory,Long> {

    @Query("""
            select count(*) from user_category t1 join user_category t2
            on t1.cat_id=t2.cat_id
            where t1.user_id=:sid and t2.user_id=:adminid;
            """)
    Mono<Integer> checkUsersInSameCategory(long sid, long adminid);

    @Query("""
            select t2.name as category_name, t2.id as cat_id, t3.name as user_name, t3.id as user_id
            from user_category t1, category t2, user t3
            where t1.cat_id=t2.id and t1.user_id=t3.id and t2.coll_id=:collid and t3.role=:role
            """)
    Flux<AdminDO> findByCollId(long collid, String role);

    @Query("""
            select count(*) from user_category t1 where t1.cat_id=:cid and t1.user_id=:uid;
            """)
    Mono<Integer> checkInCategory(long uid, long cid);
}
