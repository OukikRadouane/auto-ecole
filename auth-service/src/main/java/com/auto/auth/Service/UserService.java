package com.auto.auth.Service;

import com.auto.auth.Dto.Request.UpdateProfileRequest;
import com.auto.auth.Dto.Request.UpdateRoleRequest;
import com.auto.auth.Dto.Response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getMyProfile(String userId);
    UserResponse updateMyProfile(String userId, UpdateProfileRequest request);
    void deleteMyProfile(String userId);
    String exportMyData(String userId);

    List<UserResponse> getAllUsers();
    UserResponse getUserById(String userId);
    UserResponse updateUserRole(String userId, UpdateRoleRequest request);
    void deleteUser(String userId);
}
