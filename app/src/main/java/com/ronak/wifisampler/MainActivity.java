package com.ronak.wifisampler;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    TextView tv_ssid;
    TextView tv_rssi;
    TextView tv_timestamp;
    GraphView gv_plot;

    WifiManager wifiManager;

    Timer timer;
    TimerTask timerTask;
    Runnable updateTextViews;

    LineGraphSeries<DataPoint> series;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_ssid = findViewById(R.id.tv_ssid);
        tv_rssi = findViewById(R.id.tv_rssi);
        tv_timestamp = findViewById(R.id.tv_timeStamp);
        gv_plot = findViewById(R.id.gv_plot);

        i = 0;
        series = new LineGraphSeries<>();
        gv_plot.setTitle("Wifi RSSi");
        gv_plot.getGridLabelRenderer().setHorizontalAxisTitle("Scans");
        gv_plot.getViewport().setScrollable(true);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        updateTextViews = new Runnable() {
            @Override
            public void run() {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = "SSID :  " + wifiInfo.getSSID();
                String rssi = "RSSi :  " + Integer.toString(wifiInfo.getRssi()) + " dBm";
                String time = "Time :  " + Long.toString(SystemClock.currentThreadTimeMillis());

                tv_ssid.setText(ssid);
                tv_rssi.setText(rssi);
                tv_timestamp.setText(time);

                series.appendData(new DataPoint(i++, wifiInfo.getRssi()), true, 100);
                series.setDrawDataPoints(true);
                gv_plot.addSeries(series);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(updateTextViews);
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }
}
