package com.mobipos.app.database;

/**
 * Created by folio on 1/13/2018.
 */

public class productInterface {

    public int product_id;
    public int count;

    public productInterface(int product_id,int count){
        this.product_id=product_id;
        this.count=count;
    }
}
