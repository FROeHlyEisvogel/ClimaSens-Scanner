package com.example.android.ClimaSensScanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Options extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        setTitle("Options");

        load();
    }

    @Override
    public void onBackPressed() {
        save();
        finish();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            save();
            this.finish();
            return true;
        }
        return false;
    }

    public void load() {
        Switch switchFilterButtons = (Switch) findViewById(R.id.switchFilterButtons);
        Switch switchSortDevices = (Switch) findViewById(R.id.switchSortDevices);

        SharedPreferences myOptions = getSharedPreferences("myOptions", Context.MODE_PRIVATE);

        switchFilterButtons.setChecked(myOptions.getBoolean("FilterButtons",true));
        switchSortDevices.setChecked(myOptions.getBoolean("SortDevices",true));
    }

    public void save() {
        Switch switchFilterButtons = (Switch) findViewById(R.id.switchFilterButtons);
        Switch switchSortDevices = (Switch) findViewById(R.id.switchSortDevices);

        SharedPreferences myOptions = getSharedPreferences("myOptions", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myOptions.edit();

        editor.clear();
        editor.putBoolean("FilterButtons",switchFilterButtons.isChecked());
        editor.putBoolean("SortDevices",switchSortDevices.isChecked());
        editor.apply();
    }
}
