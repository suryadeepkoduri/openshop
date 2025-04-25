package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.AddressRequest;
import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.dto.response.AddressResponse;
import com.suryadeep.openshop.dto.response.UserResponse;
import com.suryadeep.openshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCurrentUser() {
        UserResponse userResponse = new UserResponse();
        when(userService.getCurrentUser()).thenReturn(userResponse);

        ResponseEntity<UserResponse> responseEntity = userController.getCurrentUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    void testUpdateCurrentUser() {
        UserRegisterRequest userRequest = new UserRegisterRequest();
        UserResponse userResponse = new UserResponse();
        when(userService.updateCurrentUser(any(UserRegisterRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> responseEntity = userController.updateCurrentUser(userRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    void testGetAddress() {
        List<AddressResponse> addressResponses = Collections.singletonList(new AddressResponse());
        when(userService.getAddressess()).thenReturn(addressResponses);

        ResponseEntity<Object> responseEntity = userController.getAddress();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(addressResponses, responseEntity.getBody());
    }

    @Test
    void testAddAddress() {
        AddressRequest addressRequest = new AddressRequest();
        AddressResponse addressResponse = new AddressResponse();
        when(userService.addAddress(any(AddressRequest.class))).thenReturn(addressResponse);

        ResponseEntity<Object> responseEntity = userController.addAddress(addressRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(addressResponse, responseEntity.getBody());
    }

    @Test
    void testUpdateUserAddress() {
        Long addressId = 1L;
        AddressRequest addressRequest = new AddressRequest();
        AddressResponse addressResponse = new AddressResponse();
        when(userService.updateUserAddress(anyLong(), any(AddressRequest.class))).thenReturn(addressResponse);

        ResponseEntity<Object> responseEntity = userController.updateUserAddress(addressId, addressRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(addressResponse, responseEntity.getBody());
    }

    @Test
    void testDeleteUserAddress() {
        Long addressId = 1L;

        doNothing().when(userService).deleteUserAddress(addressId);

        ResponseEntity<Object> responseEntity = userController.deleteUserAddress(addressId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals("Address deleted successfully", responseEntity.getBody());
    }
}
