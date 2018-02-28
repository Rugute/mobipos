package com.mobipos.app.database;

/**
 * Created by folio on 2/28/2018.
 */

public class PrinterInterface {

    public String id;
    public String name;
    public String macAdress;

    public PrinterInterface(String id,String name,String macAdress){
        this.name=name;
        this.id=id;
        this.macAdress=macAdress;
    }
}
