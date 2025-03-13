package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity  //웹 보안 지원을 활성화하고 Spring MVC 통합을 제공한다.
@EnableMethodSecurity(securedEnabled = true)    //@Secured, @PreAuthorize, @PostAuthorize 와 같은 메서드 레벨 보안 애노테이션을 활성화
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {  //스프링 시큐리티는 BCryptPasswordEncoder를 제공해준다. @Bean으로 등록해 사용한다.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {  //h2 웹 콘솔을 시큐리티가 막는 걸 무시하는 코드
        return (web -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, SecurityContextHolderAwareRequestFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)//SSR이 아니기 때문에 폼 기반 로그인 기능이 필요하지 않으므로 비활성화한다. UsernamePasswordAuthenticationFilter, DefaultLoginPageGeneratingFilter 비활성화
                .anonymous(AbstractHttpConfigurer::disable)//익명 사용자 권한은 필요 없다.(인증은 JWT를 통해 이루어진다.) AnonymousAuthenticationFilter 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)//커스텀 FIlter를 사용하므로 비활성화 한다. BasicAuthenticationFilter 비활성화
                .logout(AbstractHttpConfigurer::disable)//로그아웃은 세션 정보를 지우는 요청이다. 세션이 아니므로 비활성화 한다. LogoutFilter 비활성화
                .rememberMe(AbstractHttpConfigurer::disable)//StateLess이기 때문에 Remember를 할 수가 없다. RememberMeAuthenticationFilter 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(request -> request.getRequestURI().startsWith("/auth")).permitAll()
                        .requestMatchers("/test").hasAuthority(UserRole.Authority.ADMIN)
                        .requestMatchers("/open").permitAll()
                        .requestMatchers("/error").permitAll()  //에러가 무조건 403으로만 반환되는걸 해결하는 코드
                        .anyRequest().authenticated()   //SecurityContext에 AbstractAuthenticationToken이 set이 되어 있다면 통과를 시키겠단 의미이다.
                )
                .build();
    }
}
