package com.tps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tps.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
	
	Optional<User> findByUserName(String username);

	boolean existsByGmail(String gmail);

}
