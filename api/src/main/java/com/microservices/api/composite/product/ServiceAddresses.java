package com.microservices.api.composite.product;

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

    public ServiceAddresses(final String compositeAddress, final String productAddress, final String reviewAddress, final String recommendationAddress) {
        cmp = compositeAddress;
        pro = productAddress;
        rev = reviewAddress;
        rec = recommendationAddress;
    }

    public String getCmp() {
        return cmp;
    }

    public String getPro() {
        return pro;
    }

    public String getRev() {
        return rev;
    }

    public String getRec() {
        return rec;
    }
}