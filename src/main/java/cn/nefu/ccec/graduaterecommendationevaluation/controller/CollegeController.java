package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Category;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.Item;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.Major;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.RegisterUserDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.service.CategoryService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.CollegeService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.ItemService;
import cn.nefu.ccec.graduaterecommendationevaluation.service.UserService;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.TokenAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/college/")
@RequiredArgsConstructor
@Slf4j
public class CollegeController {
    private final CategoryService categoryService;
    private final CollegeService collegeService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping("users")
    public Mono<ResultVO> postCategoryAdmin(@RequestBody RegisterUserDTO registerUser,
                                            @RequestAttribute(TokenAttribute.COLLID) long collid) {
        registerUser.setCollId(collid);
        return userService.addCategoryAdmin(registerUser)
                .thenReturn(ResultVO.success());
    }

    // 获取全部分类及专业
    @GetMapping("categories")
    public Mono<ResultVO> getCategories(@RequestAttribute(TokenAttribute.COLLID) long collid,
                                        @RequestAttribute(TokenAttribute.UID) long uid,
                                        @RequestAttribute(TokenAttribute.ROLE) String role) {
        var result = switch (role) {
            case User.COLLAGE_ADMIN -> categoryService.listCategories(collid);
            case User.CATEGORY_ADMIN -> categoryService.listCategoriesByuid(uid);
            default -> Mono.empty();
        };
        return result.map(ResultVO::success);
    }

    // 添加类别
    @PostMapping("categories")
    public Mono<ResultVO> addCategory(@RequestBody Category category,
                                      @RequestAttribute(TokenAttribute.COLLID) long collid) {
        return categoryService.addCategory(category)
                .then(categoryService.listCategories(collid))
                .map(ResultVO::success);
    }

    // 添加专业
    @PostMapping("majors")
    public Mono<ResultVO> addMajor(@RequestBody Major major,
                                   @RequestAttribute(TokenAttribute.COLLID) long collid) {
        return categoryService.addMajor(major)
                .then(categoryService.listCategories(collid))
                .map(ResultVO::success);
    }

    @PostMapping("items")
    public Mono<ResultVO> addItem(@RequestBody Item item,
                                  @RequestAttribute(TokenAttribute.COLLID) long collid) {
        return itemService.addItem(item)
                .then(Mono.defer(() -> itemService.listItems(item.getCatId())))
                .map(ResultVO::success);
    }

    // 重置密码
    @PutMapping("passwords/{account}")
    public Mono<ResultVO> putPassword(@PathVariable String account,
                                      @RequestAttribute(TokenAttribute.COLLID) long collid) {
        return collegeService.updatePassword(collid, account)
                .thenReturn(ResultVO.success());
    }

    //
    @GetMapping("categories/{catid}/items")
    public Mono<ResultVO> getItems(@PathVariable long catid) {
        return itemService.listItems(catid)
                .map(ResultVO::success);
    }

    @GetMapping("categories/{catid}/items/{itemid}")
    public Mono<ResultVO> getItems(@PathVariable long catid,
                                   @PathVariable long itemid) {
        return itemService.listItems(catid, itemid)
                .map(ResultVO::success);
    }

}
