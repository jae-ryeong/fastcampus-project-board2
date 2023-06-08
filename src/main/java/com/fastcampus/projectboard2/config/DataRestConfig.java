package com.fastcampus.projectboard2.config;

import com.fastcampus.projectboard2.domain.UserAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class DataRestConfig {

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig((config, cors) ->
                config.exposeIdsFor(UserAccount.class));    // UserAccount 클래스의 ID를 노출(expose)하도록 구성한다.
            //  Spring Data REST가 UserAccount 엔티티의 ID 필드를 클라이언트에게 노출하게 된다.
    }
}
