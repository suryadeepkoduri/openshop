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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final EntityMapper entityMapper;

    @Override
    @Cacheable(value = "users", key = "#root.target.getCurrentAuthenticatedUserEmail()")
    public UserResponse getCurrentUser() {
        User user = getCurrentAuthenticatedUser();
        log.info("Fetching current user profile for user with ID: {}", user.getId());
        return entityMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#root.target.getCurrentAuthenticatedUserEmail()")
    public UserResponse updateCurrentUser(UserRegisterRequest userRequest) {
        User user = getCurrentAuthenticatedUser();
        log.info("Updating current user profile for user with ID: {}", user.getId());
        user.setName(userRequest.getUsername());
        // Don't update email as it's a unique identifier
        // Don't update password here - should be handled by a separate password change service

        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for user with ID: {}", user.getId());
        return entityMapper.toUserResponse(updatedUser);
    }

    @Override
    @Cacheable(value = "users", key = "'addresses_' + #root.target.getCurrentAuthenticatedUserEmail()")
    public List<AddressResponse> getAddressess() {
        User user = getCurrentAuthenticatedUser();
        log.info("Fetching addresses for user with ID: {}", user.getId());
        return entityMapper.toAddressResponseList(user.getAddresses());
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "'addresses_' + #root.target.getCurrentAuthenticatedUserEmail()")
    public AddressResponse addAddress(AddressRequest addressRequest) {
        User user = getCurrentAuthenticatedUser();
        log.info("Adding new address for user with ID: {}", user.getId());
        Address address = entityMapper.toAddressEntity(addressRequest);
        address = addressRepository.save(address);
        user.getAddresses().add(address);
        userRepository.save(user);
        log.info("Address added successfully for user with ID: {}", user.getId());
        return entityMapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "'addresses_' + #root.target.getCurrentAuthenticatedUserEmail()")
    public AddressResponse updateUserAddress(Long id, AddressRequest updatedAddress) {
        User user = getCurrentAuthenticatedUser();
        log.info("Updating address with ID: {} for user with ID: {}", id, user.getId());
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
        log.info("Address updated successfully with ID: {} for user with ID: {}", id, user.getId());
        return entityMapper.toAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "'addresses_' + #root.target.getCurrentAuthenticatedUserEmail()")
    public void deleteUserAddress(Long id) {
        User user = getCurrentAuthenticatedUser();
        log.info("Deleting address with ID: {} for user with ID: {}", id, user.getId());
        Address address = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        user.getAddresses().remove(address);
        userRepository.save(user);
        addressRepository.delete(address);
        log.info("Address deleted successfully with ID: {} for user with ID: {}", id, user.getId());
    }

    public User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (userDetails == null) {
            log.error("Principal cannot be null");
            throw new IllegalStateException("Principal cannot be null");
        }
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("Fetching current authenticated user with ID: {}", user.getId());
        return user;
    }

    /**
     * Helper method to get the email of the currently authenticated user.
     * Used for cache key generation.
     * 
     * @return the email of the currently authenticated user
     */
    public String getCurrentAuthenticatedUserEmail() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (userDetails == null) {
            log.error("Principal cannot be null");
            throw new IllegalStateException("Principal cannot be null");
        }
        return userDetails.getUsername();
    }

}
