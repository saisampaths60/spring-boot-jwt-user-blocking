package com.sample.springbootjwtuserblock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.springbootjwtuserblock.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
	Token findByToken(String token);
}
