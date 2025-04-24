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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Admin - Orders", description = "Order management APIs for administrators")
public class AdminOrderController {

    private final OrderService orderService;

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId,status));
    }

    public ResponseEntity<Object> verifyPayment(@PathVariable Long orderId,@PathVariable String paymentId) {
        return ResponseEntity.ok("need to implement");
    }

    @GetMapping("")
    public ResponseEntity<Object> getOrders(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(orderService.getOrders(page,size));
    }

    @GetMapping("/status")
    public ResponseEntity<Object> getOrderByStatus(@RequestParam OrderStatus orderStatus,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(orderService.getOrdersByStatus(orderStatus,page,size));
    }
}
