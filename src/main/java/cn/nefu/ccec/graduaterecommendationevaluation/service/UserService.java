package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.UserCategory;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.RegisterUserDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.dto.UserInfoDTO;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserCategoryRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCategoryRepository userCategoryRepository;

    public Mono<User> getUser(String account) {
        return userRepository.findByAccount(account);
    }

    public Mono<UserInfoDTO> getUserInfo(long id) {
        return userRepository.find(id);
    }

    @Transactional
    public Mono<Void> updatePassword(long uid, String password) {
        return userRepository.updatePassword(uid, passwordEncoder.encode(password));
    }

    @Transactional
    public Mono<Void> addCategoryAdmin(RegisterUserDTO registerUser) {
        var newUser = new User();
        BeanUtils.copyProperties(registerUser, newUser);
        newUser.setRole(User.CATEGORY_ADMIN);
        newUser.setPassword(passwordEncoder.encode(registerUser.getAccount()));
        return userRepository.save(newUser)
                .flatMap(u -> Flux.fromIterable(registerUser.getCatIds())
                        .flatMap(catid -> userCategoryRepository.save(UserCategory.builder()
                                .userId(u.getId())
                                .catId(catid)
                                .build())
                        ).collectList()
                ).then();
    }

    @Transactional
    public Mono<Void> addStudent(RegisterUserDTO registerUser) {
        var newUser = new User();
        BeanUtils.copyProperties(registerUser, newUser);
        newUser.setRole(User.STUDENT);
        newUser.setPassword(passwordEncoder.encode(registerUser.getAccount()));
        return userRepository.save(newUser)
                .flatMap(u -> userCategoryRepository.save(UserCategory.builder()
                        .userId(u.getId())
                        .catId(registerUser.getCatId())
                        .build()))
                .then();
    }

}
