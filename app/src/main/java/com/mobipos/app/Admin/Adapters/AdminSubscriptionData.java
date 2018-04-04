package com.mobipos.app.Admin.Adapters;

/**
 * Created by root on 1/8/18.
 */

public class AdminSubscriptionData {
    public String subscriptionId;
    public String name;
    public String valid;
    public String trans;
    public int image;

    public AdminSubscriptionData(String subscriptionId,String trans,String valid, String name) {
        this.subscriptionId=subscriptionId;
        this.name = name;
        this.valid= valid;
        this.trans= trans;

    }
}

