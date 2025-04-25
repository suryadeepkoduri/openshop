package com.suryadeep.openshop.entity;

import com.suryadeep.openshop.entity.enums.OrderStatus;
import com.suryadeep.openshop.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_sequence")
    @SequenceGenerator(name = "order_sequence", sequenceName = "order_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String orderNumber;

    @Column(unique = true)
    private String paymentRefNo;

    @CreationTimestamp
    private LocalDateTime orderDate;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    private LocalDateTime estimatedDeliveryDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address shippingAddress;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalItemPrice;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingPrice;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;


    @Pattern(regexp = "[A-Z]{3}")
    @Column(length = 3)
    private String currencyCode = "INR";

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentMethod;

    private String orderNotes;

    private String trackingId;

    private String courierName;

    @Version
    private Long version;

    @Pattern(regexp = "^(?:[\\d]{1,3}\\.){3}[\\d]{1,3}$")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    private static String generateOrderId() {
        String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + time + "-" + random;
    }

    @PrePersist
    protected void onCreate() {
        orderNumber = generateOrderId();
        //TODO remove below func after payment adding
        paymentRefNo = "txn"+orderNumber;
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }

}
