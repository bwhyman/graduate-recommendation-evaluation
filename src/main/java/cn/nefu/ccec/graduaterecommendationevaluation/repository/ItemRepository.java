package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
    
    @Query("""
            select * from item t1 where t1.cat_id=:catid and t1.parent_id is null;
            """)
    Flux<Item> findTopByCatId(long catid);

    Flux<Item> findByCatId(Long catid);

    @Query("""
            with recursive t0 as (
                select * from item t1 where t1.cat_id=:catid and t1.parent_id=:parentid
                union
                select t2.* from item t2 join t0 where t2.parent_id=t0.id
            )
            select * from t0;
            """)
    Flux<Item> findByCatIdAndParentId(long catid, long parentid);

    Mono<Item> findByIdAndCatId(long id, long catid);

}
