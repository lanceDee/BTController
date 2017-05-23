package com.example.lance.btcontroller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    public static final String SERVER_ADDRESS = "server_address";
    private String serverIP;
    private String serverPort;
    private String localIP;
    private String netmask;
    private String gateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final EditText editServerIP = (EditText) findViewById(R.id.edit_server_ip);
        final EditText editServerPort = (EditText) findViewById(R.id.edit_server_port);
        final EditText editLocalIP = (EditText) findViewById(R.id.edit_local_ip);
        final EditText editNetmask = (EditText) findViewById(R.id.edit_netmask);
        final EditText editGateway = (EditText) findViewById(R.id.edit_gateway);
        final Button butEdit = (Button) findViewById(R.id.button_edit_setting);
        final Button butSave = (Button) findViewById(R.id.button_save_setting);
       // final Button butSync = (Button) findViewById(R.id.button_sync_setting);

        SharedPreferences pre = getSharedPreferences("netinfo", 0);

        serverIP = pre.getString("serverIP", "");
        Log.e(TAG, "onStart: serverIP from sharedpre: "+serverIP);
        serverPort = pre.getString("serverPort", "");
        localIP = pre.getString("localIP", "");
        netmask = pre.getString("netmask", "");
        gateway = pre.getString("gateway", "");

        editServerIP.setText(serverIP);
        editServerPort.setText(serverPort);
        editLocalIP.setText(localIP);
        editNetmask.setText(netmask);
        editGateway.setText(gateway);
        editServerIP.setEnabled(false);
        editServerPort.setEnabled(false);
        editLocalIP.setEnabled(false);
        editNetmask.setEnabled(false);
        editGateway.setEnabled(false);
        butSave.setEnabled(false);

        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                serverIP = editServerIP.getText().toString();
                serverPort = editServerPort.getText().toString();
                localIP = editLocalIP.getText().toString();
                netmask = editNetmask.getText().toString();
                gateway = editGateway.getText().toString();

                if(!checkIP(serverIP)) {
                    errorShow("服务器IP地址无效");
                    return;
                }
                if(!checkIP(localIP)){
                    errorShow("仪器IP地址无效");
                    return;
                }
                if(!checkIP(netmask)){
                    errorShow("子网掩码无效");
                    return;
                }
                if(!checkIP(gateway)){
                    errorShow("默认网关无效");
                    return;
                }

                editServerIP.setEnabled(false);//按钮及文本框失能
                editServerPort.setEnabled(false);
                editLocalIP.setEnabled(false);
                editNetmask.setEnabled(false);
                editGateway.setEnabled(false);
                butSave.setEnabled(false);
                butEdit.setEnabled(true);

                SharedPreferences.Editor editor = getSharedPreferences("netinfo",0).edit();
                editor.putString("serverIP", serverIP);
                editor.putString("serverPort", serverPort);
                editor.putString("localIP",localIP);
                editor.putString("netmask", netmask);
                editor.putString("gateway", gateway);
                editor.apply();

                Intent intent = new Intent();
                String serverAddress = serverIP+":"+serverPort;
                intent.putExtra(SERVER_ADDRESS, serverAddress);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        butEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                butEdit.setEnabled(false);
                butSave.setEnabled(true);
                editServerIP.setEnabled(true);
                editServerPort.setEnabled(true);
                editLocalIP.setEnabled(true);
                editNetmask.setEnabled(true);
                editGateway.setEnabled(true);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ((EditText) findViewById(R.id.edit_server_ip)).setText(serverIP);
        ((EditText) findViewById(R.id.edit_server_port)).setText(serverPort);
        ((Button) findViewById(R.id.button_edit_setting)).setEnabled(true);
    }

    private void errorShow(String string){
        Toast.makeText(SettingActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    private boolean checkIP(String ip) {
        String[] ipSplit = ip.split("\\.");//单纯使用"."将得不到正确结果 因为传入的参数不是简单的字符串而是匹配模式 某些符号有特殊含义 需要使用转义符号
        if (ipSplit.length != 4)
            return false;
        for(byte i = 0; i<4; i++){
            int ipNum = Integer.parseInt(ipSplit[i]);
           // Log.e(TAG, "checkIP: ipNum ="+ipNum);
            if(ipNum>255)
                return false;
        }
        return true;
    }

}
