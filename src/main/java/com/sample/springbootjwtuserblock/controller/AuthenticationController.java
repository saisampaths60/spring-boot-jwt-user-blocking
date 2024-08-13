package com.sample.springbootjwtuserblock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.sample.springbootjwtuserblock.config.JwtTokenUtil;
import com.sample.springbootjwtuserblock.entity.LoginAttempt;
import com.sample.springbootjwtuserblock.entity.Token;
import com.sample.springbootjwtuserblock.entity.User;
import com.sample.springbootjwtuserblock.repository.LoginAttemptRepository;
import com.sample.springbootjwtuserblock.repository.TokenRepository;
import com.sample.springbootjwtuserblock.repository.UserRepository;
import com.sample.springbootjwtuserblock.service.JwtUserDetailsService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private LoginAttemptRepository loginAttemptRepository;

	@Autowired
	private TokenRepository tokenRepository;

	private static final int MAX_ATTEMPTS = 5;
	private static final int ATTEMPT_WINDOW = 5; // in minutes

	@PostMapping("/login")
	public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
		UserDetails user = jwtUserDetailsService.loadUserByUsername(username);

		if (!user.isAccountNonLocked()) {
			throw new RuntimeException("Account is locked. Try again later.");
		}

		if (user.isEnabled() && password.equals(user.getPassword())) {
			// Successful login
			String token = jwtTokenUtil.generateToken(user);
			tokenRepository.save(new Token(token));

			// Clear failed login attempts on successful login
			loginAttemptRepository.deleteAllByUsername(username);

			Map<String, String> response = new HashMap<>();
			response.put("token", token);
			return response;
		} else {
			// Handle failed login attempt
			handleFailedAttempt(username);
			throw new RuntimeException("Invalid credentials");
		}
	}

	private void handleFailedAttempt(String username) {

		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		// Record the failed attempt
		LoginAttempt attempt = new LoginAttempt();
		attempt.setUsername(username);
		attempt.setAttemptTime(LocalDateTime.now());
		loginAttemptRepository.save(attempt);

		// Check the number of failed attempts in the window
		LocalDateTime windowStart = LocalDateTime.now().minusMinutes(ATTEMPT_WINDOW);
		List<LoginAttempt> attempts = loginAttemptRepository.findByUsernameAndAttemptTimeAfter(username, windowStart);

		if (attempts.size() >= MAX_ATTEMPTS) {
			// Lock the user account
			user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(ATTEMPT_WINDOW));
			userRepository.save(user);
			loginAttemptRepository.deleteAllByUsername(username); // Clean up old attempts
			throw new RuntimeException("Account locked. Try again later.");
		}
	}

	@PostMapping("/validate")
	public Map<String, String> validateToken(@RequestParam String token) {
		Token storedToken = tokenRepository.findByToken(token);
		if (storedToken != null) {
			return Map.of("status", "valid");
		}
		return Map.of("status", "invalid");
	}
}
