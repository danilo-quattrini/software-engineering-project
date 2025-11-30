package securityconfigs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Profile("secured")
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomSuccessHandler successHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler((req, res, ex) -> {
                            String email = req.getParameter("email");
                            String encoded = URLEncoder.encode(email == null ? "" : email, StandardCharsets.UTF_8);
                            res.sendRedirect("/login?error&email=" + encoded);
                        })
                        .permitAll()
                )
                .exceptionHandling(e -> e
                        .accessDeniedPage("/access-denied"))
                .logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
                ROLE_ADMIN > ROLE_ENTERTAINER
                ROLE_ADMIN > ROLE_BUYER
                ROLE_ADMIN > ROLE_TRUSTEE
                ROLE_ADMIN > ROLE_PRODUCER
                ROLE_ADMIN > ROLE_DISTRIBUTOR
                ROLE_ADMIN > ROLE_TRANSFORMER
                """);
        return hierarchy;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public GrantedAuthoritiesMapper authoritiesMapper(RoleHierarchy roleHierarchy) {
        RoleHierarchyAuthoritiesMapper mapper = new RoleHierarchyAuthoritiesMapper(roleHierarchy);
        return mapper::mapAuthorities;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
