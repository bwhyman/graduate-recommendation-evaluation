package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.College;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.service.AdminService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.UserService;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("colleges")
    public Mono<ResultVO> postCollege(@RequestBody College college) {
        return adminService.addCollege(college)
                .thenReturn(ResultVO.success());
    }

    @GetMapping("colleges")
    public Mono<ResultVO> getColleges() {
        return adminService.listColleges()
                .map(ResultVO::success);
    }

    @PostMapping("users")
    public Mono<ResultVO> postUser(@RequestBody User user) {
        return adminService.addCollegeAdmin(user)
                .thenReturn(ResultVO.success());
    }
}
