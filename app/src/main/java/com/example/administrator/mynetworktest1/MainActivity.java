package com.example.administrator.mynetworktest1;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager cmgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cmgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);



    }

    private boolean isConnectNetwork(){
        NetworkInfo info =  cmgr.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnectedOrConnecting();
        return isConnected;
    }

    private boolean isConnectWifi(){
        NetworkInfo wifiInfo =  cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isConnected();
    }

    public void test1(View view){
        Log.i("brad", "Network:" + isConnectNetwork());
        Log.i("brad", "Wifi:" + isConnectWifi());
    }



}
