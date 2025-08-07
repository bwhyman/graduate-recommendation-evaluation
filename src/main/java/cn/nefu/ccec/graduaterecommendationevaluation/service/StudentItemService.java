package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemFile;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemLog;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemResp;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemsDO;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemsStatusDO;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.Code;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.XException;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem.Status.*;

@Service
@RequiredArgsConstructor
public class StudentItemService {
    private final StudentItemRepository studentItemRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final StudentItemFileRepository studentItemFileRepository;
    private final TransactionalOperator transactionalOperator;
    private final UserCategoryRepository userCategoryRepository;
    private final StudentItemLogRepository studentItemLogRepository;

    public Mono<StudentItem> addStudentItem(StudentItem studentItem) {
        return studentItemRepository.save(studentItem)
                .as(transactionalOperator::transactional);
    }

    // 返回，`学院/类别/专业`
    @Cacheable(value = "majordirs", key = "#majorid")
    public Mono<String> getMajorDirectoryName(long majorid) {
        return majorRepository.findFileDirectoryName(majorid).cache();
    }

    // 返回，`姓名-学号`
    public Mono<String> getUserFileDirectoryName(long uid) {
        return userRepository.findFileDirectoryName(uid);
    }

    public Mono<StudentItemFile> addStudentItemFile(StudentItemFile sf) {
        return studentItemFileRepository.save(sf)
                .as(transactionalOperator::transactional);
    }

    public Mono<StudentItem> getStudentItem(long uid, long stuitemid) {
        return studentItemRepository.findByUserId(uid, stuitemid);
    }

    public Mono<List<StudentItemResp>> listStudentItems(long uid, long rootitemid) {
        return studentItemRepository.findByRootItemId(uid, rootitemid)
                .collectList()
                .map(this::convertToDTO);
    }

    public Flux<StudentItemFile> listStudentItemFiles(long stuitemid) {
        return studentItemFileRepository.findByStudentItemIds(stuitemid);
    }

    public Mono<List<StudentItemResp>> getStudentItemDTOs(long uid) {
        return studentItemRepository.findByUserId(uid)
                .collectList()
                .map(this::convertToDTO);
    }

    private List<StudentItemResp> convertToDTO(List<StudentItemsDO> dos) {
        return dos.stream()
                .collect(Collectors.groupingBy(StudentItemsDO::getId))
                .values()
                .stream()
                .map(itemGroup -> {
                    var first = itemGroup.getFirst();
                    var studentItemResp = new StudentItemResp();
                    BeanUtils.copyProperties(first, studentItemResp);
                    studentItemResp.setItemName(first.getItemName());
                    studentItemResp.setItemComment(first.getItemComment());

                    var files = itemGroup.stream()
                            .filter(record -> record.getFilename() != null)
                            .map(record -> StudentItemFile.builder()
                                    .id(record.getStudentItemFileId())
                                    .studentItemId(first.getId())    // 来自外层分组 key
                                    .filename(record.getFilename())  // 来自 DO
                                    .build())
                            .toList();

                    studentItemResp.setFiles(files);
                    return studentItemResp;
                })
                .sorted(Comparator.comparing(StudentItemResp::getItemId))
                .toList();
    }

    public Mono<Path> getFilePath(long uid, long fileid) {
        return studentItemFileRepository.findPath(uid, fileid)
                .map(Path::of)
                .switchIfEmpty(Mono.error(XException.builder()
                        .codeN(Code.ERROR)
                        .message("文件不存在")
                        .build())
                );
    }

    public Mono<Path> getFilePath(long fileid) {
        return studentItemFileRepository.findById(fileid)
                .map(stuFile -> Path.of(stuFile.getPath(), stuFile.getFilename()))
                .switchIfEmpty(Mono.error(XException.builder()
                        .codeN(Code.ERROR)
                        .message("文件不存在")
                        .build())
                );
    }

    public Mono<Void> removeStudentItemFile(long fileid) {
        return studentItemFileRepository.deleteById(fileid)
                .as(transactionalOperator::transactional);
    }

    public Mono<Void> removeStudentItemFiles(long studentitemid) {
        return studentItemFileRepository.deleteByStudentItemId(studentitemid)
                .as(transactionalOperator::transactional);
    }

    public Mono<Void> removeStudentItem(long id) {
        return studentItemRepository.deleteById(id)
                .as(transactionalOperator::transactional);
    }

    public Mono<Integer> updateStudentItem(long uid, long stuid, String name, String comment, String status) {
        return studentItemRepository.updateByUserId(uid, stuid, name, comment, status)
                .switchIfEmpty(Mono.error(XException.builder().
                        codeN(Code.ERROR)
                        .message("更新条目不存在")
                        .build())
                ).as(transactionalOperator::transactional);
    }

    public Mono<List<StudentItemsStatusDO>> listStudentsInfos(long majorid) {
        return studentItemRepository.findStudentItemsInfos(majorid, PENDING_REVIEW, REJECTED, PENDING_MODIFICATION, CONFIRMED)
                .collectList();
    }

    public Mono<StudentItemsStatusDO> getStudentItemsInfo(long uid) {
        return studentItemRepository.findStudentItemsInfo(uid, PENDING_REVIEW, REJECTED, PENDING_MODIFICATION, CONFIRMED);
    }

    public Mono<Integer> updateStatus(long uid, String status) {
        return studentItemRepository.updateStatus(uid, status)
                .as(transactionalOperator::transactional);
    }

    public Mono<Boolean> checkUserCategoryAuth(long sid, long adminid) {
        return userCategoryRepository.checkUserCategory(sid, adminid)
                .hasElement();
    }

    public Mono<Void> updteStudentItem(StudentItem stuItem, StudentItemLog log) {
        return studentItemRepository.update(stuItem.getId(), stuItem.getPoint(), stuItem.getStatus())
                .flatMap(r -> studentItemLogRepository.save(log))
                .as(transactionalOperator::transactional)
                .then();
    }

    public Mono<List<StudentItemLog>> listStudentItemLogs(long uid,long stuitemid) {
        return studentItemLogRepository.find(uid,stuitemid)
                .collectList();
    }
}
