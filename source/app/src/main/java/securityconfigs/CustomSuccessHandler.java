package securityconfigs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/profile");
            return;
        }

        if (roles.contains("ROLE_BUYER")) {
            response.sendRedirect("/products");
            return;
        }

        if (roles.contains("ROLE_PRODUCER") ||
                roles.contains("ROLE_TRANSFORMER") ||
                roles.contains("ROLE_DISTRIBUTOR")) {
            response.sendRedirect("/products");
            return;
        }

        if (roles.contains("ROLE_ENTERTAINER")) {
            response.sendRedirect("/events");
            return;
        }

        if (roles.contains("ROLE_TRUSTEE")) {
            response.sendRedirect("/products/review");
            return;
        }

        response.sendRedirect("/profile");
    }
}

