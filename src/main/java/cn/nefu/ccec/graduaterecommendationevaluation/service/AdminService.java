package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.Category;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.College;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.AdminDO;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.AdminResp;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.CollegeRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserCategoryRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCategoryRepository userCategoryRepository;

    public Mono<Void> addCollege(College college) {
        return collegeRepository.save(college).then();
    }

    public Mono<List<College>> listColleges() {
        return collegeRepository.findAll().collectList();
    }

    public Mono<Void> addCollegeAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getAccount()));
        user.setRole(User.COLLAGE_ADMIN);
        return userRepository.save(user).then();
    }

    public Mono<List<AdminResp>> listAdmins(long collid, String role) {
        return userCategoryRepository.findByCollId(collid, role)
                .collectList()
                .map(admins -> {
                    return admins.stream()
                            .collect(Collectors.groupingBy(AdminDO::getCatId))
                            .values()
                            .stream()
                            .map(adminDOS -> {
                                AdminDO first = adminDOS.getFirst();
                                Category cat = Category.builder()
                                        .id(first.getCatId())
                                        .name(first.getCategoryName())
                                        .build();
                                List<User> list = adminDOS.stream()
                                        .map(adminDO -> User.builder()
                                                .id(adminDO.getUserId())
                                                .name(adminDO.getUserName())
                                                .build())
                                        .toList();
                                return AdminResp.builder()
                                        .users(list)
                                        .category(cat)
                                        .build();
                            }).toList();
                });
    }
}
