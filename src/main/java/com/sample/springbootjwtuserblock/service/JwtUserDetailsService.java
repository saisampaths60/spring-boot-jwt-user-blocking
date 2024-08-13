package com.sample.springbootjwtuserblock.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sample.springbootjwtuserblock.entity.User;
import com.sample.springbootjwtuserblock.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
				.password(user.getPassword()).authorities("USER") // You can set authorities here
				.accountExpired(false)
				.accountLocked(user.getAccountLockedUntil() != null
						&& user.getAccountLockedUntil().isAfter(LocalDateTime.now()))
				.credentialsExpired(false).disabled(!user.isEnabled()).build();
	}

}
