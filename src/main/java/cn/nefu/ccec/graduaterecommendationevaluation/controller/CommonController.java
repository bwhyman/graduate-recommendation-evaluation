package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
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
    public Mono<ResultVO> getInfo(@RequestAttribute(TokenAttribute.UID) long uid) {
        return userService.getUserInfo(uid)
                .map(ResultVO::success);
    }

    @PostMapping("passwords")
    public  Mono<ResultVO> postPassword(@RequestBody User user, @RequestAttribute(TokenAttribute.UID) long uid) {
        return userService.updatePassword(uid, user.getPassword())
                .thenReturn(ResultVO.success());
    }
}
