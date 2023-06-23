package com.fastcampus.projectboard2.config;


import com.fastcampus.projectboard2.dto.UserAccountDto;
import com.fastcampus.projectboard2.dto.security.BoardPrincipal;
import com.fastcampus.projectboard2.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    //시큐리티 설정에 모든 요청에 인증이 열리도록 설정
/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {    //무조건 /login 페이지가 뜨지 않게 해준다.
        return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin().and()
                .build();
    }
*/

    // spring security에 대해 자세히 공부해보기
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {    //무조건 /login 페이지가 뜨지 않게 해준다.
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll().
                requestMatchers
                (HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()   // 위에를 제외한 나머지 request는 인증이 되어야만 한다.
                )
                .formLogin().and()
                .logout()
                    .logoutSuccessUrl("/")
                    .and()
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
                // 이 부분에 등록한 내용들은 spring security 검사에서 제외하겠다
                // static resource, css, js 등
                // PathRequest.toStaticResources().atCommonLocations()에 기본적인 모든게 다 정의되어있다. 들어가서 찾아보자
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        // 실제 인증데이터 로직을 가져오는 서비스를 구현한다.
        // Bean스캐닝을 통해서 Repository에 불러온다.
        return username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)    // principal은 인증과 관련된 정보
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));  // 인증된 사용자를 찾지 못했을때를 위해 (Optional이므로)
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // password 설정을 factory로부터 위임해서 가지고 오겠다 라는 뜻 (???)
    }



























}
