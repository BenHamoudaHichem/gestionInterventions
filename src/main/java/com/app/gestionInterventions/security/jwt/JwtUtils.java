package com.app.gestionInterventions.security.jwt;

import com.app.gestionInterventions.models.blackList.JwtBlackList;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.repositories.blacklist.JwtBlackListRepository;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.security.services.UserDetailsImpl;
import com.app.gestionInterventions.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    JwtBlackListRepository jwtBlackListRepository;
    @Autowired
    UserRepositoryImpl userRepository;
    @Autowired
    public JwtUtils() {
    }

    @Value("${gestInterv.app.jwtSecret}")
    private String jwtSecret;

    @Value("${gestInterv.app.jwtExpirationMs}")
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

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            this.jwtBlackListRepository.isInTheBlackList(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Could not get all claims Token from passed token",e.getMessage());
            claims = null;
        }
        return claims;
    }
    public void logout(String token)
    {


        this.jwtBlackListRepository.toTheBlackList(new JwtBlackList(token,
                this.userRepository.findById(((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get(),
                Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token.substring(7,token.length())).getBody().getIssuedAt(),
                null));
    }
}
