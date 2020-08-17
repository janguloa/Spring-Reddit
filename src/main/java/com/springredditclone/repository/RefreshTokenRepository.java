package com.springredditclone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springredditclone.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	 Optional<RefreshToken> findByToken(String token);

	 void deleteByToken(String token);

}