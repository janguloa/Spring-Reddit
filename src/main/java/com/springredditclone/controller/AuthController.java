package com.springredditclone.controller;

import static org.springframework.http.HttpStatus.OK;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springredditclone.dto.AuthenticationResponse;
import com.springredditclone.dto.LoginRequest;
import com.springredditclone.dto.RefreshTokenRequest;
import com.springredditclone.dto.RegisterRequest;
import com.springredditclone.service.AuthService;
import com.springredditclone.service.RefreshTokenService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/signup")
	public ResponseEntity signup(@RequestBody RegisterRequest registerRequest) {

		authService.signup(registerRequest);
		return new ResponseEntity("Se ha registrado el usuario satisfactoriamente", OK);

	}

	@PostMapping("/login")
	public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
		return authService.login(loginRequest);
	}

	@GetMapping("accountVerification/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable String token) {

		authService.verifyAccount(token);

		return new ResponseEntity<>("Cuenta activada satisfactoriamente", OK);

	}
	
	@PostMapping("/refresh/token")
	public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		
		return authService.refreshToken(refreshTokenRequest);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		
		refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
		
		return ResponseEntity.status(OK).body("Refresh Token eliminado correctamente!!");
		
	}
}