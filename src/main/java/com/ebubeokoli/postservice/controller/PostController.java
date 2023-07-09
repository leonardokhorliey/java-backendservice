package com.ebubeokoli.postservice.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ebubeokoli.postservice.model.Post;
import com.ebubeokoli.postservice.model.Request;
import com.ebubeokoli.postservice.model.Response;
import com.ebubeokoli.postservice.service.PostService;
import com.ebubeokoli.postservice.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody Response<List<Post>> getPostsByUser(
        @RequestBody Request request
        , @RequestParam(name = "owner", required = false) Long userId
        , HttpServletResponse response
        ) {

        System.out.println("Already here");
        List<Post> postList = Arrays.asList(new Post[0]);
        boolean isSuccess = false;
        String message = "";
        
        try {
            Pageable pageable = PageRequest.of(request.page, request.pageSize);
		    Page<Post> posts = userId != null ? this.postService.getUserPosts(pageable, userId) : this.postService.getAllPosts(pageable);
            System.out.println(posts);
            postList = posts.getContent();
            isSuccess = true;
            response.setStatus(200);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(500);
            message = "Internal server error";
        }

        return new Response<List<Post>>(isSuccess, postList, message);

    }

    @RequestMapping(value = "{postId}", method = RequestMethod.GET)
    public @ResponseBody Response<Post> getPostById(
        @PathVariable Long postId
        , HttpServletResponse response
        ) {

        Post post = null;
        boolean isSuccess = false;
        String message = "";
        
        try {
		    post = this.postService.getPostById(postId).orElseThrow();
            isSuccess = true;
            response.setStatus(200);

        } catch (NoSuchElementException e) {
            response.setStatus(404);
            message = "Post with provided ID does not exist";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(500);
            message = "Internal server error";
        }

        return new Response<Post>(isSuccess, post, message);

    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody Response<Post> createPost(
        @RequestBody Request request
        , @RequestParam(name = "owner") Long userId
        , HttpServletResponse response
        ) {

        System.out.println("Already here");
        Post post = null;
        boolean isSuccess = false;
        String message = "";
        
        try {
            this.userService.getUserByIdOrEmail(userId, null).orElseThrow();
            post = this.postService.createPost(request.heading, request.content, userId);
            isSuccess = true;
            response.setStatus(200);

        } catch (NoSuchElementException e) {
            response.setStatus(403);
            message = "User with provided ID does not exist";
        } catch (Exception e) {
            response.setStatus(500);
            message = "Internal server error: ";
        }

        return new Response<Post>(isSuccess, post, message);

    }

    @RequestMapping(value = "/{postId}/update", method = RequestMethod.PUT)
    public @ResponseBody Response<List<String>> updatePost(
        @RequestBody Request request
        , @PathVariable Long postId
        , HttpServletResponse response
        ) {

        System.out.println("Already here");
        List<String> fieldsUpdated = new ArrayList<>();
        boolean isSuccess = false;
        String message = "";
        
        try {
            if (request.heading != null) {
                boolean val = this.postService.updatePost(postId, "heading", request.heading);
                System.out.println(val);
                fieldsUpdated.add("heading");
                isSuccess = val;
            }

            if (request.content != null) {
                boolean val = this.postService.updatePost(postId, "content", request.content);
                System.out.println(val);
                fieldsUpdated.add("content");
                isSuccess = val;
            }
            response.setStatus(200);

        } catch (NoSuchElementException e) {
            response.setStatus(404);
            message = "Post with provided ID does not exist";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(500);
            message = "Internal server error: ";
        }

        return new Response<List<String>>(isSuccess, fieldsUpdated, message);

    }
    
}
