package com.msd.chat.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final Environment environment;
    long TTl = 8640000000L;

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }


    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);

        return claimsResolver.apply(claims);
    }


    public Claims extractAllClaims(String jwt) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(jwt).getPayload();
    };


    public SecretKey getSignInKey() {
        final String SECRET_KEY = environment.getProperty("jwt.secret_key");
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }


    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TTl))
                .signWith(getSignInKey())
                .compact();
    }


    public boolean isTokenExpired(final String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    public Date extractExpiration(final String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            extractAllClaims(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public boolean isTokenValid(final String jwt, final UserDetails userDetails) {
        if (validateJwtToken(jwt)) {
            final String username = extractUsername(jwt);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
        }

        return false;
    }
}
