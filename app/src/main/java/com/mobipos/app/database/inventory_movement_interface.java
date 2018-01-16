package com.mobipos.app.database;

/**
 * Created by folio on 1/15/2018.
 */

public class inventory_movement_interface {

    public String id;
    public String product_id;
    public String type;
    public String saleId;
    public String date;
    public String count;


    public inventory_movement_interface(String id,String product_id,String type,String count,String saleId,String date){
        this.id=id;
        this.product_id=product_id;
        this.type=type;
        this.count=count;
        this.saleId=saleId;
        this.date=date;
    }
}
