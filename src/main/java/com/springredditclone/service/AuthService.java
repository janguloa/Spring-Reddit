package com.springredditclone.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springredditclone.service.MailContentBuilder;
import com.springredditclone.dto.RegisterRequest;
import com.springredditclone.exception.SpringRedditException;
import com.springredditclone.model.NotificationEmail;
import com.springredditclone.model.Users;
import com.springredditclone.model.VerificationToken;
import com.springredditclone.repository.UserRepository;
import com.springredditclone.repository.VerificationTokenRepository;
import com.springredditclone.util.Constants;
import lombok.AllArgsConstructor;
import static java.time.Instant.now;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailContentBuilder mailContentBuilder;
	private final MailService mailService;
	
	
	public void signup(RegisterRequest registerRequest) {
		
		Users user = new Users();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(encodePassword(registerRequest.getPassword()));
		user.setCreated(now());
		user.setEnabled(false);
		
		userRepository.save(user);
		
		String token = generateVerificationToken(user);
		
		String message = mailContentBuilder.build("Gracias por registrarse en PanaUpgrade, "
				+ "por favor hacer click en la URL para activar su cuenta : "
				+ Constants.ACTIVATION_EMAIL + "/" + token);
		mailService.sendMail(new NotificationEmail("Por favor activar tu cuenta", user.getEmail(), message));
		
	}
	
	private String generateVerificationToken(Users user) {
		
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);
		
		return token;
		
	}
	
	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}
	
	public void verifyAccount(String token) {
		
		Optional<VerificationToken> verificationTokenOptional =
				verificationTokenRepository.findByToken(token);
		verificationTokenOptional.orElseThrow(() -> new SpringRedditException("Token inválido"));
		fetchUserAndEnable(verificationTokenOptional.get());
		
	}
	
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		
		String username = verificationToken.getUser().getUsername();
		Users user = userRepository.findByUsername(username).orElseThrow(() -> new
				SpringRedditException("Usuario no encontrado con el id - " + username));
		user.setEnabled(true);
		userRepository.save(user);
	}
	
}