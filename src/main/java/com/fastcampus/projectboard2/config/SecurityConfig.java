package com.fastcampus.projectboard2.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    //시큐리티 설정에 모든 요청에 인증이 열리도록 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {    //무조건 /login 페이지가 뜨지 않게 해준다.
        return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin().and()
                .build();
    }
}
