package com.ebubeokoli.postservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.io.IOException;

import com.ebubeokoli.postservice.model.User;

import org.springframework.stereotype.Service;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.PageImpl;


@Service
public class UserService {

    public Page<User> getAllUsers(Pageable pageable) throws IOException {

        String userData = User.database();
        ArrayList<User> userList = new ArrayList<>();

        String[] allUsers = userData.split(";\n");

        int pageSize = pageable.getPageSize();
        int page = pageable.getPageNumber();

        String[] expectedUsers = Arrays.copyOfRange(allUsers, page * pageSize, page * pageSize + pageSize - 1);

        for (String userString: expectedUsers) {
            userString = userString.replaceFirst("User ", "");
            
            User user = User.fromJsonString(userString);

            if (user != null) {
                userList.add(user);
            }
        }

        return new PageImpl<User>(userList, pageable, allUsers.length);
    }

    public Optional<User> getUserByIdOrEmail(Long userId, String email) throws IOException {

        String userData = User.database();

        String[] allUsers = userData.split(";\n");

        for (String userString: allUsers) {
            userString = userString.replaceFirst("User ", "");
            JSONObject obj = new JSONObject(userString);

            if (obj.getString("id") == userId.toString() || obj.getString("email") == email) 
            return Optional.of(User.fromJsonString(userString));
        }

        return null;
    }

    public User createUser(String email, String username, String password) throws JSONException, NumberFormatException, IOException {

        String userData = User.database();

        String[] allUsers = userData.split(";\n");

        String lastUser = allUsers[allUsers.length - 1].replaceFirst("User ", "");

        JSONObject obj = new JSONObject(lastUser);
        Long lastId = Long.parseLong(obj.getString("id"));
        Long newUserId = lastId + 1;
        User user = new User(newUserId, email, password, username);

        user.save();

        return user;
    }
}
