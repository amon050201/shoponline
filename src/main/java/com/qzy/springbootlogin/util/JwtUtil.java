package com.qzy.springbootlogin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY_STRING = "YourSecureSecretKeyForJWTTokenGeneration2026!@#$%^&*()_+-=";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    
    public static final String TOKEN_CLAIM_USERNAME = "username";
    public static final String TOKEN_CLAIM_ROLE = "role";

    public static String generateToken(String username) {
        return generateToken(username, null, null);
    }

    public static String generateToken(String username, Long userId, Integer roleType) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .claim(TOKEN_CLAIM_USERNAME, username)
                .claim("userId", userId)
                .claim("roleType", roleType)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Object uid = claims.get("userId");
            return uid != null ? Long.valueOf(uid.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getRoleTypeFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Object role = claims.get("roleType");
            return role != null ? Integer.valueOf(role.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        if (token == null || !validateToken(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
