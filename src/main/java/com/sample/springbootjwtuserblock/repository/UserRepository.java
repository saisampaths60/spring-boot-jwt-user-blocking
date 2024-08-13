package com.sample.springbootjwtuserblock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.springbootjwtuserblock.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
