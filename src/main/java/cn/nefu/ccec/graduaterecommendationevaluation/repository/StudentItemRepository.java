package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemsDO;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentItemRepository extends ReactiveCrudRepository<StudentItem, Long> {

    @Query("""
            select * from student_item t1 where t1.id=:id and t1.user_id=:uid;
            """)
    Mono<StudentItem> findByUserId(long uid, long id);

    @Query("""
            select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment,
                   t1.status,
                   t2.path, t2.filename, t2.id as student_item_file_id,
                   t3.name as item_name, t3.comment as item_comment, t3.max_points, t3.max_items
            from student_item t1 left join student_item_file t2
            on t1.id = t2.student_item_id
            join item t3 on t3.id=t1.item_id
            where t1.root_item_id=:rootitemid and t1.user_id=:uid order by t1.item_id;
            """)
    Flux<StudentItemsDO> findByRootItemId(long uid, long rootitemid);

    @Query("""
            select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment,
                   t1.status,
                   t2.path, t2.filename, t2.id as student_item_file_id,
                   t3.id ,t3.name as item_name, t3.comment as item_comment, t3.max_points, t3.max_items, t3.parent_id as item_parent_id
            from student_item t1 left join student_item_file t2
            on t1.id = t2.student_item_id
            join item t3 on t3.id=t1.item_id
            where t1.user_id=:uid order by t3.id;
            """)
    Flux<StudentItemsDO> findByUserId(long uid);

    @Modifying
    @Query("""
            update student_item t1 set t1.name=:name, t1.comment=:comment
            where t1.user_id=:uid and t1.id=:id
            """)
    Mono<Integer> updateByUserId(long uid, long id, String name, String comment);
}
