package com.nysertxs.bluetoothtests;

/**
 * Created by Luis on 7/15/16.
 */
public class Device {

    private String deviceName;
    private String address;
    private boolean connected;

    public Device (String name, String address, String connected){
        this.deviceName = name;
        this.address = address;
        if (connected == "true") {
            this.connected = true;
        }
        else {
            this.connected = false;
        }
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getAddress() {
        return address;
    }

    public boolean isConnected() {
        return connected;
    }
}
