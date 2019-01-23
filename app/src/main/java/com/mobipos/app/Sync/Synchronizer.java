package com.mobipos.app.Sync;

import android.content.Context;

/**
 * Created by folio on 1/15/2018.
 */

public class Synchronizer {
    Context context;
    public Synchronizer(Context context){

        this.context=context;
        new OrdersSync(context);
      //  new SalesSync(context);
        new InventoryMovementSync(context);
    }
}
