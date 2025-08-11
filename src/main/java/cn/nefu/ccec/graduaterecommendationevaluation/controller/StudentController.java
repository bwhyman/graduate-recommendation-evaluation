package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItem;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.StudentItemFile;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScore;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.Code;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.XException;
import cn.nefu.ccec.graduaterecommendationevaluation.service.FileService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.ItemService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.StudentItemService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.WeightedScoreService;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.TokenAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/student/")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    private final ItemService itemService;
    private final WeightedScoreService weightedScoreService;
    private final StudentItemService studentItemService;
    private final FileService fileService;

    // 获取一级指标
    @GetMapping("topitems")
    public Mono<ResultVO> getItems(@RequestAttribute(TokenAttribute.CATID) long catid) {
        return itemService.listTopItems(catid)
                .map(ResultVO::success);

    }

    // 获取全部子items
    @GetMapping("items/{parentitemid}")
    public Mono<ResultVO> getItems(@PathVariable long parentitemid,
                                   @RequestAttribute(TokenAttribute.CATID) long catid) {
        return itemService.listItems(catid, parentitemid)
                .map(ResultVO::success);

    }

    @GetMapping("weightedscores")
    public Mono<ResultVO> getWeightedScores(@RequestAttribute(TokenAttribute.UID) long uid) {
        return weightedScoreService.getWeightedScore(uid)
                .map(ResultVO::success)
                .switchIfEmpty(Mono.just(ResultVO.success(Map.of())));
    }

    @PostMapping("weightedscores")
    public Mono<ResultVO> postWeightedScore(@RequestBody WeightedScore weightedScore,
                                            @RequestAttribute(TokenAttribute.UID) long uid) {
        return weightedScoreService.getWeightedScore(uid)
                .flatMap(ws -> {
                    if (ws.getVerified() == WeightedScore.VERIFIED) {
                        return Mono.just(ResultVO.error(Code.ERROR, "成绩已认定，无法修改"));
                    }
                    return weightedScoreService.updateWeightedScore(uid, weightedScore.getScore(), weightedScore.getRanking(), WeightedScore.UNVERIFIED)
                            .thenReturn(ResultVO.success());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    weightedScore.setNew();
                    weightedScore.setId(uid);
                    weightedScore.setVerified(WeightedScore.UNVERIFIED);
                    return weightedScoreService.addWeightedScore(weightedScore)
                            .thenReturn(ResultVO.success());
                }));
    }


    @PostMapping("studentitems")
    public Mono<ResultVO> postStudentItems(
            @RequestBody StudentItem studentItem,
            @RequestAttribute(TokenAttribute.UID) long uid,
            @RequestAttribute(TokenAttribute.CATID) long catid) {
        return itemService.getItem(studentItem.getItemId(), catid)
                .flatMap(item -> {
                    var stuI = StudentItem.builder()
                            .rootItemId(studentItem.getRootItemId())
                            .itemId(studentItem.getItemId())
                            .name(studentItem.getName())
                            .status(StudentItem.Status.PENDING_REVIEW)
                            .userId(uid)
                            .comment(studentItem.getComment())
                            .build();
                    return studentItemService.addStudentItem(stuI)
                            .thenReturn(ResultVO.success());
                }).defaultIfEmpty(ResultVO.error(Code.ERROR, "指标项不存在"));
    }

    @PostMapping("studentitems/{stuitemid}/files")
    public Mono<ResultVO> postStudentFiles(
            @PathVariable long stuitemid,
            Mono<FilePart> uploadFile,
            @RequestAttribute(TokenAttribute.UID) long uid,
            @RequestAttribute(TokenAttribute.MAGORID) long majorid) {

        return studentItemService.getStudentItem(uid, stuitemid)
                .flatMap(sutFile -> {
                            if (sutFile.getStatus().equals(StudentItem.Status.CONFIRMED)) {
                                return Mono.error(XException.builder().codeN(Code.ERROR).message("指标项已认定，不可修改").build());
                            }
                            return uploadFile
                                    .flatMap(filePart -> {
                                        var fileName = filePart.filename();
                                        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                                            log.warn("恶意上传包含路径的文件。用户ID：{}", uid);
                                            return Mono.error(XException.builder().codeN(Code.ERROR).message("上传文件错误").build());
                                        }
                                        return fileService.createAndGetRelativePath(majorid, uid)
                                                .flatMap(relativePath -> fileService.saveFile(relativePath, filePart.filename(), filePart.content()));
                                    })
                                    .flatMap(path ->
                                            studentItemService.addStudentItemFile(StudentItemFile.builder()
                                                    .studentItemId(stuitemid)
                                                    .path(path.getParent().toString())
                                                    .filename(path.getFileName().toString())
                                                    .build()))
                                    .flatMap(sf -> studentItemService.updateStatus(uid, StudentItem.Status.PENDING_REVIEW))
                                    .then()
                                    .thenReturn(ResultVO.success());
                        }
                )
                .defaultIfEmpty(ResultVO.error(Code.ERROR, "指标项不存在"));
    }


    @GetMapping("studentitems/{rootitemid}")
    public Mono<ResultVO> getStudentItems(@PathVariable long rootitemid,
                                          @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.listStudentItems(uid, rootitemid)
                .map(ResultVO::success);
    }

    @GetMapping("studentitems/files/{fileid}")
    public Flux<DataBuffer> download(
            @PathVariable long fileid,
            @RequestAttribute(TokenAttribute.UID) long uid,
            ServerHttpResponse response) {
        return studentItemService.getFilePath(uid, fileid)
                .flatMapMany(filepath -> fileService.getSize(filepath)
                        .flatMapMany(size -> {
                            String name = URLEncoder.encode(filepath.getFileName().toString(), StandardCharsets.UTF_8);
                            HttpHeaders headers = response.getHeaders();
                            headers.set("filename", name);
                            headers.setContentLength(size);
                            return fileService.downloadFile(filepath);
                        }));
    }


    @DeleteMapping("studentitems/files/{fileid}")
    public Mono<ResultVO> deleteFiles(
            @PathVariable long fileid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.getFilePath(uid, fileid)
                .flatMap(fileService::removeFile)
                .flatMap(removed -> {
                    if (removed) {
                        return studentItemService.removeStudentItemFile(fileid)
                                .thenReturn(ResultVO.success());
                    }
                    return Mono.just(ResultVO.error(Code.ERROR, "文件移除失败"));
                });
    }

    @DeleteMapping("studentitems/{stuitemid}")
    public Mono<ResultVO> deleteStudentItems(
            @PathVariable long stuitemid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.getStudentItem(uid, stuitemid)
                .flatMap(studentItem -> studentItemService.removeStudentItem(stuitemid))
                .then(Mono.defer(() -> studentItemService.listStudentItemFiles(stuitemid)
                        .map(file -> Path.of(file.getPath(), file.getFilename()))
                        .flatMap(fileService::removeFile)
                        .flatMap(removed -> studentItemService.removeStudentItemFiles(stuitemid))
                        .then(Mono.just(ResultVO.success())))
                );
    }

    @PatchMapping("studentitems/{stuitemid}")
    public Mono<ResultVO> updateStudentItems(
            @PathVariable long stuitemid,
            @RequestBody StudentItem studentItem,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.updateStudentItem(uid, stuitemid, studentItem.getName(), studentItem.getComment(), StudentItem.Status.PENDING_REVIEW)
                .thenReturn(ResultVO.success());
    }

    @GetMapping("statuses")
    public Mono<ResultVO> getStatuses(@RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.getStudentItemsInfo(uid)
                .map(ResultVO::success)
                .defaultIfEmpty(ResultVO.success(Map.of()));
    }

    @GetMapping("logs/{stuitemid}")
    public Mono<ResultVO> getStudentItemLogs(
            @PathVariable long stuitemid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.listStudentItemLogs(uid, stuitemid)
                .map(ResultVO::success);
    }
}
