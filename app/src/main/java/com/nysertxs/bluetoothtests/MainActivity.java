package com.nysertxs.bluetoothtests;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> devices;

    Button findDevicesBtn;
    Button stopFindDevicesBtn;

    IntentFilter filter;

    H7ConnectThread h7ConnectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findDevicesBtn = (Button)findViewById(R.id.findDevicesBtn);
        stopFindDevicesBtn = (Button)findViewById(R.id.stopFindDevicesBtn);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Turn on Bluetooth
        if (bluetoothAdapter == null)
            Toast.makeText(this, "Your device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
        else if (!bluetoothAdapter.isEnabled()) {
            Intent BtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(BtIntent, 0);
            Toast.makeText(this, "Turning on Bluetooth", Toast.LENGTH_LONG).show();
        }

        final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        devices = new ArrayList<>();

        final ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice device : pairedDevices) {
            bluetoothDevices.add(device);
        }
//        Log.e("paired 0", bluetoothDevices.get(0).getName());
        //h7ConnectThread = new H7ConnectThread(bluetoothDevices.get(0),getApplication().getApplicationContext());

        findDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //unpairDevice(bluetoothDevices.get(0));
//                Log.e("paired 0", bluetoothDevices.get(0).getName());

                if (devices.size() > 0) {
                    h7ConnectThread = new H7ConnectThread(devices.get(0),getApplication().getApplicationContext());
                    h7ConnectThread.start();
                    //pairDevice(devices.get(0));
                }
            }
        });

        stopFindDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForDevices();
                //h7ConnectThread.cancel();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e) {
            Log.e("pairDevice()", e.getMessage());
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Log.d("unpairDevice()", "Start Un-Pairing...");
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("unpairDevice()", "Un-Pairing finished.");
        } catch (Exception e) {
            Log.e("unpair", e.getMessage());
        }
    }

    private void searchForDevices() {
        devices.clear();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        bluetoothAdapter.startDiscovery();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
        public void onReceive(Context context, Intent intent) {
          String action = intent.getAction();
          if (BluetoothDevice.ACTION_FOUND.equals(action) && bluetoothAdapter.isDiscovering()) {

              try {
                  BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                  devices.add(device);

                  Log.e("device", device.getName());
                  Log.e("receiver", "intentReceiver finished");
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }
    };

}
