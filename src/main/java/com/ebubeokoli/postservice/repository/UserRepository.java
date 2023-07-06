package com.ebubeokoli.postservice.repository;

import java.util.List;

import com.ebubeokoli.postservice.model.User;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
	
	public Page<User> findAll(Pageable pageable);
	
	public User findByEmail(@Param("username") String username);
	
	public User findById(@Param("username") String userId);

	public User createUser(
		@Param("userId") Long userId
		, @Param("heading") String heading
		, @Param("content") String content
	);
	
	@Transactional
	void deleteByUserEmail(@Param("username") String username);

}