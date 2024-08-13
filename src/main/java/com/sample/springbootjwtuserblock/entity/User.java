package com.sample.springbootjwtuserblock.entity;

import lombok.Data;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "USER_DATA")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;
	private boolean enabled; // Indicates if the user is active
	private LocalDateTime accountLockedUntil; // Time until which the account is locked
}
