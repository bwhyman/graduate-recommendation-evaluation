package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.WeightedScore;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.ComfirmWeightedScoreReq;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.StudentItemReq;
import cn.nefu.ccec.graduaterecommendationevaluation.service.*;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.TokenAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/college/")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;
    private final ItemService itemService;
    private final StudentItemService studentItemService;
    private final WeightedScoreService weightedScoreService;
    private final FileService fileService;

    @GetMapping("categories")
    public Mono<ResultVO> getCategories(@RequestAttribute(TokenAttribute.UID) long uid) {
        return categoryService.listCategories(uid)
                .map(ResultVO::success);
    }

    @GetMapping("categories/{catid}/majors")
    public Mono<ResultVO> getMajors(@PathVariable long catid,
                                    @RequestAttribute(TokenAttribute.UID) long uid) {
        return categoryService.listMajors(uid, catid)
                .map(ResultVO::success);
    }

    // 加载指定专业下所有学生提交状态
    @GetMapping("majors/{majorid}/students/statuses")
    public Mono<ResultVO> getUsers(@PathVariable long majorid,
                                   @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.listStudentsInfos(majorid)
                .map(ResultVO::success);
    }

    // 加载指定类别下所有指标项
    @GetMapping("categories/{catid}/items")
    public Mono<ResultVO> getItems(@PathVariable long catid) {
        return itemService.listItems(catid)
                .map(ResultVO::success);
    }

    /*@GetMapping("categories/{catid}/items/{itemid}")
    public Mono<ResultVO> getItems(@PathVariable long catid,
                                   @PathVariable long itemid) {
        return itemService.listItems(catid, itemid)
                .map(ResultVO::success);
    }*/

    @GetMapping("students/{sid}/weightedscore")
    public Mono<ResultVO> getStudentWeightScore(
            @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.checkUserCategoryAuth(sid, uid)
                .flatMap(r -> weightedScoreService.getWeightedScore(sid))
                .map(ResultVO::success)
                .defaultIfEmpty(ResultVO.success());
    }

    @PostMapping("students/{sid}/weightedscore")
    public Mono<ResultVO> postWeightedScore(
            @RequestBody ComfirmWeightedScoreReq req, @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        req.getLog().setUserId(uid);
        return studentItemService.checkUserCategoryAuth(sid, uid)
                .flatMap(r -> weightedScoreService.updateWeightedScore(sid, req.getWeightedScore().getScore(), req.getWeightedScore().getRanking(), WeightedScore.VERIFIED, req.getLog()))
                .then(Mono.defer(() -> weightedScoreService.getWeightedScore(sid)))
                .map(ResultVO::success);
    }

    @GetMapping("students/{sid}/studentitems")
    public Mono<ResultVO> getStudentItems(
            @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        return studentItemService.checkUserCategoryAuth(sid, uid)
                .flatMap(r -> studentItemService.getStudentItemDTOs(sid))
                .map(ResultVO::success);
    }

    @PostMapping("students/{sid}/studentitems")
    public Mono<ResultVO> postStudentItems(
            @RequestBody StudentItemReq req,
            @PathVariable long sid,
            @RequestAttribute(TokenAttribute.UID) long uid) {
        req.getLog().setUserId(uid);
        return studentItemService.checkUserCategoryAuth(sid, uid)
                .flatMap(r -> studentItemService.updteStudentItem(req.getStudentItem(), req.getLog()))
                .then(Mono.defer(() -> studentItemService.getStudentItemDTOs(sid)))
                .map(ResultVO::success);
    }

    @GetMapping("studentitems/files/{fileid}")
    public Flux<DataBuffer> download(
            @PathVariable long fileid,
            ServerHttpResponse response) {
        return studentItemService.getFilePath(fileid)
                .flatMapMany(filepath -> fileService.getSize(filepath)
                        .flatMapMany(size -> {
                            String name = URLEncoder.encode(filepath.getFileName().toString(), StandardCharsets.UTF_8);
                            HttpHeaders headers = response.getHeaders();
                            headers.set("filename", name);
                            headers.setContentLength(size);
                            return fileService.downloadFile(filepath);
                        }));
    }
}
