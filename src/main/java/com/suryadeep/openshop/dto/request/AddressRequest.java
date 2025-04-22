package com.suryadeep.openshop.dto.request;

import lombok.Data;

@Data
public class AddressRequest {
    Long id;
    String addressLine;
    String city;
    String state;
    String country;
    String pincode;
}
