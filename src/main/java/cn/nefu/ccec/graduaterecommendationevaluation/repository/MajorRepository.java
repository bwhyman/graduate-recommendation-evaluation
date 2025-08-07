package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Major;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MajorRepository extends ReactiveCrudRepository<Major,Long> {

    @Query("""
            select t1.* from major t1, user_category t2
            where t1.cat_id=t2.cat_id and t2.cat_id=:catid and t2.user_id=:uid;
            """)
    Flux<Major> findByCatId(long uid, long catid);

    @Query("""
            select t1.* from major t1 where t1.cat_id=:catid;
            """)
    Flux<Major> findByCatId(long catid);

    @Query("""
            select t3.* from category t2, major t3
            where t2.id=t3.cat_id and t2.coll_id=:collid
            """)
    Flux<Major> findByCollId(long collid);

    @Query("""
            select concat_ws('/',t3.name, t2.name, t1.name) from major t1,category t2, college t3
            where t1.cat_id=t2.id and t2.coll_id=t3.id and t1.id=:mid;
            """)
    Mono<String> findFileDirectoryName(long mid);
}
