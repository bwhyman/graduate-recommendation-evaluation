package cn.nefu.ccec.graduaterecommendationevaluation.controller;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Category;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.Item;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.Major;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.RegisterUserDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.service.*;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.ResultVO;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.TokenAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/college/")
@RequiredArgsConstructor
@Slf4j
public class CollegeAdminController {
    private final CategoryService categoryService;
    private final CollegeService collegeService;
    private final UserService userService;
    private final ItemService itemService;
    private final AdminService adminService;

    @GetMapping("categories/users")
    public Mono<ResultVO> getUsers(@RequestAttribute(TokenAttribute.COLLID) long collid) {
        return adminService.listAdmins(collid, User.CATEGORY_ADMIN)
                .map(ResultVO::success);
    }

    @PostMapping("users")
    public Mono<ResultVO> postCategoryAdmin(@RequestBody RegisterUserDTO registerUser,
                                            @RequestAttribute(TokenAttribute.COLLID) long collid) {
        registerUser.setCollId(collid);
        return userService.addCategoryAdmin(registerUser)
                .thenReturn(ResultVO.success());
    }

    // 获取全部分类及专业
    @GetMapping("categories/majors")
    public Mono<ResultVO> getCategoriesAll(@RequestAttribute(TokenAttribute.COLLID) long collid) {
        return categoryService.listCategoryDTOs(collid)
                .map(ResultVO::success);
    }

    // 添加类别
    @PostMapping("categories")
    public Mono<ResultVO> addCategory(@RequestBody Category category,
                                      @RequestAttribute(TokenAttribute.COLLID) long collid) {
        return categoryService.addCategory(category)
                .then(categoryService.listCategoryDTOs(collid))
                .map(ResultVO::success);
    }

    // 添加专业
    @PostMapping("majors")
    public Mono<ResultVO> addMajor(@RequestBody Major major,
                                   @RequestAttribute(TokenAttribute.COLLID) long collid) {
        return categoryService.addMajor(major)
                .then(categoryService.listCategoryDTOs(collid))
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

}
