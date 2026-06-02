package de.joshuaschnabel.todo.infrastruktur.security.filter;

import de.joshuaschnabel.todo.application.port.out.TokenServicePort;
import de.joshuaschnabel.todo.infrastruktur.security.adapter.UserPrincipalService;
import de.joshuaschnabel.todo.infrastruktur.security.model.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenServicePort tokenServicePort;
    private final UserPrincipalService userPrincipalService;

    public JwtAuthenticationFilter(TokenServicePort tokenServicePort, UserPrincipalService userPrincipalService) {
        this.tokenServicePort = tokenServicePort;
        this.userPrincipalService = userPrincipalService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        Optional<UUID> userId = tokenServicePort.extractUserId(token);
        if (userId.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserPrincipal principal = userPrincipalService.loadByUserId(userId.get());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
