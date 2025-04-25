package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.AddressRequest;
import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.dto.response.AddressResponse;
import com.suryadeep.openshop.dto.response.UserResponse;
import com.suryadeep.openshop.entity.Address;
import com.suryadeep.openshop.entity.User;

import java.util.List;

public interface UserService {
    
    UserResponse getCurrentUser();
    UserResponse updateCurrentUser(UserRegisterRequest userRequest);
    List<AddressResponse> getAddressess();
    AddressResponse addAddress(AddressRequest addressRequest);
    AddressResponse updateUserAddress(Long id,AddressRequest addressRequest);
    void deleteUserAddress(Long id);
    User getCurrentAuthenticatedUser();
}
