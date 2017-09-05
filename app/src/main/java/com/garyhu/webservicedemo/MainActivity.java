package com.garyhu.webservicedemo;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTipsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTipsTextView = (TextView)findViewById(R.id.TipsTextView);
        mTipsTextView.setText("请在PC浏览器中输入:\n\n"+getLocalIpStr(this)+":"+HttpServerImpl.DEFAULT_SERVER_PORT);

        Intent intent = new Intent(this,HttpService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static String getLocalIpStr(Context context) {
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return intToIpAddr(wifiInfo.getIpAddress());
    }

    private static String intToIpAddr(int ip) {
        return (ip & 0xff) + "." + ((ip>>8)&0xff) + "." + ((ip>>16)&0xff) + "." + ((ip>>24)&0xff);
    }
}
