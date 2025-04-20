package by.testprojects.cardmanagementsystem.security.filter;

import by.testprojects.cardmanagementsystem.security.service.JwtService;
import by.testprojects.cardmanagementsystem.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.io.IOException;
import java.net.URI;

import static by.testprojects.cardmanagementsystem.Constants.BEARER_PREFIX;
import static org.apache.tomcat.websocket.Constants.AUTHORIZATION_HEADER_NAME;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // Формируем Problem-ответ
            response.setContentType("application/problem+json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());


            Problem problem = Problem.builder()
                    .withType(URI.create("https://api.example.com/errors/jwt-expired"))
                    .withTitle("Token Expired")
                    .withStatus(Status.UNAUTHORIZED)
                    .withDetail("JWT expired at " + e.getClaims().getExpiration())
                    .build();

            // Сериализуем Problem в JSON
           objectMapper.writeValue(response.getWriter(), problem);
        }
    }
}