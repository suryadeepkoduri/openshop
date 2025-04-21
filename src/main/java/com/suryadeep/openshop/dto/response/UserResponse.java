package com.suryadeep.openshop.dto.response;


import com.suryadeep.openshop.entity.User;
import lombok.Data;

@Data
public class UserResponse {
    private long userId;
    private String username;
    private String email;

    public static UserResponse fromUser(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId());
        userResponse.setUsername(user.getName());
        userResponse.setEmail(user.getEmail());
        return userResponse;
    }
}