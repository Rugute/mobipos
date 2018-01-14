package com.mobipos.app.Cashier.dashboardFragments.ViewSales;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobipos.app.Cashier.Adapters.ViewSalesAdapter;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.MakeSale;
import com.mobipos.app.R;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.defaults;

import butterknife.BindView;

/**
 * Created by folio on 1/13/2018.
 */

public class ViewSale extends Fragment {
    public static ViewSale newInstance(){
        ViewSale fragment = new ViewSale();
        return fragment;
    }

  //  @BindView(R.id.rv_view_sales)
    RecyclerView rv;

    Sales salesdb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.cashier_view_sales, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void onViewCreated(View view,Bundle savedInstanceState){

        salesdb =new Sales(getActivity(), defaults.database_name,null,1);
        rv=view.findViewById(R.id.rv_view_sales);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeAdapter();
    }

    public void initializeAdapter(){
        ViewSalesAdapter adapter=new ViewSalesAdapter(salesdb.getSalesData("loadLocal"));
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }
}
