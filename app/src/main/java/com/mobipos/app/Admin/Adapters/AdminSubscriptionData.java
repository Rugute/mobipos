package com.mobipos.app.Admin.Adapters;

/**
 * Created by root on 1/8/18.
 */

class AdminSubscriptionData {
    public String name;
    public String valid;
    public String trans;
    public int image;

    public AdminSubscriptionData(String trans,String valid, String name, int image) {
        this.name = name;
        this.valid= valid;
        this.trans= trans;
        this.image = image;
    }
}

