package com.example.android.ClimaSensScanner;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ClimaSensDevices {
    private ArrayList<ClimaSensDevice> mDevices;
    private Context mContext;

    public int getCount () {
        return mDevices.size();
    }


    ClimaSensDevices(Context context) {
        super();
        mDevices = new ArrayList<>();
        mContext = context;
        load();
    }

    public ClimaSensDevice getDevice (String deviceAddress) {
        if (getDevicePosition(deviceAddress) < 0) return null;
        return mDevices.get(getDevicePosition(deviceAddress));
    }

    public ClimaSensDevice getDevice (int position) {
        if (position < 0) return null;
        return mDevices.get(position);
    }

    public int getDevicePosition (String address) {
        int position = -1;
        for (int i = 0; i < mDevices.size(); i++) {
            if (mDevices.get(i).getAddress().equals(address)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void removeDevice (String deviceAddress) {
        int position = getDevicePosition(deviceAddress);
        if (position >= 0) mDevices.remove(position);
    }

    public void addDevice (String deviceName, String deviceAddress) {
        if (getDevicePosition(deviceAddress) < 0) mDevices.add(new ClimaSensDevice(deviceName, deviceAddress));
    }

    public void addDevice (String deviceName, String deviceAddress, byte[] rawData , long TimestampNanoseconds, int RSSI) {
        addDevice (deviceName, deviceAddress);
        addDeviceData (deviceAddress, rawData, TimestampNanoseconds, RSSI);
    }

    public void addDeviceData (String deviceAddress, byte[] rawData , long TimestampNanoseconds ,int RSSI) {
        if (getDevice(deviceAddress) == null) return;
        ClimaSensDevice tempDevice = getDevice(deviceAddress);
        tempDevice.addData(rawData, TimestampNanoseconds, RSSI);
        mDevices.set(getDevicePosition(deviceAddress),tempDevice);
    }

    public void clearDeviceData (String deviceAddress) {
        if (getDevice(deviceAddress) == null) return;
        ClimaSensDevice tempDevice = getDevice(deviceAddress);
        tempDevice.clearData();
        mDevices.set(getDevicePosition(deviceAddress),tempDevice);
    }

    public void setDeviceName (String deviceName, String deviceAddress) {
        if (getDevice(deviceAddress) == null) return;
        ClimaSensDevice tempDevice = getDevice(deviceAddress);
        tempDevice.setName(deviceName);
        mDevices.set(getDevicePosition(deviceAddress),tempDevice);
    }

    public void sortList () {
        Collections.sort(mDevices);
    }

    public void load() {
        SharedPreferences mySavedData = mContext.getSharedPreferences("mySavedData2", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mySavedData.getString("mClimaSensDevices",null);
        Type type = new TypeToken<ArrayList<ClimaSensDevice>>() {}.getType();

        mDevices = gson.fromJson(json, type);

        if (mDevices == null) {
            mDevices = new ArrayList<>();
        }
    }

    public void save() {
        SharedPreferences mySavedData = mContext.getSharedPreferences("mySavedData2", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySavedData.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mDevices);
        editor.clear();
        editor.putString("mClimaSensDevices",json);
        editor.apply();
    }
}
