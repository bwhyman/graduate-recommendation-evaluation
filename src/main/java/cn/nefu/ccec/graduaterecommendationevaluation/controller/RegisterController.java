package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.RegisterUserDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.Code;
import cn.nefu.ccec.graduaterecommendationevaluation.service.CategoryService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.UserService;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/open/")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;
    private final CategoryService categoryService;

    @PostMapping("register")
    public Mono<ResultVO> register(@RequestBody RegisterUserDTO user) {
        return userService.getUser(user.getAccount())
                .flatMap(existingUser -> Mono.just(ResultVO.error(Code.ERROR, "学号已经存在，请联系导员或推免负责人！"))
                )
                .switchIfEmpty(userService.addStudent(user)
                        .thenReturn(ResultVO.success()));
    }

    @GetMapping("colleges")
    public Mono<ResultVO> getColleges() {
        return categoryService.listCollegesAndMajors()
                .map(ResultVO::success);
    }
}
