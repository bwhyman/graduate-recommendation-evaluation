package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemsDO;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemsStatusDO;
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

    @Modifying
    @Query("""
            update student_item t1 set t1.status=:status where t1.user_id=:uid
            """)
    Mono<Integer> updateStatus(long uid, String status);

    @Query("""
            select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment,
                   t1.status,
                   t2.filename, t2.id as student_item_file_id,
                   t3.name as item_name, t3.comment as item_comment, t3.max_points, t3.max_items
            from student_item t1 left join student_item_file t2
            on t1.id = t2.student_item_id
            join item t3 on t3.id=t1.item_id
            where t1.root_item_id=:rootitemid and t1.user_id=:uid order by t1.item_id;
            """)
    Flux<StudentItemsDO> findByRootItemId(long uid, long rootitemid);

    @Query("""
            select t1.id, t1.user_id, t1.root_item_id, t1.item_id, t1.point, t1.name as name, t1.comment as comment, t1.status,
            t2.filename, t2.id as student_item_file_id,
            t3.id ,t3.name as item_name, t3.comment as item_comment, t3.max_points, t3.max_items, t3.parent_id as item_parent_id
            from student_item t1 left join student_item_file t2
            on t1.id = t2.student_item_id
            join item t3 on t3.id=t1.item_id
            where t1.user_id=:uid order by t3.id;
            """)
    Flux<StudentItemsDO> findByUserId(long uid);

    @Modifying
    @Query("""
            update student_item t1 set t1.name=:name, t1.comment=:comment, t1.status=:status
            where t1.user_id=:uid and t1.id=:id
            """)
    Mono<Integer> updateByUserId(long uid, long id, String name, String comment, String status);


    @Query("""
            select
                u.id as user_id,
                u.name as user_name,
                u.mobile as mobile,
                ws.score as score,
                ws.ranking as ranking,
                ws.verified as verified,
                coalesce(sum(si.point), 0) as total_point,
                count(u.id) as total_count,
                coalesce(sum(si.status = :sub), 0) as pending_review_count,
                coalesce(sum(si.status = :rej), 0) as rejected_count,
                coalesce(sum(si.status = :pend), 0) as pending_modification_count,
                coalesce(sum(si.status = :conf), 0) as confirmed_count
            from
                `user` u left join weighted_score ws on ws.id=u.id
                    left join student_item si on u.id = si.user_id
            where
                u.major_id =:majorid
            group by
                u.id
            order by
                u.id;
            """)
    Flux<StudentItemsStatusDO> findStudentItemsInfos(long majorid, String sub, String rej, String pend, String conf);

    @Query("""
            select
                u.id as user_id,
                u.name as user_name,
                ws.score as score,
                ws.ranking as ranking,
                ws.verified as verified,
                coalesce(sum(si.point), 0) as total_point,
                count(u.id) as total_count,
                coalesce(sum(si.status = :sub), 0) as pending_review_count,
                coalesce(sum(si.status = :rej), 0) as rejected_count,
                coalesce(sum(si.status = :pend), 0) as pending_modification_count,
                coalesce(sum(si.status = :conf), 0) as confirmed_count
            from
                `user` u join weighted_score ws on ws.id=u.id
                    left join student_item si on u.id = si.user_id
            where
                u.id =:uid
            group by
                u.id
            order by
                u.id;
            """)
    Mono<StudentItemsStatusDO> findStudentItemsInfo(long uid, String sub, String rej, String pend, String conf);

    @Modifying
    @Query("""
            update student_item t1 set t1.point=:point, t1.status=:status where t1.id=:id
            """)
    Mono<Integer> update(long id, Float point, String status);
}
