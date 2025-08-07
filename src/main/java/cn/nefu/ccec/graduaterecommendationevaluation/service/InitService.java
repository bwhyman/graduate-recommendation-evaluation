package cn.nefu.ccec.graduaterecommendationevaluation.service;

import cn.nefu.ccec.graduaterecommendationevaluation.dox.User;
import cn.nefu.ccec.graduaterecommendationevaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final TransactionalOperator transactionalOperator;

    @EventListener(classes = ApplicationReadyEvent.class)
    public Mono<Void> init() {
        var account = "admin";
        return userRepository.count()
                .flatMap(r -> {
                    if (r == 0) {
                        var user = User.builder()
                                .name(account)
                                .account(account)
                                .password(encoder.encode(account))
                                .role(User.ADMIN)
                                .build();
                        return userRepository.save(user).then();
                    }
                    return Mono.empty();
                })
                .as(transactionalOperator::transactional);
    }
}
