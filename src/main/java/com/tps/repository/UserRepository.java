package com.tps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tps.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
	
	Optional<User> findByUserName(String username);
	
	@Query("SELECT u FROM User u WHERE u.userName = :value OR u.gmail = :value")
	Optional<User> findByUserNameOrGmail(@Param("value") String value);

	boolean existsByGmail(String gmail);

}
