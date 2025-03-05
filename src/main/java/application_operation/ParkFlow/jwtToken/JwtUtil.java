package application_operation.ParkFlow.jwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtUtil {
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Set<String> blacklistedTokens = new HashSet<>(); // 黑名單存儲
    private static final long EXPIRATION_TIME = 28800000; // 8 小時

    // 生成 Token
    public String generateToken(Integer userId, String chineseName, String englishName, String email, String cellphone, String carNumber, String carType, String roleName) {
        return Jwts.builder()
                .claim("chineseName", chineseName)
                .claim("englishName", englishName)
                .claim("email", email)
                .claim("cellphone", cellphone)
                .claim("carNumber", carNumber)
                .claim("carType", carType)
                .claim("userId", userId)
                .claim("roleName", roleName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // 取出 Token
    public String extractToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    // 驗證 Token
    public boolean validateToken(String token) {
        try {
            token = extractToken(token);
            if (blacklistedTokens.contains(token)) {
                return false; // 失效的 token
            }

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // 使用與生成 Token 相同的密鑰
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 將 Token 加入黑名單
    public void blacklistToken(String token) {
        blacklistedTokens.add(extractToken(token));
    }

    // 檢查 Token 是否在黑名單
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(extractToken(token));
    }
}
