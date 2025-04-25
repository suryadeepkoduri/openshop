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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final EntityMapper entityMapper;

    @Override
    public UserResponse getCurrentUser() {
        User user = getCurrentAuthenticatedUser();
        logger.info("Fetching current user profile for user with ID: {}", user.getId());
        return entityMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(UserRegisterRequest userRequest) {
        User user = getCurrentAuthenticatedUser();
        logger.info("Updating current user profile for user with ID: {}", user.getId());
        user.setName(userRequest.getUsername());
        // Don't update email as it's a unique identifier
        // Don't update password here - should be handled by a separate password change service
        
        User updatedUser = userRepository.save(user);
        logger.info("User profile updated successfully for user with ID: {}", user.getId());
        return entityMapper.toUserResponse(updatedUser);
    }

    @Override
    public List<AddressResponse> getAddressess() {
        User user = getCurrentAuthenticatedUser();
        logger.info("Fetching addresses for user with ID: {}", user.getId());
        return entityMapper.toAddressResponseList(user.getAddresses());
    }

    @Override
    @Transactional
    public AddressResponse addAddress(AddressRequest addressRequest) {
        User user = getCurrentAuthenticatedUser();
        logger.info("Adding new address for user with ID: {}", user.getId());
        Address address = entityMapper.toAddressEntity(addressRequest);
        address = addressRepository.save(address);
        user.getAddresses().add(address);
        userRepository.save(user);
        logger.info("Address added successfully for user with ID: {}", user.getId());
        return entityMapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateUserAddress(Long id, AddressRequest updatedAddress) {
        User user = getCurrentAuthenticatedUser();
        logger.info("Updating address with ID: {} for user with ID: {}", id, user.getId());
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
        logger.info("Address updated successfully with ID: {} for user with ID: {}", id, user.getId());
        return entityMapper.toAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    public void deleteUserAddress(Long id) {
        User user = getCurrentAuthenticatedUser();
        logger.info("Deleting address with ID: {} for user with ID: {}", id, user.getId());
        Address address = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        user.getAddresses().remove(address);
        userRepository.save(user);
        addressRepository.delete(address);
        logger.info("Address deleted successfully with ID: {} for user with ID: {}", id, user.getId());
    }

    public User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (userDetails == null) {
            logger.error("Principal cannot be null");
            throw new IllegalStateException("Principal cannot be null");
        }
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        logger.info("Fetching current authenticated user with ID: {}", user.getId());
        return user;
    }
    
}
