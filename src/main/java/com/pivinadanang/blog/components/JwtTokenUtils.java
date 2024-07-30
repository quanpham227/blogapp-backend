package com.pivinadanang.blog.components;

import com.pivinadanang.blog.models.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;


    public String generateToken(UserEntity user) {
            //properties => claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", user.getPhoneNumber());
            try {
                String token = Jwts.builder()
                        .setClaims(claims)
                        .setSubject(user.getPhoneNumber())
                        .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                        .compact();
                return token;
            }catch (Exception e){
                //logger
                System.err.println("Cannot create jwt token , error: " + e.getMessage());
                return null;
            }
    }
    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
        return Keys.hmacShaKeyFor(bytes);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()  // Khởi tạo JwtParserBuilder
                .verifyWith(getSignInKey())  // Sử dụng verifyWith() để thiết lập signing key
                .build()  // Xây dựng JwtParser
                .parseSignedClaims(token)  // Phân tích token đã ký
                .getPayload();  // Lấy phần body của JWT, chứa claims
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    public String getSubject(String token) {
        return  extractClaim(token, Claims::getSubject);
    }
//    public boolean validateToken(String token, User userDetails) {
//        try {
//            String subject = extractClaim(token, Claims::getSubject);
//            //subject is phoneNumber or email
//            Token existingToken = tokenRepository.findByToken(token);
//            if(existingToken == null ||
//                    existingToken.isRevoked() == true ||
//                    !userDetails.isActive()
//            ) {
//                return false;
//            }
//            return (subject.equals(userDetails.getUsername()))
//                    && !isTokenExpired(token);
//        } catch (MalformedJwtException e) {
//            logger.error("Invalid JWT token: {}", e.getMessage());
//        } catch (ExpiredJwtException e) {
//            logger.error("JWT token is expired: {}", e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            logger.error("JWT token is unsupported: {}", e.getMessage());
//        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty: {}", e.getMessage());
//        }
//
//        return false;
//    }
    public String extractPhoneNumbers(String token) {
        return extractClaim(token, Claims::getSubject);
    }
}
