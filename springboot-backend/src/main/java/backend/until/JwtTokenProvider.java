package backend.until;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Tạo một chuỗi khóa bí mật an toàn có độ dài tối thiểu 256-bit để mã hóa hóa dữ liệu
    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // Cấu hình thời gian sống của Token (Ví dụ: 1 ngày = 86400000 mili-giây)
    private final long jwtExpirationMs = 86400000L;

    /**
     * Hàm sinh Token thật: Gói cả Username và UserId vào bên trong chuỗi mã hóa
     */
    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId) // Đính kèm userId kiểu Long vào payload của Token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }

    /**
     * Kiểm tra tính hợp lệ của Token gửi lên (Đúng khóa bí mật, chưa hết hạn)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Giải mã Token để lấy lại Username (Subject)
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Giải mã Token để lấy lại UserId kiểu Long
     */
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }
}