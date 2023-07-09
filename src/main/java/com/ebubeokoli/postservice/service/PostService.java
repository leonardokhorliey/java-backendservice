package com.ebubeokoli.postservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ebubeokoli.postservice.model.Post;

import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.PageImpl;

@Service
public class PostService {
    
    /**
     * @param pageable Pagination object defining number of records and page number
     * @return Page of posts containing number of posts defined in pageable and matching the page number
     * @throws IOException
     */
    public Page<Post> getAllPosts(Pageable pageable) throws IOException {

        String postData = Post.database();
        ArrayList<Post> postList = new ArrayList<>();

        if (postData.length() == 0) return new PageImpl<Post>(postList, pageable, 0);

        String[] allPosts = postData.split(";\n");

        int pageSize = pageable.getPageSize();
        int page = pageable.getPageNumber();

        int recordCounter = 0;
        int pageSizeCounter = 0;
        for (String postStr : allPosts) {
            if (recordCounter >= page*pageSize && pageSizeCounter < pageSize) {
                postStr = postStr.replaceFirst("Post ", "");
                Post post = Post.fromJsonObject(new JSONObject(postStr));
                if (post != null) {
                    postList.add(post);
                }
                pageSizeCounter++;
            }
            recordCounter += 1;
        }

        return new PageImpl<Post>(postList, pageable, allPosts.length);
    }

    public Optional<Post> getPostById(Long postId) throws IOException {

        String postData = Post.database();

        if (postData.length() == 0) return Optional.empty();

        String[] allPosts = postData.split(";\n");

        for (String postStr : allPosts) {
            postStr = postStr.replaceFirst("Post ", "");
            JSONObject obj = new JSONObject(postStr);

            if (obj.getString("id").equals(postId.toString())) return Optional.of(Post.fromJsonObject(obj));
            
        }

        return Optional.empty();
    }


    /**
     * @param pageable Pagination object defining number of records and page number
     * @param userId User ID for user 
     * @return Page of user's posts containing number of posts defined in pageable and matching the page number
     * @throws IOException
     */
    public Page<Post> getUserPosts(Pageable pageable, Long userId) throws IOException {

        String postData = Post.database();
        ArrayList<Post> postList = new ArrayList<>();

        if (postData.length() == 0) return new PageImpl<Post>(postList, pageable, 0);

        String[] allPosts = postData.split(";\n");

        int pageSize = pageable.getPageSize();
        int page = pageable.getPageNumber();

        int pageSizeCounter = 0;
        int recordCounter = 0;
        for (String postStr: allPosts) {
            postStr = postStr.replaceFirst("Post ", "");
            JSONObject obj = new JSONObject(postStr);

            if (obj.getString("userId").equals(userId.toString())) {
                if (pageSizeCounter < pageSize && recordCounter >= page*pageSize) {
                    Post post = Post.fromJsonObject(obj);
                    postList.add(post);
                    pageSizeCounter++;
                }
                recordCounter += 1;
            }
        }
        return new PageImpl<Post>(postList, pageable, recordCounter);
    }

    public Post createPost(String heading, String content, Long userId) throws IOException {
        String postData = Post.database();

        Long newPostId = Long.valueOf(1);
        if (!postData.equals("")) {
            String[] allPosts = postData.split(";\n");

            String lastPost = allPosts[allPosts.length - 1].replaceFirst("Post ", "");

            JSONObject obj = new JSONObject(lastPost);
            Long lastId = Long.parseLong(obj.getString("id"));

            newPostId = lastId + 1;
        }

        Post post = new Post(newPostId, heading, content, userId);

        post.save();

        return post;
    }

    public boolean updatePost(Long postId, String fieldUpdated, Object newValue) throws IllegalAccessException, IOException, InvocationTargetException {

        Post postToUpdate = getPostById(postId).orElseThrow();
        Post newPost = new Post(postToUpdate.getId(), postToUpdate.getHeading(), postToUpdate.getContent(), postToUpdate.getUserId());

        Map<String, Method> methodsMapper = new HashMap<>();

        Arrays.asList(Post.class.getMethods())
        .stream()
        .forEach(field -> {
            if (field.getName().startsWith("set")) {
                String ky = field.getName().replaceAll("set", "").toLowerCase();
                methodsMapper.put(ky, field);
            }
        });
        System.out.println(methodsMapper.get("heading").getName());

        if (methodsMapper.get(fieldUpdated) != null) {
            methodsMapper.get(fieldUpdated).invoke(newPost, newValue);

            return postToUpdate.update(newPost);
        }
        
        return false;
        
    }
}
