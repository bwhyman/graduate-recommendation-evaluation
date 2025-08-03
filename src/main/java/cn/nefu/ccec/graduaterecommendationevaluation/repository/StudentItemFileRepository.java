package cn.nefu.ccec.graduaterecommendationevaluation.repository;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemFile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface StudentItemFileRepository extends ReactiveCrudRepository<StudentItemFile, Long> {

    Mono<Void> deleteByStudentItemId(long studentItemId);

    @Query("""
            select * from student_item_file t1
            where t1.student_item_id=:stuitemid
            """)
    Flux<StudentItemFile> findByStudentItemIds(long stuitemid);

    @Query("""
            select concat_ws('/', t1.path, t1.filename)
            from student_item_file t1, student_item t2
            where t1.student_item_id=t2.id and t2.user_id=:uid and t1.id=:fileid
            """)
    Mono<String> findPath(long uid, long fileid);
}
