package com.example.android.ClimaSensScanner;

import android.content.Context;
import java.util.ArrayList;

public class ClimaSensDevice implements Comparable<ClimaSensDevice> {
    private String mDeviceName;
    private String mDeviceAddress;
    private ArrayList<ClimaSensData> mData;

    ClimaSensDevice(String deviceName, String deviceAddress) {
        super();
        mDeviceName = deviceName;
        mDeviceAddress = deviceAddress;
        mData = new ArrayList<>();
    }

    @Override
    public int compareTo(ClimaSensDevice device) {
        long compareSalary = device.getLatestSeen();

        // ascending order
        // return (int) (this.salary - compareSalary);

        // descending order
        return (int) (compareSalary - this.getLatestSeen());
    }

    public ArrayList<ClimaSensData> getArrayList() {
        return  mData;
    }

    private int convertToUnsiged (byte value) {
        return ((int)value & 0xFF);
    }

    public int getCount () {
        return mData.size() -1;
    }

    public void addData (byte[] rawData , long TimestampNanoseconds, int RSSI) {
        mData.add(0, new ClimaSensData(rawData, TimestampNanoseconds, RSSI));
    }

    public void clearData () {
        mData.clear();
    }

    public String getName () {
        return mDeviceName;
    }

    public void setName (String name) {
        mDeviceName = name;
    }

    public String getAddress () {
        return mDeviceAddress;
    }

    public void setAddress (String address) {
        mDeviceAddress = address;
    }

    public ClimaSensData getData () {
        if (getCount() < 0) return null;
        return mData.get(0);
    }

    public byte[] getRawData (int position) {
        if (position < 0) return null;
        return mData.get(position).getRawData();
    }

    public double getBatteryValue () {
        if (getCount() >= 0)  return mData.get(0).getBatteryValue();
        else return -1;
    }

    public int getLuminanceValue () {
        if (getCount() >= 0)  return mData.get(0).getLuminanceValue();
        else return -1;
    }

    public double getTemperatureValue () {
        if (getCount() >= 0)  return mData.get(0).getTemperatureValue();
        else return -1;
    }

    public double getHumidityValue () {
        if (getCount() >= 0)  return mData.get(0).getHumidityValue();
        else return -1;
    }

    public double getBarometricValue () {
        if (getCount() >= 0)  return mData.get(0).getBarometricValue();
        else return -1;
    }

    public double getRSSIValue () {
        if (getCount() >= 0)  return mData.get(0).getRSSI();
        else return -1;
    }

    public long getLatestSeen () {
        if (getCount() >= 0) return mData.get(0).getTimestampNanoseconds();
        else return -1;
    }

    public String getLatestSeen (Context context) {
        if (getCount() >= 0) return mData.get(0).getTimeSinceString(context);
        else return "";
    }
}