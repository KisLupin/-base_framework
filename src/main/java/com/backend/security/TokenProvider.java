package com.backend.security;

import com.backend.cache.CacheRepository;
import com.backend.cache.UserCache;
import com.backend.common.Constant;
import com.backend.domain.User;
import com.backend.enumeration.ErrorCode;
import com.backend.enumeration.UserType;
import com.backend.exception.RestApiException;
import com.backend.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final CacheRepository<UserCache> cacheRepository;

    public String createToken(User user, boolean rememberMe) {
        long plusTime = rememberMe ? jwtProperties.getTokenValidityInSecondsForRememberMe()
                : jwtProperties.getTokenValidityInSeconds();
        Date validity = Date.from(Instant.now().plusSeconds(plusTime));
        String subject = String.valueOf(user.getUsername());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        if (Objects.nonNull(user.getEmployee())) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(UserType.EMPLOYEE.name()));
//        }
//        if (Objects.nonNull(user.getMember())) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(UserType.MEMBER.name()));
//        }
        grantedAuthorities.add(new SimpleGrantedAuthority(UserType.USER.name()));
        return Jwts.builder().setAudience(Constant.TokenAudience.API).setSubject(subject)
                .claim(AUTHORITIES_KEY, grantedAuthorities)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey()).setExpiration(validity)
                .compact();
    }

    public String createTokenNotExpire(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String subject = String.valueOf(userPrincipal.getUsername());
        return Jwts.builder().setSubject(subject).setIssuedAt(new Date())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey()).compact();
    }

    public void setAuthenticationByToken(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return;
            }
            Optional<UserCache> userCache =
                    cacheRepository.find(Constant.TOKEN_PREFIX + token, UserCache.class);
            if (!userCache.isPresent()) {
                throw new RestApiException(ErrorCode.TOKEN_NOT_EXIST);
            }
            Claims claims = validateAndParseToken(token);
            if (Objects.isNull(claims)) {
                return;
            }
            UserDetails userDetails = null;
            String audience = claims.getAudience();
            String username = claims.getSubject();
            if (Constant.TokenAudience.API.equals(audience)) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_EXIST));
                userDetails = new UserPrincipal(user, token);
            }
//            if (Constant.TokenAudience.WEB.equals(audience)) {
//                UserWeb userWeb = userWebRepository.findByUsername(username)
//                        .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_EXIST));
//                userDetails = new UserPrincipal(userWeb, token);
//            }
            if (Objects.isNull(userDetails)) {
                return;
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    SecurityUtils.getCurrentUserJWT(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            // logger.logTrace("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            // logger.logTrace("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // logger.logTrace("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // logger.logTrace("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // logger.logTrace("JWT token compact of handler are invalid: {}",
            // e.getMessage());
        }
        return false;
    }

    public Claims validateAndParseToken(String token) {
        Claims claims = null;
        try {
            claims =
                    Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
        } catch (SignatureException e) {
            // logger.logTrace("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            // logger.logTrace("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // logger.logTrace("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // logger.logTrace("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // logger.logTrace("JWT token compact of handler are invalid: {}",
            // e.getMessage());
        } catch (Exception e) {
        }
        return claims;
    }
}
