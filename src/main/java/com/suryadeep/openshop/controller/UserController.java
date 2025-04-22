package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.AddressRequest;
import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.dto.response.UserResponse;
import com.suryadeep.openshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@RequestBody UserRegisterRequest userRequest){
        return ResponseEntity.ok(userService.updateCurrentUser(userRequest));
    }

    @GetMapping("/me/addresses")
    public ResponseEntity<Object> getAddress() {
        return ResponseEntity.ok(userService.getAddressess());
    }

    @PostMapping("/me/addresses")
    public ResponseEntity<Object> addAddress(@RequestBody AddressRequest addressRequest) {
        return new ResponseEntity<>(userService.addAddress(addressRequest), HttpStatus.CREATED);
    }

    @PutMapping("/me/addresses/{id}")
    public ResponseEntity<Object> updateUserAddress(@PathVariable Long id,@RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(userService.updateUserAddress(id,addressRequest));
    }

    @DeleteMapping("/me/addresses/{id}")
    public ResponseEntity<Object> deleteUserAddress(@PathVariable Long id) {
        userService.deleteUserAddress(id);
        return new ResponseEntity<>("Address deleted successfully", HttpStatus.NO_CONTENT);
    }
}
