package com.suryadeep.openshop.controller.admin;

import com.suryadeep.openshop.dto.response.OrderResponse;
import com.suryadeep.openshop.entity.enums.OrderStatus;
import com.suryadeep.openshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Admin - Orders", description = "Order management APIs for administrators")
public class AdminOrderController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderController.class);

    private final OrderService orderService;

    @Operation(
        summary = "Update order status",
        description = "Updates the status of an existing order (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status successfully updated",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Object> updateOrderStatus(
        @Parameter(description = "ID of the order to update", required = true) 
        @PathVariable Long orderId, 
        @Parameter(description = "New status for the order", required = true) 
        @RequestBody OrderStatus status) {
        logger.info("Updating order status for order ID: {}", orderId);
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @Operation(
        summary = "Verify payment",
        description = "Verifies the payment for a specific order (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment successfully verified"),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}/verify-payment/{paymentId}")
    public ResponseEntity<Object> verifyPayment(
        @Parameter(description = "ID of the order to verify payment for", required = true) 
        @PathVariable Long orderId,
        @Parameter(description = "Payment ID to verify", required = true) 
        @PathVariable String paymentId) {
        logger.info("Verifying payment for order ID: {} with payment ID: {}", orderId, paymentId);
        return ResponseEntity.ok("need to implement");
    }

    @Operation(
        summary = "Get all orders",
        description = "Returns a paginated list of all orders (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<Object> getOrders(
        @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching orders with page: {} and size: {}", page, size);
        return ResponseEntity.ok(orderService.getOrders(page, size));
    }

    @Operation(
        summary = "Get orders by status",
        description = "Returns a paginated list of orders filtered by status (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders by status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/status")
    public ResponseEntity<Object> getOrderByStatus(
        @Parameter(description = "Order status to filter by", required = true) 
        @RequestParam OrderStatus orderStatus,
        @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching orders with status: {}, page: {}, and size: {}", orderStatus, page, size);
        return ResponseEntity.ok(orderService.getOrdersByStatus(orderStatus, page, size));
    }
}
