package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.AddressRequest;
import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.dto.response.AddressResponse;
import com.suryadeep.openshop.dto.response.UserResponse;
import com.suryadeep.openshop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User profile and address management APIs")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Get current user profile",
        description = "Returns the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @Operation(
        summary = "Update user profile",
        description = "Updates the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile successfully updated",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
        @Parameter(description = "Updated user information", required = true) 
        @RequestBody UserRegisterRequest userRequest){
        return ResponseEntity.ok(userService.updateCurrentUser(userRequest));
    }

    @Operation(
        summary = "Get user addresses",
        description = "Returns all addresses associated with the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = AddressResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me/addresses")
    public ResponseEntity<Object> getAddress() {
        return ResponseEntity.ok(userService.getAddressess());
    }

    @Operation(
        summary = "Add new address",
        description = "Adds a new address for the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Address successfully created",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = AddressResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/me/addresses")
    public ResponseEntity<Object> addAddress(
        @Parameter(description = "Address details", required = true) 
        @RequestBody AddressRequest addressRequest) {
        return new ResponseEntity<>(userService.addAddress(addressRequest), HttpStatus.CREATED);
    }

    @Operation(
        summary = "Update address",
        description = "Updates an existing address for the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Address successfully updated",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = AddressResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this address"),
        @ApiResponse(responseCode = "404", description = "Address not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/me/addresses/{id}")
    public ResponseEntity<Object> updateUserAddress(
        @Parameter(description = "ID of the address to update", required = true) 
        @PathVariable Long id,
        @Parameter(description = "Updated address details", required = true) 
        @RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(userService.updateUserAddress(id,addressRequest));
    }

    @Operation(
        summary = "Delete address",
        description = "Deletes an existing address for the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Address successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this address"),
        @ApiResponse(responseCode = "404", description = "Address not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/me/addresses/{id}")
    public ResponseEntity<Object> deleteUserAddress(
        @Parameter(description = "ID of the address to delete", required = true) 
        @PathVariable Long id) {
        userService.deleteUserAddress(id);
        return new ResponseEntity<>("Address deleted successfully", HttpStatus.NO_CONTENT);
    }
}
