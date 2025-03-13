package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
//    private final UserRole userRole;
    private final String nickname;
    private final Collection<? extends GrantedAuthority> authorities;   //Collection 사용하는 예시 : 여자면서 선생님이면 역할이 2개

    public AuthUser(Long id, String email, UserRole userRole, String nickname) {
        this.id = id;
        this.email = email;
//        this.userRole = userRole;
        this.nickname = nickname;
        this.authorities = List.of(new SimpleGrantedAuthority(userRole.name()));
    }
}
