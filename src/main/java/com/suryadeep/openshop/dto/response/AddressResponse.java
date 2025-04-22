package com.suryadeep.openshop.dto.response;

import lombok.Data;

@Data
public class AddressResponse {
    Long id;
    String addressLine;
    String city;
    String state;
    String country;
    String pincode;
}
