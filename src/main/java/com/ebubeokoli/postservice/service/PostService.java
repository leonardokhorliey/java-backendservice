package com.ebubeokoli.postservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.io.IOException;

import com.ebubeokoli.postservice.model.Post;

import org.springframework.stereotype.Service;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.PageImpl;


public class PostService {
    
    /**
     * @param pageable Pagination object defining number of records and page number
     * @return Page of posts containing number of posts defined in pageable and matching the page number
     * @throws IOException
     */
    public Page<Post> getAllPosts(Pageable pageable) throws IOException {

        String postData = Post.database();
        ArrayList<Post> postList = new ArrayList<>();

        String[] allPosts = postData.split(";\n");

        int pageSize = pageable.getPageSize();
        int page = pageable.getPageNumber();

        String[] expectedPosts = Arrays.copyOfRange(allPosts, page * pageSize, page * pageSize + pageSize - 1);

        for (String postStr: expectedPosts) {
            postStr = postStr.replaceFirst("Post ", "");
            JSONObject obj = new JSONObject(postStr);
            Post post = Post.fromJsonObject(obj);

            if (post != null) {
                postList.add(post);
            }
        }

        return new PageImpl<Post>(postList, pageable, allPosts.length);
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
}
