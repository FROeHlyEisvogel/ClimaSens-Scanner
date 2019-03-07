package com.example.android.ClimaSensScanner;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;

public class DetailView extends FragmentActivity {

    private ClimaSensDevices mDevicesClass;
    private String deviceAddress;
    private int devicePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        setTitle("Details");

        deviceAddress = getIntent().getStringExtra("address");

        mDevicesClass = new ClimaSensDevices (getApplicationContext());
        devicePosition = mDevicesClass.getDevicePosition(deviceAddress);

        TextView device_name = (TextView) findViewById(R.id.device_name);
        EditText device_name_edit = (EditText) findViewById(R.id.device_name_edit);
        TextView device_address = (TextView) findViewById(R.id.device_address);
        TextView device_last_seen = (TextView) findViewById(R.id.device_last_seen);

        if (device_name != null) device_name.setText(mDevicesClass.getDevice(devicePosition).getName());
        if (device_name_edit != null) device_name_edit.setText(mDevicesClass.getDevice(devicePosition).getName());
        if (device_address != null) device_address.setText (mDevicesClass.getDevice(devicePosition).getAddress());
        if (device_last_seen != null) device_last_seen.setText(String.valueOf(mDevicesClass.getDevice(devicePosition).getLatestSeen(this)));

        // Setup the data source
        ArrayList<ClimaSensData> itemsArrayList = mDevicesClass.getDevice(devicePosition).getArrayList();

        // instantiate the custom list adapter
        detailViewAdapter adapter = new detailViewAdapter(this, itemsArrayList);

        // get the ListView and attach the adapter
        ListView itemsListView  = (ListView) findViewById(R.id.detail_listview);
        itemsListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        changeName();
        finish();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            changeName();
            this.finish();
            return true;
        }
        return false;
    }

    private void changeName () {
        EditText device_name_edit = (EditText) findViewById(R.id.device_name_edit);

        if (device_name_edit != null) {
            mDevicesClass.setDeviceName(device_name_edit.getText().toString(), deviceAddress);

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            mDevicesClass.save();
        }
    }

    public void onRemoveClick (View view) {
        mDevicesClass.removeDevice(deviceAddress);

        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
        mDevicesClass.save();

        this.finish();
        return;
    }

    public void onClearDetailList (View view) {
        mDevicesClass.clearDeviceData(deviceAddress);

        Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
        mDevicesClass.save();

        this.finish();
        return;
    }

    public class detailViewAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<ClimaSensData> mData;

        public detailViewAdapter(Context context, ArrayList<ClimaSensData> data) {
            super();
            mContext = context;
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_detailview, null);
            }

            TextView time = (TextView) view.findViewById(R.id.time);
            TextView luminance = (TextView) view.findViewById(R.id.luminance);
            TextView temperature = (TextView) view.findViewById(R.id.temperature);
            TextView humidity = (TextView) view.findViewById(R.id.humidity);
            TextView absolute_humidity = (TextView) view.findViewById(R.id.absolute_humidity);
            TextView barometric = (TextView) view.findViewById(R.id.barometric);
            TextView battery = (TextView) view.findViewById(R.id.battery);

            if (mDevicesClass.getDevice(devicePosition).getCount() >= 0) {
                if (time != null) time.setText(String.valueOf(mData.get(position).getTimeString()));
                if (luminance != null)
                    luminance.setText(String.valueOf(mData.get(position).getLuminanceValue()));
                if (temperature != null)
                    temperature.setText(String.valueOf(mData.get(position).getTemperatureValue()) + " °C");
                if (humidity != null)
                    humidity.setText(String.valueOf(mData.get(position).getHumidityValue()) + " %");
                if (absolute_humidity != null)
                    absolute_humidity.setText(String.valueOf(mData.get(position).getHumidityValue()) + " %");
                if (barometric != null)
                    barometric.setText(String.valueOf(mData.get(position).getBarometricValue()) + " hPa");
                if (battery != null)
                    battery.setText(String.valueOf(mData.get(position).getBatteryValue()) + " mV");
            }

            return view;
        }
    }
}
