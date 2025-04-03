package org.example.diplomabackend.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key getSignKey(){
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(CustomUserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        claims.put("role", userDetails.getAuthorities());
        return generateToken(claims,userDetails);

    }
    public String generateToken(Map<String, Object> extraClaims, CustomUserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public String extractSubject(String token) {
        return extractClaims(token, Claims::getSubject);
    }
    public java.util.Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new java.util.Date());
    }
    public boolean isTokenValid(String token, CustomUserDetails userDetails) {
        return extractSubject(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
