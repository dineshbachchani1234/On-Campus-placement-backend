package com.campus.service;

import com.campus.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility class for JWT token operations
 */
@Component
public class JwtTokenProvider {

  @Value("${app.jwt-secret}")
  private String jwtSecret;

  @Value("${app.jwt-expiration-milliseconds}")
  private long jwtExpirationInMs;

  /**
   * Generate a JWT token for a user
   * @param user The user
   * @return JWT token
   */
  public String generateToken(User user) {
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

    return Jwts.builder()
        .setSubject(user.getUserId().toString())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().toString())
        .setIssuedAt(currentDate)
        .setExpiration(expireDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  /**
   * Get user ID from JWT token
   * @param token The JWT token
   * @return User ID
   */
  public Integer getUserIdFromJWT(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();
    return Integer.parseInt(claims.getSubject());
  }

  /**
   * Validate JWT token
   * @param token The JWT token
   * @return true if valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}