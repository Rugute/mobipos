package com.mobipos.app.Defaults;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Cashier.Adapters.ViewCartAdapter;
import com.mobipos.app.Cashier.DashboardCashier;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.PushSaleData;
import com.mobipos.app.R;
import com.mobipos.app.Sync.SalesSync;
import com.mobipos.app.Sync.Synchronizer;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.defaults;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentActivity extends AppCompatActivity {

    Sales salesdb;
    private static final String TAG = PaymentActivity.class.getSimpleName();

    @BindView(R.id.receipt_order_no)
    TextView receipt_order_no;

    @BindView(R.id.reciept_order_date)
    TextView reciept_order_date;

    @BindView(R.id.reciept_recycler)
    RecyclerView rv;

    @BindView(R.id.receipt_total)
    TextView receipt_total;


    @BindView(R.id.button_bottom_sheet)
    Button btnBottomSheet;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    @BindView(R.id.btn_proceed_payment)
    Button btn_proceed_payment;

    @BindView(R.id.grand_total)
    TextView grand_total;

   // @BindView(R.id.txt_inclusive_data)
    TextView inclusive;
    //@BindView(R.id.txt_exclusive_tax)
    TextView exclusive;




    BottomSheetBehavior sheetBehavior;

    Order_Items orderItemsdb;
    Orders ordersdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);

        orderItemsdb=new Order_Items(this, defaults.database_name,null,1);
        ordersdb=new Orders(this, defaults.database_name,null,1);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        inclusive=findViewById(R.id.txt_inclusive_data);
        exclusive=findViewById(R.id.txt_exclusive_tax);

        salesdb=new Sales(this,defaults.database_name,null,1);
        initializeAdapter();
        reciept_order_date.setText(ordersdb.getOrderDate(PackageConfig.order_no));
        receipt_order_no.setText(PackageConfig.order_no);
        receipt_total.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));
        grand_total.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));

     // btnClick();

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        btn_proceed_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(salesdb.salesIdExists("order",PackageConfig.order_no)){
                    Toast.makeText(getApplicationContext(),"Order Already Paid !!",Toast.LENGTH_SHORT).show();
                }else{
                    selectPaymentPopup();
                    btn_proceed_payment.setEnabled(false);

                }

            }
        });

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }
    /**
     * manually opening / closing bottom sheet on button click
     */
    @OnClick(R.id.button_bottom_sheet)
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }

    private void initializeAdapter(){
        ViewCartAdapter adapter = new ViewCartAdapter(this,orderItemsdb.getCartData(PackageConfig.order_no),
                PackageConfig.order_no,inclusive,exclusive,grand_total);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

       // inclusive.setText(String.valueOf(PackageConfig.INCLUSIVE_TAX));
     //   exclusive.setText(String.valueOf(PackageConfig.EXCLUSIVE_TAX));
    }


    public void selectPaymentPopup(){

        View view= LayoutInflater.from(this).inflate(R.layout.cashier_select_payment,null);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.show();

        final RadioButton cash,mpesa,visa,mastercard;
        RadioGroup radioGroup=view.findViewById(R.id.payment_radio_group);

        cash=view.findViewById(R.id.radio_cash);
        mpesa=view.findViewById(R.id.radio_mpesa);
        visa=view.findViewById(R.id.radio_visa);
        mastercard=view.findViewById(R.id.radio_master);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(cash.isChecked()){
                   cashPaymentPopup();
                }else if (mpesa.isChecked()){
                    MpesaPaymentPopup();

                }

                alertDialog.dismiss();
            }
        });

    }

    public void cashPaymentPopup(){

        View view= LayoutInflater.from(this).inflate(R.layout.cashier_make_cash_payment,null);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setView(view);

        final EditText tender,grand,change;
        tender=view.findViewById(R.id.cash_tendered);
        grand=view.findViewById(R.id.amount_grand_total);
        change=view.findViewById(R.id.change_due);

        grand.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)+PackageConfig.EXCLUSIVE_TAX));
        grand.setEnabled(false);

        tender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(tender.getText())){
                    String amount=tender.getText().toString();
                    String grand_am=grand.getText().toString();
                    int change_is= Integer.parseInt(amount)-Integer.parseInt(grand_am);
                    change.setText(String.valueOf(change_is));
                    change.setEnabled(false);
                }

            }
        });
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!TextUtils.isEmpty(tender.getText())){
                    if(Integer.parseInt(change.getText().toString())>=0){
                        List<PushSaleData> data=new ArrayList<>();
                        data.add(new PushSaleData(PackageConfig.order_no,
                                tender.getText().toString(),
                                grand.getText().toString(),
                                "CASH",
                                "N/A"
                        ));

                        if(!salesdb.addSaleData(data)){
                            salesdb.getSalesData("loadLocal","NO NEED");
                            showPrinterPopUp();
                            alertDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"PAYMENT SUCCESSFUL",Toast.LENGTH_SHORT).show();
                        }else{

                            Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(getApplicationContext(),"AMOUNT TENDERED IS WRONG!!",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"PLEASE TENDER AMOUNT!!",Toast.LENGTH_SHORT).show();
                }


            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();


    }

    public void showPrinterPopUp(){
        View view= LayoutInflater.from(this).inflate(R.layout.cashier_print_reciept,null);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);



        final RadioButton print,mail,no_rec;
        RadioGroup radioGroup=view.findViewById(R.id.recipt_radio);

        print=view.findViewById(R.id.print_rec);
        mail=view.findViewById(R.id.mail_rec);
        no_rec=view.findViewById(R.id.no_rec);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                CheckInternetSettings internetSettings=new CheckInternetSettings(PaymentActivity.this);
                if(mail.isChecked()||no_rec.isChecked()){


                    if (internetSettings.isNetworkConnected()){
                        new Synchronizer(getApplicationContext());
                    }

                    startActivity(new Intent(PaymentActivity.this, DashboardCashier.class));
                }else if(print.isChecked()){
                    if (internetSettings.isNetworkConnected()){
                        new Synchronizer(getApplicationContext());
                    }

                    startActivity(new Intent(PaymentActivity.this, PrinterActivity.class));
                }
            }
        });

        alertDialog.show();
    }

    public void MpesaPaymentPopup(){

        View view= LayoutInflater.from(this).inflate(R.layout.cashier_make_mpesa_payment,null);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setView(view);

        final EditText tender;
        tender=view.findViewById(R.id.mpesa_tendered);
        String total=String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)+PackageConfig.EXCLUSIVE_TAX);

        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<PushSaleData> data=new ArrayList<>();
                String total=String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)+PackageConfig.EXCLUSIVE_TAX);
                data.add(new PushSaleData(PackageConfig.order_no,
                       total,
                       total,
                        "MPESA",
                        tender.getText().toString()
                ));

                if(!salesdb.addSaleData(data)){
                    salesdb.getSalesData("loadLocal","NO NEED");
                    showPrinterPopUp();
                    alertDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"PAYMENT SUCCESSFUL",Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
               }
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();


    }

}
