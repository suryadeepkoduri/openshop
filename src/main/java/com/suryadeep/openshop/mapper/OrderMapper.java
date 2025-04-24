package com.suryadeep.openshop.mapper;

import com.suryadeep.openshop.dto.request.OrderRequest;
import com.suryadeep.openshop.dto.response.OrderResponse;
import com.suryadeep.openshop.entity.Order;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shippingAddress.id", source = "shippingAddressId")
    @Mapping(target = "orderNotes", source = "orderNotes")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    Order toEntity(OrderRequest orderRequest);

    @Mapping(target = "orderStatus", source = "status")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "totalShippingPrice", source = "shippingPrice")
    OrderResponse toResponse(Order order);

    @AfterMapping
    default void setEnumStrings(@MappingTarget OrderResponse response, Order order) {
        response.setOrderStatus(order.getStatus().name());
        response.setPaymentStatus(order.getPaymentStatus().name());
    }
}