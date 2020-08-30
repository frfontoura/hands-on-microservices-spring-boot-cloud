package com.microservices.api.composite.product;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ServiceAddresses {
    private final String cmp;
    private final String pro;
    private final String rev;
    private final String rec;

    public ServiceAddresses() {
        cmp = null;
        pro = null;
        rev = null;
        rec = null;
    }

}
