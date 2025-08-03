package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.College;
import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.CollegeRepository;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
