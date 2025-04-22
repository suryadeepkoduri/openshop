package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.AddressRequest;
import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.dto.response.AddressResponse;
import com.suryadeep.openshop.dto.response.UserResponse;
import com.suryadeep.openshop.entity.Address;

import java.util.List;

public interface UserService {
    
    public UserResponse getCurrentUser();
    public UserResponse updateCurrentUser(UserRegisterRequest userRequest);
    public List<AddressResponse> getAddressess();
    public AddressResponse addAddress(AddressRequest addressRequest);
    public AddressResponse updateUserAddress(Long id,AddressRequest addressRequest);
    public void deleteUserAddress(Long id);
}
