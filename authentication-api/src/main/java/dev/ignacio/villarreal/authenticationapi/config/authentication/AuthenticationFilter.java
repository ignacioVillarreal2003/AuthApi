package dev.ignacio.villarreal.authenticationapi.config.authentication;

import dev.ignacio.villarreal.authenticationapi.application.helpers.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            log.debug("[AuthenticationFilter::doFilterInternal] Bearer token found");
            if (!jwtService.isTokenExpired(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                final String email = jwtService.extractUsername(token);
                log.debug("[AuthenticationFilter::doFilterInternal] Extracted email from token: {}", email);
                UserDetails user = customUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("[AuthenticationFilter::doFilterInternal] Security context updated with authenticated user");
            }
            else {
                log.debug("[AuthenticationFilter::doFilterInternal] Token expired or authentication already set");
            }
        }
        else {
            log.debug("[AuthenticationFilter::doFilterInternal] No bearer token found in Authorization header");
        }
        filterChain.doFilter(request, response);
    }
}
