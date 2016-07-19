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
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> devices;

    Button findDevicesBtn;
    Button stopFindDevicesBtn;

    public static final int REQUEST_BLUETOOTH = 1;

    public static final UUID uuid = UUID.fromString("efa03c96-79c0-4751-b99c-b806699b55ff");

    boolean discover = true;

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

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        devices = new ArrayList<>();

//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                Device newDevice = new Device(device.getName(),device.getAddress(),"false");
//                devices.add(newDevice);
//                Log.e("paired device", newDevice.getDeviceName());
//            }
//        }

        findDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(receiver, filter);

                bluetoothAdapter.startDiscovery();
                if (bluetoothAdapter.isDiscovering()){
                    //Log.e("discovering", "true");
                }

//                ConnectThread connectThread = new ConnectThread();
//                if (devices.size() >= 1) {
//                    boolean connected = false;
//                    if (devices.get(1).getName() == "Nexus 6") {
//                        connected = connectThread.connect(devices.get(1), uuid);
//                    }
//                    if (devices.get(0).getName() == "Nexus 6") {
//                        connected = connectThread.connect(devices.get(0), uuid);
//                    }
//
//                    if (connected) {
//
//                    }
//                }
                ServerConnectThread serverConnectThread = new ServerConnectThread();
                serverConnectThread.acceptConnect(bluetoothAdapter, uuid);
            }
        });

        stopFindDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isDiscovering()) {
                    Log.e("discovering", "false");
                    bluetoothAdapter.cancelDiscovery();
                    unregisterReceiver(receiver);
                }
            }
        });

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action) && bluetoothAdapter.isDiscovering()) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);

                Log.e("device", device.getName());
            }
            Log.e("receiver", "intentReceiver finished");
        }
    };
}
