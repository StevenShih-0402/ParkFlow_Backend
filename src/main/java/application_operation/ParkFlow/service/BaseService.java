package application_operation.ParkFlow.service;

import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.exception.JwtTokenException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class BaseService {

    public UsersBaseDto getUserBase() {

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        JwtUtil jwtUtil = new JwtUtil();

        HttpServletRequest request = attrs.getRequest();
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.getClaimsFromToken(token);

        return UsersBaseDto.builder()
                .chineseName(claims.get("chineseName", String.class))
                .englishName(claims.get("englishName", String.class))
                .email(claims.get("email", String.class))
                .cellphone(claims.get("cellphone", String.class))
                .carNumber(claims.get("carNumber", String.class))
                .carType(claims.get("carType", String.class))
                .userId(claims.get("userId", Integer.class))
                .roleName(claims.get("roleName", String.class))
                .build();
    }

    public void validateToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        JwtUtil jwtUtil = new JwtUtil();

        HttpServletRequest request = attrs.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtTokenException("Missing or invalid Authorization header");
        }

        if(!jwtUtil.validateToken(authHeader)) {
            throw new JwtTokenException("Invalid JWT token");
        }
    }
}
