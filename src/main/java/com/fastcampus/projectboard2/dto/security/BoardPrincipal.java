package com.fastcampus.projectboard2.dto.security;

import com.fastcampus.projectboard2.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record BoardPrincipal (
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname,
        String memo
) implements UserDetails {

    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new BoardPrincipal(
                username, password,
                roleTypes.stream().map(RoleType::getName).map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet()),
                email, nickname, memo
        );
    }

    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }

    public UserAccountDto toDto() { // 이미 인스턴스가 만들어진 상황이니까 static 메서드가 아니다.
        return UserAccountDto.of(
                username, password, email, nickname, memo
        );  // 역으로 회원 정보를 저장하기 위해 생성
    }

    @Override    public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}  // 로그인한 사용자가 어떤 권한을 가지고있냐, 여기서는 모두 동일한 권한이므로 X

    @Override    public String getPassword() {return password;}

    @Override    public String getUsername() {return username;}

    @Override    public boolean isAccountNonExpired() {        return true;    }

    @Override    public boolean isAccountNonLocked() {        return true;    }

    @Override    public boolean isCredentialsNonExpired() {        return true;    }

    @Override public boolean isEnabled() {   return true;    }

    public enum RoleType {
        USER("ROLE_USER");  // 권한은 단 하나, USER 뿐이다.

        @Getter private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }
}
