package com.jango.socialmediaapi.utils;

import com.jango.socialmediaapi.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtils {


  @Value("${ideateller.app.jwtSecret}")
  private String jwtSecret;

  @Value("${ideateller.app.jwtExpirationMs}")
  private int jwtExpirationMs;



  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();


    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

//  public String doGenerateRefreshToken(Users users) {
//    return Jwts.builder()
//        .setSubject( users.getEmail())
//        .setIssuedAt(new Date())
//        .setExpiration(new Date((new Date()).getTime() + refreshExpirationDateInMs))
//        .signWith(SignatureAlgorithm.HS512, jwtSecret)
//        .compact();
//
//  }

  public String generateToken(String userName){
    log.info("Generating refresh token::::::::");
    Map<String,Object> claims=new HashMap<>();
    return createToken(userName);
  }

  private String createToken(String userName) {
    return Jwts.builder()
            .setSubject((userName))
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
  }

  private String extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
    }
    return null;
  }

  public String getCustomerEmailFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }

    return null;
  }

  public String parseJwt(String rawToken) {
    return rawToken.startsWith("Bearer ") ? rawToken.substring(7) : null;
  }


  public boolean hasTokenExpired(String token) {
    try {
      Claims claims = Jwts.parser()
          .setSigningKey(jwtSecret)
          .parseClaimsJws(token).getBody();

      Date tokenExpirationDate = claims.getExpiration();
      Date today = new Date();

      return tokenExpirationDate.before(today);
    } catch (ExpiredJwtException | UnsupportedJwtException |
        MalformedJwtException |SignatureException |
        IllegalArgumentException ex) {
      return true;
    }

  }


}
