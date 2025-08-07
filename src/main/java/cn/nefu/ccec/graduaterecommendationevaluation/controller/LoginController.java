package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.component.JWTComponent;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.Code;
import cn.nefu.ccec.graduaterecommendationevaluation.service.CategoryService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.UserService;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.TokenAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/open/")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final JWTComponent jwtComponent;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("login")
    public Mono<ResultVO> login(@RequestBody User userLogin, ServerHttpResponse response) {
        return userService.getUser(userLogin.getAccount())
                .filter(u -> passwordEncoder.matches(userLogin.getPassword(), u.getPassword()))
                .flatMap(u -> {
                    var role = u.getRole();
                    Map<String, Object> map = new HashMap<>();
                    map.put(TokenAttribute.UID, u.getId());
                    map.put(TokenAttribute.ROLE, role);
                    if (u.getCollId() != null) {
                        map.put(TokenAttribute.COLLID, u.getCollId());
                    }
                    if (u.getMajorId() != null) {
                        map.put(TokenAttribute.MAGORID, u.getMajorId());
                    }
                    if(u.getCatId() != null) {
                        map.put(TokenAttribute.CATID, u.getCatId());
                    }
                    response.getHeaders().add("role", u.getRole());
                    response.getHeaders().add("token", jwtComponent.encode(map));
                    return userService.getUserInfo(u.getId());
                }).map(ResultVO::success)
                .defaultIfEmpty(ResultVO.error(Code.LOGIN_ERROR));
    }
}
