package com.ebubeokoli.postservice.model;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.core.env.Environment;

import com.ebubeokoli.postservice.helpers.JsonToClass;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Post {

    private static Environment env;
    
    private final static String dbPath = env.getProperty("postDbPath");
    
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
    
    public void setId(Long id) {
        this.id = id;
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

        Post user;

        if (JsonToClass.convertJsonToClass(obj, Post.class, nullables)) {

            user = new Post(Long.parseLong(obj.getString("id"))
            , obj.getString("heading")
            , obj.getString("content")
            , Long.parseLong(obj.getString("userId"))
            );
        }

        return user;
    }

    public static String database() throws IOException {

        Path postDbPath = Path.of(dbPath);
        String userData = Files.readString(postDbPath);

        return userData;
    }

    public boolean save() throws IOException {
        FileWriter writer = new FileWriter(dbPath);

        String post = this.toString() + ";\n";

        writer.append(post);
        writer.close();
    }

}
