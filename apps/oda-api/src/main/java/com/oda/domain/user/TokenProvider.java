package com.oda.domain.user;

public interface TokenProvider {
    String createAccessToken(Long userId);
    String createRefreshToken(Long userId);
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
}
