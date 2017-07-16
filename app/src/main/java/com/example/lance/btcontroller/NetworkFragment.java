package com.example.lance.btcontroller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by Lance on 2017/5/23.
 */

public class NetworkFragment extends Fragment implements View.OnClickListener{

    /*NetworkFragment的控件*/
    private CheckBox checkBox;
    private EditText editServerIP;
    private EditText editServerPort;
    private Button butStartServer;
    private Button butStopServer;
    private EditText editLocalIP;
    private EditText editNetmask;
    private EditText editGateway;
    private Button butEditConf;
    private Button butSaveConf;

    private MainActivity mainActivity;
    private TCPServerService mTCPServerService;

    private static final String TAG = "NetworkFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View networkLayout = inflater.inflate(R.layout.network_fragment_layout, container, false);
        return networkLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        checkBox = (CheckBox) mainActivity.findViewById(R.id.network_checkbox);
        butEditConf = (Button) mainActivity.findViewById(R.id.network_button_edit);
        butSaveConf = (Button) mainActivity.findViewById(R.id.network_button_save);
        editServerIP = (EditText) mainActivity.findViewById(R.id.network_edit_server_ip);
        editServerPort = (EditText) mainActivity.findViewById(R.id.network_edit_server_port);
        butStartServer = (Button) mainActivity.findViewById(R.id.network_button_start_server);
        butStopServer = (Button) mainActivity.findViewById(R.id.network_button_stop_server);
        editLocalIP = (EditText) mainActivity.findViewById(R.id.network_edit_local_ip);
        editNetmask = (EditText) mainActivity.findViewById(R.id.network_edit_netmask);
        editGateway = (EditText) mainActivity.findViewById(R.id.network_edit_gateway);
        butStartServer.setOnClickListener(this);
        butStopServer.setOnClickListener(this);
        butEditConf.setOnClickListener(this);
        butSaveConf.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editServerIP.setText(getLocalIP());
                    editServerIP.setEnabled(false);
                    editServerPort.setEnabled(true);
                    butStartServer.setEnabled(true);
                }
                else{
                    butStartServer.setEnabled(false);
                    if(butEditConf.isEnabled())
                        editServerPort.setEnabled(false);
                    else
                        editServerIP.setEnabled(true);
                }
            }
        });

        initNetworkFragment();
    }

    private void initNetworkFragment(){
        SharedPreferences pre = mainActivity.getSharedPreferences("netinfo", 0);

        checkBox.setChecked(pre.getBoolean("checkState", false));
        String serverIP = pre.getString("serverIP", "");
        String serverPort = pre.getString("serverPort", "");
        String localIP = pre.getString("localIP", "");
        String netmask = pre.getString("netmask", "");
        String gateway = pre.getString("gateway", "");

        if(checkBox.isChecked()){
            editServerIP.setText(getLocalIP());
            if(mTCPServerService != null && mTCPServerService.getState()) {
                butStopServer.setEnabled(true);
                butStartServer.setEnabled(false);
            }
            else{
                butStartServer.setEnabled(true);
                butStopServer.setEnabled(false);
            }
        }
        else {
            butStartServer.setEnabled(false);
            butStopServer.setEnabled(false);
        }
        editServerIP.setText(serverIP);
        editServerPort.setText(serverPort);
        editLocalIP.setText(localIP);
        editNetmask.setText(netmask);
        editGateway.setText(gateway);
        editServerIP.setEnabled(false);
        if(!checkBox.isChecked())
            editServerPort.setEnabled(false);
        editLocalIP.setEnabled(false);
        editNetmask.setEnabled(false);
        editGateway.setEnabled(false);
        butSaveConf.setEnabled(false);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.network_button_start_server:
                butStartServerClicked();
                break;
            case R.id.network_button_stop_server:
                butStopServerClicked();
                break;
            case R.id.network_button_edit:
                butEditConfClicked();
                break;
            case R.id.network_button_save:
                butSaveConfClicked();
                break;
        }
    }

    private String getLocalIP(){
        String ipString = null;
        WifiManager wifiManager = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        ipString = String.format("%d.%d.%d.%d",ip & 0xff, (ip >> 8) & 0xff, (ip >> 16) & 0xff, (ip >> 24) & 0xff);
        return ipString;
    }

    private void butStartServerClicked(){
        final EditText editFileName = new EditText(mainActivity);
        new AlertDialog.Builder(mainActivity)
                .setTitle("请输入存储文件名")
                .setView(editFileName)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = editFileName.getText().toString();
                        if(fileName.equals(""))
                            mainActivity.displayToast("文件名不能为空");
                        else{
                            mTCPServerService = new TCPServerService(mainActivity, Integer.parseInt(editServerPort.getText().toString()), fileName, mainActivity.mHandler);
                            mTCPServerService.start();
                            butStartServer.setEnabled(false);
                            butStopServer.setEnabled(true);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void butStopServerClicked(){
        stopServer();
        butStopServer.setEnabled(false);
        butStartServer.setEnabled(true);
    }
    public void stopServer(){
        if(mTCPServerService != null) {
            mTCPServerService.stop();
            mTCPServerService = null;
        }
    }

    private void butEditConfClicked(){
        if(mainActivity.getBluetoothState() != BluetoothChatService.STATE_CONNECTED){
            mainActivity.displayToast("蓝牙未连接，无法更改配置");
            return;
        }
        butEditConf.setEnabled(false);
        butSaveConf.setEnabled(true);
        if(!checkBox.isChecked()) {
            editServerIP.setEnabled(true);
            editServerPort.setEnabled(true);
        }
        editLocalIP.setEnabled(true);
        editNetmask.setEnabled(true);
        editGateway.setEnabled(true);
    }

    private void butSaveConfClicked(){
        if(mainActivity.getBluetoothState() != BluetoothChatService.STATE_CONNECTED){
            mainActivity.displayToast("蓝牙未连接，无法保存配置");
            return;
        }
        final String serverIP = editServerIP.getText().toString();
        final String serverPort = editServerPort.getText().toString();
        String localIP = editLocalIP.getText().toString();
        String netmask = editNetmask.getText().toString();
        String gateway = editGateway.getText().toString();

        if(!checkIP(serverIP)) {
            mainActivity.displayToast("服务器IP地址无效");
            return;
        }
        if(!checkIP(localIP)){
            mainActivity.displayToast("仪器IP地址无效");
            return;
        }
        if(!checkIP(netmask)){
            mainActivity.displayToast("子网掩码无效");
            return;
        }
        if(!checkIP(gateway)){
            mainActivity.displayToast("默认网关无效");
            return;
        }

        editServerIP.setEnabled(false);//按钮及文本框失能
        if(!checkBox.isChecked())
            editServerPort.setEnabled(false);
        editLocalIP.setEnabled(false);
        editNetmask.setEnabled(false);
        editGateway.setEnabled(false);
        butSaveConf.setEnabled(false);
        butEditConf.setEnabled(true);

        if(!checkBox.isChecked() && serverIP.equals(getLocalIP())){
            mainActivity.displayToast("服务器IP与本机IP冲突");
            return;
        }
        mainActivity.sendCommand("netcfg/"+localIP+"/"+netmask+"/"+gateway+"\r\n");
        final ProgressDialog dia = new ProgressDialog(mainActivity);
        dia.setTitle("正在为仪器配置网络");
        dia.setMessage("请稍候...");
        dia.setCancelable(false);
        dia.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    if(!checkBox.isChecked()){
                        mainActivity.sendCommand("sercfg/"+serverIP+"/"+serverPort);
                        sleep(1000);
                    }
                }
                catch(InterruptedException e){
                    Log.e(TAG, "run: delay failed");
                }
                finally {
                    dia.dismiss();
                }
            }
        }.start();
        if(!checkBox.isChecked()) {
           // while (dia.isShowing()) ;

        }
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

    @Override
    public void onDetach() {
        super.onDetach();

        String serverIP = editServerIP.getText().toString();
        String serverPort = editServerPort.getText().toString();
        String localIP = editLocalIP.getText().toString();
        String netmask = editNetmask.getText().toString();
        String gateway = editGateway.getText().toString();

        SharedPreferences.Editor editor = mainActivity.getSharedPreferences("netinfo",0).edit();
        editor.putBoolean("checkState", checkBox.isChecked());
        editor.putString("serverIP", serverIP);
        editor.putString("serverPort", serverPort);
        editor.putString("localIP",localIP);
        editor.putString("netmask", netmask);
        editor.putString("gateway", gateway);
        editor.apply();
    }
}
