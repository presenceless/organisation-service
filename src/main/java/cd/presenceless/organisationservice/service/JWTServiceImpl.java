package cd.presenceless.organisationservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {
    @Value("${jwt.secret}")
    private String SECRET;

    public boolean validateToken(final String token) {
        try {
            int length = "Bearer ".length();

            final var claims = Jwts.
                    parserBuilder().
                    setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token.substring(length))
                    .getBody();

            return switch (claims) {
                case null -> false;
                case Exception ignored -> false;
                default -> true;
            };
        } catch (Exception e) {
            return false;
        }
    }

    public String createToken(Long orgId, Map<String, Object> claims) {
        final var oneYear = oneYear();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(orgId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + oneYear))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private long oneYear() {
        return ChronoUnit.YEARS.getDuration().toMillis();
    }
}
