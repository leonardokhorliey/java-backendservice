package com.ebubeokoli.postservice.repository;

import java.util.List;

import com.ebubeokoli.postservice.model.Post;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


@Transactional
public interface PostRepository extends JpaRepository<Post, Long> {
	
	public Page<Post> findAll(Pageable pageable);
	
	public Post findByUser(@Param("username") String userId);
	
	public Post findById(@Param("username") String postId);

	public Post createPost(
		@Param("username") String username
		, @Param("email") String email
		, @Param("password") String password
	);

}