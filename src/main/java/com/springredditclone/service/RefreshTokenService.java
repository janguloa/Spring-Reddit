package com.springredditclone.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springredditclone.exception.SpringRedditException;
import com.springredditclone.model.RefreshToken;
import com.springredditclone.repository.RefreshTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	RefreshToken generateRefreshToken() {
		
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setCreatedDate(Instant.now());
		
		return refreshTokenRepository.save(refreshToken);
	}
	void validateRefreshToken(String token) {
		
		refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new SpringRedditException("Invalid refresh Token"));
	}
	
	public void deleteRefreshToken(String token) {
		
		refreshTokenRepository.deleteByToken(token);
	}
}