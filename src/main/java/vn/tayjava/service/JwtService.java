package vn.tayjava.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.tayjava.common.TokenType;

public interface JwtService {

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String extractUsername(String token, TokenType type);

    boolean isValidToken(String token, TokenType type, UserDetails userDetails);
}
