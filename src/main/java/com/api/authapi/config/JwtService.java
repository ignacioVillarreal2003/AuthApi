package com.api.authapi.config;

import com.api.authapi.config.properties.JwtProperties;
import com.api.authapi.domain.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("id", Long.class));
    }

    public List<String> extractUserRole(String token) {
        return extractClaims(token, claims -> claims.get("role", List.class));
    }

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return buildToken(extraClaims, user, jwtProperties.getExpirationToken());
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return buildToken(extraClaims, user, jwtProperties.getExpirationRefreshToken());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>())
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    private  <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .json(new JacksonDeserializer<>())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
