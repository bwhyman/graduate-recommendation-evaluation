package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CollegeService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionalOperator transactionalOperator;

    public Mono<Void> updatePassword(long collid, String account) {
        return userRepository.updatePassword(collid, account, passwordEncoder.encode(account))
                .as(transactionalOperator::transactional);
    }
}
