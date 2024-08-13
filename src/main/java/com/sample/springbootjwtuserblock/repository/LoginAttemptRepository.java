package com.sample.springbootjwtuserblock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sample.springbootjwtuserblock.entity.LoginAttempt;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
	List<LoginAttempt> findByUsernameAndAttemptTimeAfter(String username, LocalDateTime attemptTime);

	@Transactional
	@Modifying
	@Query(value = "delete from LOGIN_ATTEMPT where username=:username", nativeQuery = true)
	void deleteAllByUsername(@Param(value = "username") String username); // For cleanup
}
