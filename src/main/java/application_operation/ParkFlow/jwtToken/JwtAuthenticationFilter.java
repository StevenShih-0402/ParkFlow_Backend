package application_operation.ParkFlow.jwtToken;

import application_operation.ParkFlow.exception.JwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter { //  extends OncePerRequestFilter
//    private final JwtUtil jwtUtil;
//
//    private static final List<String> WHITELISTED_URLS = List.of(
//            "/v3/api-docs.*",
//            "/swagger-ui.*"
//    );
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String requestURI = request.getRequestURI();
//        System.out.println("====" + requestURI + "====");
//
//        if (isWhitelisted(requestURI)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new JwtTokenException("JWT token not provided.");
//        }
//
//        String token = authHeader.substring(7);
//
//        if (jwtUtil.validateToken(token)) {
//            throw new JwtTokenException("JWT token validation failed.");
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isWhitelisted(String requestURI) {
//        return WHITELISTED_URLS.stream().anyMatch(requestURI::matches);
//    }
}
