package org.example.expert.config;

import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {   //AuthUserArgumentResolver를 대체

    private final AuthUser authUser;

    public JwtAuthenticationToken(AuthUser authUser) {
        super(authUser.getAuthorities());
        this.authUser = authUser;
        setAuthenticated(true); //Security 보안을 통과하려면 SecurityContext에 AbstractAuthenticationToken을 set해주어야한다.
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {  //컨트롤러 파라미터에서 @AuthenticationPrincipal를 이용해 어떤 인증 객체를 받을 지 설정한다.
        return authUser;
    }
}
