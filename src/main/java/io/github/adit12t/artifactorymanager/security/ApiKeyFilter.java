package io.github.adit12t.artifactorymanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Authenticates the X-API-Key header, comparing in constant time so timing doesn't leak the key. */
public class ApiKeyFilter extends OncePerRequestFilter {

    static final String HEADER = "X-API-Key";

    private final byte[] readKey;
    private final byte[] adminKey;

    public ApiKeyFilter(ApiKeyProperties keys) {
        this.readKey = keys.read().getBytes(StandardCharsets.UTF_8);
        this.adminKey = keys.admin().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HEADER);
        if (header != null) {
            byte[] presented = header.getBytes(StandardCharsets.UTF_8);
            if (MessageDigest.isEqual(presented, adminKey)) {
                authenticate("admin", "ROLE_READER", "ROLE_ADMIN");
            } else if (MessageDigest.isEqual(presented, readKey)) {
                authenticate("reader", "ROLE_READER");
            }
        }
        chain.doFilter(request, response);
    }

    private static void authenticate(String principal, String... roles) {
        var auth = new UsernamePasswordAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList(roles));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
