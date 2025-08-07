package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemLog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StudentItemLogRepository extends ReactiveCrudRepository<StudentItemLog, Long> {

    @Query("""
            select * from student_item_log t1 join student_item t2
            on t1.student_item_id=t2.id and t2.user_id=:uid
            where t1.student_item_id=:stuitemid
            """)
    Flux<StudentItemLog> find(long uid, long stuitemid);
}
