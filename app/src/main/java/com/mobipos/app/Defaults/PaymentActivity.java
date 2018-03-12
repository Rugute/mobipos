package com.mobipos.app.Defaults;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.mobipos.app.Cashier.dashboardFragments.MakeSales.viewCartData;
import com.mobipos.app.R;
import com.mobipos.app.Sync.SalesSync;
import com.mobipos.app.Sync.Synchronizer;
import com.mobipos.app.database.Order_Items;
import com.mobipos.app.database.Orders;
import com.mobipos.app.database.Sales;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentActivity extends AppCompatActivity {

    Sales salesdb;
    Users usersdb;

    private static final String TAG = PaymentActivity.class.getSimpleName();

    @BindView(R.id.receipt_order_no)
    TextView receipt_order_no;

    @BindView(R.id.discount_receipt)
    TextView txt_discount_name;

    @BindView(R.id.discount_amount)
    TextView txt_discount_amount;

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
        usersdb=new Users(this,defaults.database_name,null,1);
        initializeAdapter();
        reciept_order_date.setText(ordersdb.getOrderDate(PackageConfig.order_no));
        receipt_order_no.setText(PackageConfig.order_no);
        receipt_total.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)));


        AppConfig.discount_amnt=Integer.parseInt(PackageConfig.DISCOUNT_VALUE)*orderItemsdb.getCartTotal(PackageConfig.order_no)/100;

        txt_discount_name.setText("DISCOUNT: "+PackageConfig.DISCOUNT_VALUE+"%");
        txt_discount_amount.setText("-"+String.valueOf(AppConfig.discount_amnt));
        grand_total.setText(String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no)-AppConfig.discount_amnt));


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
                PackageConfig.order_no,inclusive,exclusive,grand_total,txt_discount_name,txt_discount_amount);
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

        int total=orderItemsdb.getCartTotal(PackageConfig.order_no)+PackageConfig.EXCLUSIVE_TAX;
        int disc=total*Integer.parseInt(PackageConfig.DISCOUNT_VALUE)/100;

        grand.setText(String.valueOf(total-disc));
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

                        AppConfig.tendered_amount=tender.getText().toString();
                        AppConfig.cash_sale=String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no));
                        AppConfig.change=change.getText().toString();
                        AppConfig.grand=grand.getText().toString();
                        AppConfig.discount=txt_discount_amount.getText().toString();


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
                        send_email_popup();

                    }
                    alertDialog.cancel();

                }else if(print.isChecked()){
                    if (internetSettings.isNetworkConnected()){
                        new Synchronizer(getApplicationContext());
                    }

                    List<viewCartData> data=new ArrayList<>();
                    data=orderItemsdb.getCartData(PackageConfig.order_no);
                    AppConfig.formattedData=new String[data.size()];

                    Log.d("data size for print",String.valueOf(data.size()));

                    for(int j=0;j<data.size();j++){
                        AppConfig.formattedData[j] = data.get(j).product_name + "\n" +
                                data.get(j).count + "     " + "     \u0002" +
                                data.get(j).price + ".00/=  " +
                                String.valueOf(Integer.parseInt(data.get(j).count)+Integer.parseInt( data.get(j).price)) + ".00/=";
                    }
                    AppConfig.print_biz_name=usersdb.printer_header()[1];
                    AppConfig.print_branch_name=usersdb.printer_header()[0];

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

                AppConfig.tendered_amount=String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no));
                AppConfig.cash_sale=String.valueOf(orderItemsdb.getCartTotal(PackageConfig.order_no));
                AppConfig.change="0";
                int total_amount=orderItemsdb.getCartTotal(PackageConfig.order_no)+PackageConfig.EXCLUSIVE_TAX;
                int disc=total_amount*Integer.parseInt(PackageConfig.DISCOUNT_VALUE)/100;

                AppConfig.grand=String.valueOf(total_amount-disc);


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

    public void onBackPressed(){
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);

        alertDialog.setMessage((CharSequence) "Are you sure you want terminate this transcation");
        alertDialog.setPositiveButton((CharSequence) "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(PaymentActivity.this,DashboardCashier.class));
            }
        });
        alertDialog.setNegativeButton((CharSequence) "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();

    }

    public static String ed_email;
    public void send_email_popup(){
        View view=LayoutInflater.from(this).inflate(R.layout.forgot_password,null);
        TextView title=view.findViewById(R.id.reset_password);
        final EditText email_address=view.findViewById(R.id.email_add);
        title.setText("ENTER EMAIL ADDRESS");

        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setView(view);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ed_email=email_address.getText().toString();
                new sendEmail().execute();

            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();

    }

    public class sendEmail extends AsyncTask<String,String,String>{

        String server_message;
        int success;

        ProgressDialog dialog=new ProgressDialog(PaymentActivity.this);
        protected void onPreExecute(){
            super.onPreExecute();

            dialog.setMessage("Emailing Receipt. Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            new Synchronizer(getApplicationContext());
        }
        @Override
        protected String doInBackground(String... strings) {
            JSONParser jsonParser=new JSONParser();
            List paramters=new ArrayList();
            paramters.add(new BasicNameValuePair("email",ed_email));
            paramters.add(new BasicNameValuePair("order_id",PackageConfig.order_no));

            JSONObject jsonObject=jsonParser.makeHttpRequest(AppConfig.protocol+AppConfig.hostname+PackageConfig.email_receipt,
                    "GET",paramters);
            try{
                success=jsonObject.getInt("success");
                server_message=jsonObject.getString("message");
            }catch (Exception e){

            }

            return null;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);

            if(success==1){
                startActivity(new Intent(PaymentActivity.this, DashboardCashier.class));
                Toast.makeText(getApplicationContext(),server_message,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),server_message,Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        }
    }

}
