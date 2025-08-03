package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemFile;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemResp;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemsDO;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.Code;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.XException;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.MajorRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.StudentItemFileRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.StudentItemRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentItemService {
    private final StudentItemRepository studentItemRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final StudentItemFileRepository studentItemFileRepository;

    @Transactional
    public Mono<StudentItem> addStudentItem(StudentItem studentItem) {
        return studentItemRepository.save(studentItem);
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

    @Transactional
    public Mono<StudentItemFile> addStudentItemFile(StudentItemFile sf) {
        return studentItemFileRepository.save(sf);
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

    public Mono<List<StudentItemResp>> listStudentItemDTOs(long uid) {
        return studentItemRepository.findByUserId(uid)
                .collectList()
                .map(this::convertToDTO);
    }

    private List<StudentItemResp> convertToDTO(List<StudentItemsDO> dos) {
        return dos.stream()
                .collect(Collectors.groupingBy(StudentItemsDO::getItemId))
                .values()
                .stream()
                .map(itemGroup -> {
                    var first = itemGroup.getFirst();
                    var studentItemResp = new StudentItemResp();
                    BeanUtils.copyProperties(first, studentItemResp);
                    studentItemResp.setItemName(first.getItemName());
                    studentItemResp.setItemComment(first.getItemComment());

                    var files = itemGroup.stream()
                            .filter(record -> record.getPath() != null)
                            .map(record -> StudentItemFile.builder()
                                    .id(record.getStudentItemFileId())
                                    .studentItemId(first.getId())    // 来自外层分组 key
                                    .path(record.getPath())          // 来自 DO
                                    .filename(record.getFilename())  // 来自 DO
                                    .build())
                            .toList();

                    studentItemResp.setFiles(files);
                    return studentItemResp;
                })
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

    @Transactional
    public Mono<Void> removeStudentItemFile(long fileid) {
        return studentItemFileRepository.deleteById(fileid);
    }

    @Transactional
    public Mono<Void> removeStudentItemFiles(long studentitemid) {
        return studentItemFileRepository.deleteByStudentItemId(studentitemid);
    }

    @Transactional
    public Mono<Void> removeStudentItem(long id) {
        return studentItemRepository.deleteById(id);
    }

    @Transactional
    public Mono<Integer> updateStudentItem(long uid, long stuid, String name, String comment) {
        return studentItemRepository.updateByUserId(uid, stuid, name, comment)
                .switchIfEmpty(Mono.error(XException.builder().
                        codeN(Code.ERROR)
                        .message("更新条目不存在")
                        .build())
                );
    }
}
