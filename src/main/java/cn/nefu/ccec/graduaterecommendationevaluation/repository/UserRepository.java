package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.UserInfoDTO;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByAccount(String account);

    @Query("""
            select t1.id, t1.name, t2.name as coll_name,
            json_arrayagg(json_insert(t4.weighting, '$.id', cast(t4.id as char), '$.name', t4.name)) as categories,
            t5.name as major_name
            from user t1
                     left join college t2 on t1.coll_id = t2.id
                     left join user_category t3 on t1.id = t3.user_id
                     left join category t4 on t4.id = t3.cat_id
                     left join major t5 on t1.major_id = t5.id
            where t1.id=:id group by t1.id;
            """)
    Mono<UserInfoDTO> find(long id);

    @Modifying
    @Query("""
            update user u set u.password=:password where u.id=:uid;
            """)
    Mono<Void> updatePassword(long uid, String password);

    @Modifying
    @Query("""
            update user u set u.password=:password where u.coll_id=:collid and u.account=:account;
            """)
    Mono<Integer> updatePassword(long collid, String account, String password);

    @Query("""
            select concat_ws('-', u.name, u.account) from user u where u.id=:uid;
            """)
    Mono<String> findFileDirectoryName(long uid);


    Flux<User> findByMajorId(long majorid);
}
