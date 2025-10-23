package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.UserInfoDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.Code;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.XException;
import cn.nefu.ccec.graduaterecommendationevaluation.service.UserService;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.TokenAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class CommonController {
    private final UserService userService;

    @GetMapping("info")
    public Mono<ResultVO> getInfo(@RequestAttribute(TokenAttribute.UID) long uid,
                                  @RequestAttribute(TokenAttribute.ROLE) String role) {
        return (switch (role) {
            case User.COLLAGE_ADMIN -> userService.getCollegeAdminUserInfo(uid);
            case User.CATEGORY_ADMIN ->  userService.getCategoryAdminUserInfo(uid);
            case User.STUDENT ->  userService.getStudentUserInfo(uid);
            case User.ADMIN -> Mono.just(UserInfoDTO.builder().name("admin").build());
            default -> Mono.error(XException.builder().code(Code.FORBIDDEN).build());
        }).map(ResultVO::success);
    }

    @PostMapping("passwords")
    public  Mono<ResultVO> postPassword(@RequestBody User user, @RequestAttribute(TokenAttribute.UID) long uid) {
        return userService.updatePassword(uid, user.getPassword())
                .thenReturn(ResultVO.success());
    }
}
