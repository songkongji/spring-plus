package org.example.expert.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN(Authority.ADMIN),
    USER(Authority.USER);

    private final String userRole;  //각 열거형 상수에 해당하는 권한 이름을 저장하는 필드. 즉 Authority 클래스에서 정의한 권한 문자열을 저장함

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UerRole"));
    }

//스프링 시큐리티에서 제공하는 권한 기능을 사용하려면, 반드시 prefix로 "ROLE_”을 붙여야합니다. @Secured 안에 enum을 곧바로 넣지 못하기 때문
    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
