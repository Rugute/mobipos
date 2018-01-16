package com.mobipos.app.database;

/**
 * Created by folio on 1/15/2018.
 */

public class orders_interface {
    public String order_id;
    public String date;

    public orders_interface(String order_id,String date){
        this.order_id=order_id;
        this.date=date;
    }
}

