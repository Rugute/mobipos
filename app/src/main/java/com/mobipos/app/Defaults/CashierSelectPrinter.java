package com.mobipos.app.Defaults;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipos.app.Admin.AdminMeasurements;
import com.mobipos.app.Cashier.Adapters.PrinterAdapter;
import com.mobipos.app.Cashier.PackageConfig;
import com.mobipos.app.R;
import com.mobipos.app.database.PrinterInterface;
import com.mobipos.app.database.Printers;
import com.mobipos.app.database.Users;
import com.mobipos.app.database.defaults;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by folio on 3/1/2018.
 */

public class CashierSelectPrinter extends Fragment {

    Button btn_title;
    ListView listView;
    Printers printersdb;
    Users usersdb;



    public static CashierSelectPrinter newInstance(){
        CashierSelectPrinter fragment=new CashierSelectPrinter();
        return fragment;
    }
    List<PrinterInterface> printerData=null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_list_branches, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){
        printersdb=new Printers(getContext(), defaults.database_name,null,1);
        usersdb=new Users(getContext(), defaults.database_name,null,1);


        btn_title=view.findViewById(R.id.btn_title);
        listView=view.findViewById(R.id.view_outlet);

        btn_title.setText("SELECT PRINTER");


         printerData=printersdb.getPrintes();

        listView.setAdapter(new PrinterAdapter(getContext(),printersdb.getPrintes()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos= (int) adapterView.getItemIdAtPosition(i);

                AppConfig.printMac=printersdb.getPrintes().get(pos).macAdress;
                Toast.makeText(getContext(),printersdb.getPrintes().get(pos).macAdress,Toast.LENGTH_SHORT).show();

                try {
                    findBT();
                    openBT();
                    sendData();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

    }

    String header = "QTY     PRICE     AMOUNT\n";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    InputStream mmInputStream;
    OutputStream mmOutputStream;
    BluetoothSocket mmSocket;

    void findBT() {
        try {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.mBluetoothAdapter == null) {
               Toast.makeText(getActivity(),"Device not available",Toast.LENGTH_SHORT).show();
            }
            if (!this.mBluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
            }
            Set<BluetoothDevice> pairedDevices = this.mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("Thermal Printer")) {
                        this.mmDevice = device;
                        break;
                    }
                }
            }

            Toast.makeText(getActivity(),"Bluetooth device found.",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void openBT() throws IOException {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            this.mmDevice = this.mBluetoothAdapter.getRemoteDevice(AppConfig.printMac);
            this.mmSocket = this.mmDevice.createRfcommSocketToServiceRecord(uuid);
            this.mmSocket.connect();
            this.mmOutputStream = this.mmSocket.getOutputStream();
            this.mmInputStream = this.mmSocket.getInputStream();
            beginListenForData();
            Toast.makeText(getActivity(),"connection opened.",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    Thread workerThread;
    String  msg = "       \u0002"+usersdb.printer_header()[1]+" \n       \u0002Branch :\t\t" + usersdb.printer_header()[0] +
            "\t\n" + "       \u0002Order id:" + PackageConfig.order_no + "\n       \u0002" + new Date().toLocaleString() + "\n\n\n";
   public void beginListenForData() {


        try {
            final Handler handler = new Handler();
            this.stopWorker = false;
            this.readBufferPosition = 0;
            this.readBuffer = new byte[1024];
            this.workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == (byte) 10) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getActivity(),data,Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        byte[] bArr = readBuffer;
                                      //  PrintActivity printActivity = PrintActivity.this;
                                        int i2 = readBufferPosition;
                                        readBufferPosition = i2 + 1;
                                        bArr[i2] = b;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            stopWorker = true;
                        }
                    }
                }
            });
            this.workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void sendData() throws IOException {
        try {
            String order;
            String[] items = new String[]{"one\n", "two\n", "three\n"};
            String timeOf = String.valueOf(new Date().toLocaleString());
            StringBuilder sb = new StringBuilder();
            for (String str : AppConfig.formattedData) {
                sb.append(str + "\n");
            }
            String str2 = sb.toString();
            Log.e("items", str2);
            String habitnumber = "<b>Habit Number: </b><br>i am great for<br> who i am<br>thank you lord<br><br><br><br>";

            Amounts="testing amounts";
            msg="testing messages";

            mmOutputStream.write((msg + this.header + str2 + "\n" + Amounts).getBytes());
           Toast.makeText(getActivity(),"sending data...",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
    }

    void closeBT() throws IOException {
        try {
            this.stopWorker = true;
            this.mmOutputStream.close();
            this.mmInputStream.close();
            this.mmSocket.close();
            Toast.makeText(getActivity(),"connection closed",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   String Amounts = "Tendered Amount:     \u0002" + AppConfig.tendered_amount + ".00/=\n" +
           "Cash Sale:           \u0002" +AppConfig.cash_sale + ".00/=\n"+
            "Discount("+PackageConfig.DISCOUNT_VALUE+"%):          \u0002" + AppConfig.discount + ".00/=\n\n\n" +
            "Grand Total:          \u0002" + AppConfig.grand + ".00/=\n" +
            "Change:                \u0002" + AppConfig.change + ".00/=\n\n" +
           "Served by:\u0002" + usersdb.get_user_name() + "\n\nTHANK YOU\n\n\n\n\n";


}





















