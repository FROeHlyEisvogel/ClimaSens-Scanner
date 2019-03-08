/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.ClimaSensScanner;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.FrameLayout;

/**
 * Holds and displays {@link ScanResult}s, used by {@link ScannerFragment}.
 */
public class ScanResultAdapter extends BaseAdapter {

    private ClimaSensDevices mDevicesClass;

    private Context mContext;

    private LayoutInflater mInflater;

    ScanResultAdapter(Context context, LayoutInflater inflater) {
        super();
        mContext = context;
        mInflater = inflater;
        mDevicesClass = new ClimaSensDevices (context);
    }

    @Override
    public int getCount() {
        return mDevicesClass.getCount();
    }

    @Override
    public Object getItem(int position) {
        return  mDevicesClass.getDevice(position);
    }

    @Override
    public long getItemId(int position) {
        return mDevicesClass.getDevice(position).getAddress().hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Reuse an old view if we can, otherwise create a new one.
        //if (view == null) {
            view = mInflater.inflate(R.layout.listitem_scanresult, null);
        //}

        if (mDevicesClass.getDevice(position) == null) return mInflater.inflate(R.layout.empty_view, null);

        FrameLayout frame = (FrameLayout) view.findViewById(R.id.frame);
        TextView deviceNameView = (TextView) view.findViewById(R.id.device_name);
        TextView deviceAddressView = (TextView) view.findViewById(R.id.device_address);
        TextView lastSeenView = (TextView) view.findViewById(R.id.last_seen);
        TextView luminance = (TextView) view.findViewById(R.id.luminance);
        TextView temperature = (TextView) view.findViewById(R.id.temperature);
        TextView humidity = (TextView) view.findViewById(R.id.humidity);
        TextView absolute_humidity = (TextView) view.findViewById(R.id.absolute_humidity);
        TextView barometric = (TextView) view.findViewById(R.id.barometric);
        TextView battery = (TextView) view.findViewById(R.id.battery);
        TextView rssi = (TextView) view.findViewById(R.id.rssi);

        if (frame != null) frame.setBackgroundColor(Color.parseColor("#ffffff"));

        if (deviceNameView != null) deviceNameView.setText(mDevicesClass.getDevice(position).getName());
        if (deviceAddressView != null) deviceAddressView.setText(mDevicesClass.getDevice(position).getAddress());

        if (mDevicesClass.getDevice(position).getCount() >= 0) {
            SharedPreferences myOptions = mContext.getSharedPreferences("myOptions", Context.MODE_PRIVATE);
            if (myOptions.getBoolean("FilterButtons",true) == true) {
                if (mDevicesClass.getDevice(position).getTemperatureValue() <= 0) return mInflater.inflate(R.layout.empty_view, null);
            }

            if (lastSeenView != null)
                lastSeenView.setText(mDevicesClass.getDevice(position).getData().getTimeSinceString(mContext));
            if (luminance != null)
                luminance.setText(String.valueOf(mDevicesClass.getDevice(position).getLuminanceValue()));
            if (temperature != null)
                temperature.setText(String.valueOf(mDevicesClass.getDevice(position).getTemperatureValue() + " °C"));
            if (humidity != null)
                humidity.setText(String.valueOf(mDevicesClass.getDevice(position).getHumidityValue() + " %"));
            if (absolute_humidity != null)
                absolute_humidity.setText(String.valueOf(mDevicesClass.getDevice(position).getHumidityValue() + " %"));
            if (barometric != null)
                barometric.setText(String.valueOf(mDevicesClass.getDevice(position).getBarometricValue() + " hPa"));
            if (battery != null)
                battery.setText(String.valueOf(mDevicesClass.getDevice(position).getBatteryValue() + " mV"));
            if (rssi != null)
                rssi.setText("RSSI: " + String.valueOf(mDevicesClass.getDevice(position).getRSSIValue() + " dBm"));

            if (mDevicesClass.getDevice(position).getData().getTimeSinceSeconds(mContext) > 1 * 60 * 60) {
                if (frame != null) frame.setBackgroundColor(Color.parseColor("#bfbfbf"));
            }
        }

        return view;
    }

    public void add(ScanResult scanResult) {
        String deviceName = scanResult.getScanRecord().getDeviceName();
        String deviceAddress = scanResult.getDevice().getAddress();
        long lastSeen = scanResult.getTimestampNanos();
        byte[] rawData = scanResult.getScanRecord().getBytes();
        int RSSI = scanResult.getRssi();

        mDevicesClass.addDevice(deviceName, deviceAddress, rawData, lastSeen, RSSI);

        SharedPreferences myOptions = mContext.getSharedPreferences("myOptions", Context.MODE_PRIVATE);
        if (myOptions.getBoolean("SortDevices",true) == true) mDevicesClass.sortList();

        mDevicesClass.save();
        notifyDataSetChanged();
    }

    public void reload() {
        mDevicesClass.load();
        notifyDataSetChanged();
    }
}