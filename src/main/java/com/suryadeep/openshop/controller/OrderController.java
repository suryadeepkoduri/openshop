package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.OrderRequest;
import com.suryadeep.openshop.dto.response.OrderResponse;
import com.suryadeep.openshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order with items from the user's cart"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully created",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Address not found or cart is empty"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("")
    public ResponseEntity<Object> createOrder(
        @Parameter(description = "Order details", required = true) 
        @RequestBody OrderRequest orderRequest){
        log.info("Creating new order for user with shipping address ID: {}", orderRequest.getShippingAddressId());
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @Operation(
        summary = "Get order by ID",
        description = "Returns a specific order by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved order",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to access this order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrder(
        @Parameter(description = "ID of the order to retrieve", required = true) 
        @PathVariable Long orderId){
        log.info("Fetching order with ID: {}", orderId);
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @Operation(
        summary = "Cancel order",
        description = "Cancels an existing order if it's in a cancellable state"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order successfully cancelled"),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled due to its current status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to cancel this order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Object> cancelOrder(
        @Parameter(description = "ID of the order to cancel", required = true) 
        @PathVariable Long orderId){
        log.info("Cancelling order with ID: {}", orderId);
        return new ResponseEntity<>(orderService.cancelOrder(orderId), HttpStatus.NO_CONTENT);
    }

    @Operation(
        summary = "Get user's orders",
        description = "Returns all orders placed by the current authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<Object> getUserOrders(){
        log.info("Fetching orders for the current authenticated user");
        return ResponseEntity.ok(orderService.getUserOrders());
    }

    @Operation(
        summary = "Download invoice",
        description = "Downloads the invoice for a specific order"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved invoice"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to access this order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<Object> downloadInvoice(
        @Parameter(description = "ID of the order to download invoice for", required = true) 
        @PathVariable Long orderId){
        log.info("Downloading invoice for order with ID: {}", orderId);
        return ResponseEntity.ok(orderService.downloadInvoice(orderId));
    }
}
