package com.example.administrator.mynetworktest1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager cmgr;
    private MyNetworkStateReceiver receiver;

    private WifiManager wmgr;

    private File root;

    private EditText account, passwd, realname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        }else{
            init();
        }




    }

    private void init(){
        cmgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        receiver = new MyNetworkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        root = Environment.getExternalStorageDirectory();

        account = (EditText)findViewById(R.id.account);
        passwd = (EditText)findViewById(R.id.passwd);
        realname = (EditText)findViewById(R.id.realname);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        }


    }

    @Override
    public void finish() {
        unregisterReceiver(receiver);
        super.finish();
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

    private class MyNetworkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("brad", "state:" + isConnectNetwork());
        }
    }


    public void test2(View view){
        try {
            File myfile = new File(root, "mydata.txt");
            FileOutputStream fout = new FileOutputStream(myfile);
            fout.write("Hello, World\nHello, Brad\n1234567\n7654321\n".getBytes());
            fout.flush();
            fout.close();
            Log.i("brad", "File save OK");
        }catch(Exception e){
            Log.i("brad", e.toString());
        }

    }

    public void test3(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    File upload = new File(root, "mydata.txt");
                    byte[] buf = new byte[(int)(upload.length())];
                    FileInputStream fin = new FileInputStream(upload);
                    fin.read(buf);
                    fin.close();

                    Socket socket = new Socket(InetAddress.getByName("10.0.2.2"), 7777);
                    OutputStream out = socket.getOutputStream();
                    out.write(buf);
                    out.flush();
                    out.close();
                    Log.i("brad", "Send OK");
                }catch(Exception ee) {
                    Log.i("brad", "Send ERR:" + ee.toString());
                }
            }
        }.start();
    }

    public void reg(View view){
        new Thread(){
            @Override
            public void run() {
                String inputAccount = account.getText().toString();
                String inputPasswd = passwd.getText().toString();
                String inputRealname = realname.getText().toString();

                String urlString = "http://10.0.1.8/brad03.php?account=" +
                        inputAccount + "&passwd=" + inputPasswd
                        + "&realname=" + inputRealname;

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream())) ;
                    String ret = reader.readLine();
                    Log.i("brad", ret);
                } catch (Exception e) {
                    Log.i("brad", e.toString());
                }


            }
        }.start();
    }

    // POST
    public void login(View view){
        new Thread(){
            @Override
            public void run() {
                String inputAccount = account.getText().toString();
                String inputPasswd = passwd.getText().toString();

                String urlString = "http://10.0.1.8/check.php";

                try {
                    MultipartUtility mu = new MultipartUtility(urlString, "UTF-8");
                    mu.addFormField("account", inputAccount, false);
                    mu.addFormField("passwd", inputPasswd, true);
                    List<String> ret = mu.finish();
                    Log.i("brad", ret.get(0));
                } catch (IOException e) {
                    Log.i("brad", e.toString());
                }


//                try {
//                    URL url = new URL(urlString);
//                    HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
//                    conn.setReadTimeout(3000);
//                    conn.setConnectTimeout(3000);
//                    conn.setRequestMethod("POST");
//                    conn.setDoInput(true);
//                    conn.setDoOutput(true);
//
//                    ContentValues data = new ContentValues();
//                    data.put("account", inputAccount);
//                    data.put("passwd", inputPasswd);
//                    String qs = queryString(data);
//
//                    OutputStream os = conn.getOutputStream();
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
//                    writer.write(qs);
//                    writer.flush();
//                    writer.close();
//
//                    conn.connect();
//
//                    int responseCode = conn.getResponseCode();
//                    String responseMesg = conn.getResponseMessage();
//
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(conn.getInputStream())) ;
//                    String ret = reader.readLine();
//                    Log.i("brad", responseCode + ":" + responseMesg + ":" + ret);
//
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


            }
        }.start();
    }

    private String queryString(ContentValues data){
        StringBuffer sb = new StringBuffer();
        Set<String> keys = data.keySet();
        try {
            for (String key : keys) {
                sb.append(URLEncoder.encode(key, "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(data.getAsString(key), "UTF-8"));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }catch(Exception ee){
            return null;
        }
    }

    public void upload(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility mu =
                            new MultipartUtility("http://10.0.1.8/doUpload.php","UTF-8");
                    File upload = new File(root, "mydata.txt");
                    mu.addFilePart("upload", upload);
                    List<String> ret = mu.finish();
                    Log.i("brad", ret.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }



}
