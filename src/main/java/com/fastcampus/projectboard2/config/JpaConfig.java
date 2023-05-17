package com.fastcampus.projectboard2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        // (인증 기능을 붙히기 전) Article에서 createdBy는 Date와 다르게 누구인지 자동으로 알아내기 어려우므로 "wofud"이 값을 넣어주기 위해 설정

        return () -> Optional.of("wofud");  // TODO: 스프링 시큐리티로 인증 기능을 붙일때 수정
    }
}
