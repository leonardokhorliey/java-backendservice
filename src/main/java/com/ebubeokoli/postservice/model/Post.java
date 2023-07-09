package com.ebubeokoli.postservice.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.json.JSONObject;

import com.ebubeokoli.postservice.helpers.JsonToClass;

public class Post {
    
    final static String dbPath = "/Users/ikponmwosaomorisiagbon/Desktop/CodeWorks/Java/postservice/src/main/resources/db/posts.txt";
    
    private Long id;

    private String heading;

    private String content;

    private Long userId;
    
	public Post(Long id, String heading, String content, Long userId) {
        this.id = id;
        this.heading = heading;
        this.content = content;
        this.userId = userId;
    }

    public Post() {
	}

    public Long getId() {
        return this.id;
    }

    public String getHeading() {
        return this.heading;
    }
    
    public void setHeading(String newHeading) {
		this.heading = newHeading; 
    }


    public String getContent() {
        return this.content;
    }
    
    public void setContent(String newContent) {
        this.content = newContent;
    }

	public Long getUserId() {
		return userId;
	}

	@Override
	public String toString() {
		return "Post {\"id\": \"" + id + "\", \"heading\":\"" + heading + "\", \"content\":\""
				+ content + "\", \"userId\":\"" + userId + "\"}";
	}

    public static Post fromJsonObject(JSONObject obj) {

        String[] nullables = {};

        Post post = null;

        if (JsonToClass.convertJsonToClass(obj, Post.class, nullables)) {

            post = new Post(Long.parseLong(obj.getString("id"))
            , obj.getString("heading")
            , obj.getString("content")
            , Long.parseLong(obj.getString("userId"))
            );
        }

        return post;
    }

    public static String database() throws IOException {

        Path postDbPath = Path.of(dbPath);
        String userData = Files.readString(postDbPath);

        return userData;
    }

    public boolean save() throws IOException {
        String post = this.toString() + ";\n";

        Files.writeString(
            Path.of(dbPath),
            post,
            Files.exists(Path.of(dbPath)) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE
        );
        return true;
    }

    public boolean update(Post newVersion) throws IOException {
        String post = this.toString();

        String newVersion_ = newVersion.toString();

        String db = Post.database().replace(post, newVersion_);
        // .replaceAll(post, newVersion_);

        Files.writeString(
            Path.of(dbPath),
            db,
            StandardOpenOption.WRITE
        );

        return true;
    }

}
