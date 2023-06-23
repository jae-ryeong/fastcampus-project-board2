package com.fastcampus.projectboard2.config;

import com.fastcampus.projectboard2.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        // (인증 기능을 붙히기 전) Article에서 createdBy는 Date와 다르게 누구인지 자동으로 알아내기 어려우므로 "wofud"이 값을 넣어주기 위해 설정
        // return () -> Optional.of("wofud");  // TODO: 스프링 시큐리티로 인증 기능을 붙일때 수정

        return () ->  Optional.ofNullable(SecurityContextHolder.getContext()) // SecurityContextHolder: 인증정보를 모두 가지고 있는 class
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)    // Authenticated가 되어있는지 검사한 후, 로그인 한 상태인지
                .map(Authentication::getPrincipal)  // Principal을 꺼내준다.
                .map(BoardPrincipal.class::cast)    // 타입 캐스팅할때 사용
                .map(BoardPrincipal::getUsername);
    }
}
