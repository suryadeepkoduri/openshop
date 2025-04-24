package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.AddressRequest;
import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.dto.response.AddressResponse;
import com.suryadeep.openshop.dto.response.UserResponse;
import com.suryadeep.openshop.entity.Address;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.AddressRepository;
import com.suryadeep.openshop.repository.UserRepository;
import com.suryadeep.openshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final EntityMapper entityMapper;

    @Override
    public UserResponse getCurrentUser() {
        User user = getCurrentAuthenticatedUser();
        return entityMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(UserRegisterRequest userRequest) {
        User user = getCurrentAuthenticatedUser();
        user.setName(userRequest.getUsername());
        // Don't update email as it's a unique identifier
        // Don't update password here - should be handled by a separate password change service
        
        User updatedUser = userRepository.save(user);
        return entityMapper.toUserResponse(updatedUser);
    }

    @Override
    public List<AddressResponse> getAddressess() {
        User user = getCurrentAuthenticatedUser();
        return entityMapper.toAddressResponseList(user.getAddresses());
    }

    @Override
    @Transactional
    public AddressResponse addAddress(AddressRequest addressRequest) {
        User user = getCurrentAuthenticatedUser();
        Address address = entityMapper.toAddressEntity(addressRequest);
        address = addressRepository.save(address);
        user.getAddresses().add(address);
        userRepository.save(user);
        return entityMapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateUserAddress(Long id, AddressRequest updatedAddress) {
        User user = getCurrentAuthenticatedUser();
        Address existingAddress = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        // Update address fields
        existingAddress.setAddressLine(updatedAddress.getAddressLine());
        existingAddress.setCity(updatedAddress.getCity());
        existingAddress.setState(updatedAddress.getState());
        existingAddress.setPincode(updatedAddress.getPincode());
        existingAddress.setCountry(updatedAddress.getCountry());

        Address savedAddress = addressRepository.save(existingAddress);
        return entityMapper.toAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    public void deleteUserAddress(Long id) {
        User user = getCurrentAuthenticatedUser();
        Address address = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        user.getAddresses().remove(address);
        userRepository.save(user);
        addressRepository.delete(address);
    }

    public User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    
}