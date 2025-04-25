package com.suryadeep.openshop.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String paymentRefNo;
    private LocalDateTime orderDate;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal totalItemPrice;
    private BigDecimal totalShippingPrice;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
    private AddressResponse shippingAddress;
    private String trackingId;
    private String courierName;
    private String orderNotes;


}
