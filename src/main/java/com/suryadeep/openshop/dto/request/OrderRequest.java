package com.suryadeep.openshop.dto.request;

import com.suryadeep.openshop.entity.enums.PaymentStatus;
import lombok.Data;

@Data
public class OrderRequest {
    private Long shippingAddressId;
    private String paymentMethod;
    private String orderNotes;
}
