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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


/**
 * Scans for Bluetooth Low Energy Advertisements matching a filter and displays them to the user.
 */
public class ScannerFragment extends ListFragment {

    private static final String TAG = ScannerFragment.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeScanner mBluetoothLeScanner;

    private ScanCallback mScanCallback;

    private ScanResultAdapter mAdapter;

    /**
     * Must be called after object creation by MainActivity.
     *
     * @param btAdapter the local BluetoothAdapter
     */
    public void setBluetoothAdapter(BluetoothAdapter btAdapter) {
        this.mBluetoothAdapter = btAdapter;
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        // Use getActivity().getApplicationContext() instead of just getActivity() because this
        // object lives in a fragment and needs to be kept separate from the Activity lifecycle.
        //
        // We could get a LayoutInflater from the ApplicationContext but it messes with the
        // default theme, so generate it from getActivity() and pass it in separately.
        mAdapter = new ScanResultAdapter(getActivity().getApplicationContext(), LayoutInflater.from(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = super.onCreateView(inflater, container, savedInstanceState);

        setListAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        setEmptyText(getString(R.string.empty_list));

        // Trigger refresh on app's 1st load
        startScanning();

        handler.postDelayed(runnable, 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.reload();
        updateSpinner ();
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();

            handler.removeCallbacks(runnable);
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scanner_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                if (mScanCallback == null) {
                    startScanning();
                }
                else stopScanning();
                return true;
            case R.id.options:
                Intent intent = new Intent(getActivity(), Options.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Start scanning for BLE Advertisements.
     */
    public void startScanning() {
        if (mScanCallback != null) return;

        Log.d(TAG, "Starting Scanning");

        // Kick off a new scan.
        mScanCallback = new SampleScanCallback();
        mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);

        startSpinner ();

        Toast.makeText(getActivity(), R.string.start_scanning, Toast.LENGTH_SHORT).show();
    }

    /**
     * Stop scanning for BLE Advertisements.
     */
    public void stopScanning() {
        Log.d(TAG, "Stopping Scanning");

        // Stop the scan, wipe the callback.
        mBluetoothLeScanner.stopScan(mScanCallback);
        mScanCallback = null;

        // Even if no new results, update 'last seen' times.
        mAdapter.notifyDataSetChanged();

        stopSpinner ();

        Toast.makeText(getActivity(), R.string.stop_scanning, Toast.LENGTH_SHORT).show();
    }

    private void updateSpinner () {
        if (mScanCallback != null) startSpinner ();
        else stopSpinner ();
    }

    private void startSpinner () {
        View rootView = ((Activity)getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
        ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
    }

    private void stopSpinner () {
        View rootView = ((Activity)getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
        ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
    }

    /**
     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
     */
    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setDeviceName("ClimaSens");
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //builder.setMatchMode(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
        return builder.build();
    }

    /**
     * Custom ScanCallback object - adds to adapter on success, displays error on failure.
     */
    private class SampleScanCallback extends ScanCallback {

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

            for (ScanResult result : results) {
                mAdapter.add(result);
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            mAdapter.add(result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(getActivity(), "Scan failed with error: " + errorCode, Toast.LENGTH_LONG).show();
            stopSpinner ();
            mScanCallback = null;
        }
    }
}