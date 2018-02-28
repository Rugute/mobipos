package com.mobipos.app.database;

/**
 * Created by folio on 2/28/2018.
 */

public class DiscountInterface {

    public String id;
    public String name;
    public String value;

    public DiscountInterface(String id,String name,String value){
        this.name=name;
        this.id=id;
        this.value=value;
    }
}
