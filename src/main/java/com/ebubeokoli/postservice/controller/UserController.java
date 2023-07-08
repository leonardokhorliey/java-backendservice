package com.ebubeokoli.postservice.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ebubeokoli.postservice.model.Request;
import com.ebubeokoli.postservice.model.Response;
import com.ebubeokoli.postservice.model.User;
import com.ebubeokoli.postservice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class UserController {
    
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public @ResponseBody Response<User> getUserById(@PathVariable String userId, HttpServletResponse response) {
        User user = null;
        boolean isSuccess = false;
        try {
            user = this.userService.getUserByIdOrEmail(Long.parseLong(userId), null).orElseThrow();
            response.setStatus(200);
            isSuccess = true;
        } catch (NoSuchElementException e) {
            response.setStatus(404);
        } catch (IOException e) {
            System.out.println("IOExc");
            response.setStatus(500);
        } catch (Exception e) {
            System.out.println(e.toString());
            response.setStatus(500);
        }
        
        return new Response<User>(isSuccess, user, "");
    }

    @GetMapping("/users")
    public @ResponseBody Response<List<User>> getAllUsers(@RequestBody Request request, HttpServletResponse response) {
        System.out.println("Already here");
        List<User> userList = Arrays.asList(new User[0]);
        boolean isSuccess = false;
        
        try {
            Pageable pageable = PageRequest.of(request.page, request.pageSize);
		    Page<User> users = this.userService.getAllUsers(pageable);
            System.out.println(users);
            userList = users.getContent();
            response.setStatus(200);

        } catch (Exception e) {
            System.out.println(e.getStackTrace().toString());
            response.setStatus(500);
        }

        return new Response<List<User>>(isSuccess, userList, "");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody Response<User> register(@RequestBody Request request, HttpServletResponse response) {
        System.out.println("Entered");
        User user = null;
        boolean isSuccess = false;
        try {
            if (request.email == null || request.password == null || request.email.length() <= 5 || request.password.length() <= 8
            || request.username == null || request.username.length() <= 2) {
                response.setStatus(400);
                return new Response<User>(isSuccess, user, "Invalid registration parameters sent");
            }
            if (this.userService.getUserByIdOrEmail(null, request.email).isPresent()
            || this.userService.getUserByUserName(request.username).isPresent()) {
                response.setStatus(403);
                return new Response<User>(isSuccess, user, "User exists with provided email exists");
            }

            user = this.userService.createUser(request.email, request.username, request.password);
            isSuccess = true;
            user.setPassword("");
        } catch (Exception e) {
            System.out.println(e.toString());
            response.setStatus(500);
        }
        
        return new Response<User>(isSuccess, user, "");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody Response<User> login(@RequestBody Request request, HttpServletResponse response) {
        User user = null;
        boolean isSuccess = false;
        String message = "Internal Server Error";
        try {
            if (request.email == null || request.password == null || request.email.length() <= 5 || request.password.length() <= 8) {
                response.setStatus(400);
                return new Response<User>(isSuccess, user, "Invalid login parameters sent");
            }
            user = this.userService.getUserByIdOrEmail(null, request.email).orElseThrow();

            if (new BCryptPasswordEncoder().matches(request.password, user.getPassword())) {
                response.setStatus(200);
                isSuccess = true;
                user.setPassword("");
                message = "Logged in successfully";
            } else {
                response.setStatus(403);
                user = null;
                message = "Incorrect login details";
            }
            
        } catch (NoSuchElementException e) {
            response.setStatus(404);
            message = "User with provided email not found";
        } catch (IOException e) {
            System.out.println("IOExc");
            response.setStatus(500);
            message += ": " + e.getMessage();
        } catch (Exception e) {
            System.out.println(e.toString());
            response.setStatus(500);
            message += ": " + e.getMessage();
        }
        return new Response<User>(isSuccess, user, message);
    }
}
