package com.eCommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eCommerce.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	User findByEmail(String email);

	List<User> findByRole(String role);

	User findByResetToken(String token);

	
	 @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_USER'")
	 Long countCustomers();
}
