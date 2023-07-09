package com.ebubeokoli.postservice.model;
// Generated May 28, 2015 9:05:46 AM by Hibernate Tools 3.1.0.beta4

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;



import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ebubeokoli.postservice.helpers.JsonToClass;

import org.json.JSONObject;


public class User {

    final static String dbPath = "/Users/ikponmwosaomorisiagbon/Desktop/CodeWorks/Java/postservice/src/main/resources/db/users.txt";

    private Long id;

    private String email;

    private String password;

    private String username;
    
	public User(Long id, String email, String password, String username) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public User(Long id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public User() {
	}

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
		this.password = password != null && password != "" ? new BCryptPasswordEncoder().encode(password): password; 
    }


    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	@Override
	public String toString() {
		return "User {\"id\": \"" + id + "\", \"email\":\"" + email + "\", \"password\":\""
				+ password + "\", \"username\":\"" + username + "\"}";
	}

    public static User fromJsonString(String jsonString) {

        JSONObject obj = new JSONObject(jsonString);
        System.out.println(obj);
        String[] nullables = {};

        User user = null;

        if (JsonToClass.convertJsonToClass(obj, User.class, nullables)) {

            user = new User(Long.parseLong(obj.getString("id"))
            , obj.getString("email")
            , obj.getString("password")
            , obj.getString("username")
            );
        }

        return user;
    }

    public static String database() throws IOException {
        System.out.println(dbPath);
        Path userDbPath = Path.of(dbPath);
        System.out.println(userDbPath);
        String userData = Files.readString(userDbPath);
        System.out.println("userDbPath");
        return userData;
    }

    public boolean save() throws IOException {

        String user = this.toString() + ";\n";

        Files.writeString(
            Path.of(dbPath),
            user,
            Files.exists(Path.of(dbPath)) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE
        );
        return true;
    }

}
