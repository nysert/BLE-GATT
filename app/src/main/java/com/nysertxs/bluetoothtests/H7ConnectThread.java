package com.nysertxs.bluetoothtests;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by Luis on 7/25/16.
 */
public class H7ConnectThread extends Thread {

    static BluetoothGattDescriptor descriptor;
    static BluetoothGattCharacteristic cc;
    static BluetoothGatt gatt;

    public static final String HRUUID = "0000180D-0000-1000-8000-00805F9B34FB";

    private BluetoothDevice device;
    private Context context;

    public H7ConnectThread(BluetoothDevice device, Context context) {
        this.device = device;
        this.context = context;
    }

    public void start() {
        Log.i("H7ConnectThread", "Starting H7 reader BTLE");
        gatt = device.connectGatt(context, false, btleGattCallback); // Connect to the device and store the server (gatt)
    }

    public void cancel() {
        gatt.setCharacteristicNotification(cc,false);
        descriptor.setValue( BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);

        gatt.disconnect();
        gatt.close();
        Log.i("H7ConnectThread", "Closing HRsensor");
    }

    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            byte[] data = characteristic.getValue();
            int bmp = data[1] & 0xFF; // To unsign the value

            Log.v("H7ConnectThread", "Data received from HR "+bmp);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState ==  BluetoothGatt.STATE_DISCONNECTED) {
                Log.e("H7ConnectThread", "device Disconnected");
            }
            else {
                gatt.discoverServices();
                Log.d("H7ConnectThread", "Connected and discovering services");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService service = gatt.getService(UUID.fromString(HRUUID)); // Return the HR service
            //BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB"));
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics(); //Get the hart rate value
            for (BluetoothGattCharacteristic cc : characteristics)
            {
                for (BluetoothGattDescriptor descriptor : cc.getDescriptors()) {
                    //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
                    // and then call setValue on that descriptor

                    //Those two line set the value for the disconnection
                    H7ConnectThread.descriptor = descriptor;
                    H7ConnectThread.cc = cc;

                    gatt.setCharacteristicNotification(cc,true);//Register to updates
                    descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                    Log.d("H7ConnectThread", "Connected and regisering to info");
                }
            }
        }
    };

}
