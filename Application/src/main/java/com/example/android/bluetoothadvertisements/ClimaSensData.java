package com.example.android.bluetoothadvertisements;

import android.content.Context;
import android.os.SystemClock;
import java.text.SimpleDateFormat;

import java.util.concurrent.TimeUnit;


public class ClimaSensData {
    private byte[] mRawData;
    private long mTimestampNanoseconds;
    private int mRSSI;

    ClimaSensData(byte[] rawData , long TimestampNanoseconds, int RSSI) {
        super();
        mRawData = rawData;
        mTimestampNanoseconds = TimestampNanoseconds;
        mRSSI = RSSI;
    }

    private int convertToUnsiged (byte value) {
        return ((int)value & 0xFF);
    }

    public byte[] getRawData () {
        return mRawData;
    }

    public int getRSSI () {
        return mRSSI;
    }


    public double getBatteryValue () {
        return (double)((convertToUnsiged(mRawData[7]) << 8) | convertToUnsiged(mRawData[8])) / 1000;
    }

    public int getLuminanceValue () {
        return ((convertToUnsiged(mRawData[11]) << 8) | convertToUnsiged(mRawData[12]));
    }

    public double getTemperatureValue () {
        return (double)((convertToUnsiged(mRawData[13]) << 8) | convertToUnsiged(mRawData[14])) / 100;
    }

    public double getHumidityValue () {
        return (double)((convertToUnsiged(mRawData[15]) << 8) | convertToUnsiged(mRawData[16])) / 100;
    }

    public double getBarometricValue () {
        return (double)((convertToUnsiged(mRawData[17]) << 8) | convertToUnsiged(mRawData[18])) / 100;
    }

    public long getTimestampNanoseconds () {
        return mTimestampNanoseconds;
    }

    public String getTimeSinceString (Context context) {
        if (context == null) return "";

        //String lastSeenText = context.getResources().getString(R.string.last_seen) + "\n";
        String lastSeenText = "";

        long timeSince = SystemClock.elapsedRealtimeNanos() - mTimestampNanoseconds;
        long secondsSince = TimeUnit.SECONDS.convert(timeSince, TimeUnit.NANOSECONDS);

        if (secondsSince < 5) {
            lastSeenText += context.getResources().getString(R.string.just_now);
        } else if (secondsSince < 60) {
            lastSeenText += secondsSince + " " + context.getResources()
                    .getString(R.string.seconds_ago);
        } else {
            long minutesSince = TimeUnit.MINUTES.convert(secondsSince, TimeUnit.SECONDS);
            if (minutesSince < 60) {
                if (minutesSince == 1) {
                    lastSeenText += minutesSince + " " + context.getResources()
                            .getString(R.string.minute_ago);
                } else {
                    lastSeenText += minutesSince + " " + context.getResources()
                            .getString(R.string.minutes_ago);
                }
            } else {
                long hoursSince = TimeUnit.HOURS.convert(minutesSince, TimeUnit.MINUTES);
                if (hoursSince == 1) {
                    lastSeenText += hoursSince + " " + context.getResources()
                            .getString(R.string.hour_ago);
                } else {
                    lastSeenText += hoursSince + " " + context.getResources()
                            .getString(R.string.hours_ago);
                }
            }
        }

        return lastSeenText;
    }

    public long getTimeSinceSeconds (Context context) {
        if (context == null) return -1;

        long timeSince = SystemClock.elapsedRealtimeNanos() - mTimestampNanoseconds;
        long secondsSince = TimeUnit.SECONDS.convert(timeSince, TimeUnit.NANOSECONDS);

        return secondsSince;
    }

    public String getTimeString () {
        long bootMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime ();

        long timestampMillis = mTimestampNanoseconds / 1000000;

        long timestampTotalMillis = bootMillis + timestampMillis;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(timestampTotalMillis);
    }
}
