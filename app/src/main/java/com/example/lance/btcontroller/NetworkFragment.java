package com.example.lance.btcontroller;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

    public static final int UPDATE_VIEW = 1;
    /*NetworkFragment的控件*/
    private CheckBox checkBox;
    private EditText editServerIP;
    private EditText editServerPort;
    //private Button butStartServer;
    private EditText editLocalIP;
    private EditText editNetmask;
    private EditText editGateway;
    private Button butEditConf;
    private Button butGetData;
    private boolean isRecieving;


    private MainActivity mainActivity;
    //private TCPServerService mTCPServerService;
    private DownloadService.DownloadBinder downloadBinder;
    private String fileName;
    private String fileSize;

    public static int RECIEVE_STATE = Constants.RECIEVE_STATE_NONE;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: "+"service connected" );
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    //子线程中更新获取数据按钮的显示
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_VIEW:
                    toggleButtonText(R.id.network_button_get_data);
                    break;
            }
        }
    };

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
        butGetData = (Button) mainActivity.findViewById(R.id.network_button_get_data);
        editServerIP = (EditText) mainActivity.findViewById(R.id.network_edit_server_ip);
        editServerPort = (EditText) mainActivity.findViewById(R.id.network_edit_server_port);
       // butStartServer = (Button) mainActivity.findViewById(R.id.network_button_start_server);
        editLocalIP = (EditText) mainActivity.findViewById(R.id.network_edit_local_ip);
        editNetmask = (EditText) mainActivity.findViewById(R.id.network_edit_netmask);
        editGateway = (EditText) mainActivity.findViewById(R.id.network_edit_gateway);
     //   butStartServer.setOnClickListener(this);
        butEditConf.setOnClickListener(this);
        butGetData.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editServerIP.setText(getLocalIP());
                    editServerIP.setEnabled(false);
                    editServerPort.setEnabled(true);
                  //  if (mainActivity.getBluetoothState() == BluetoothChatService.STATE_CONNECTED)
                    //    butStartServer.setEnabled(true);
                }
                else{
                   // butStartServer.setEnabled(false);
                    if(butEditConf.isEnabled())
                        editServerPort.setEnabled(false);
                    else
                        editServerIP.setEnabled(true);
                    if (editLocalIP.isEnabled()){
                        editServerIP.setEnabled(true);
                        editServerPort.setEnabled(true);
                    }
                }
            }
        });

        initNetworkFragment();
    }

    private void initNetworkFragment(){
        disableAllView();

        SharedPreferences pre = mainActivity.getSharedPreferences("netinfo", 0);

        checkBox.setChecked(pre.getBoolean("checkState", false));
        String serverIP = pre.getString("serverIP", "");
        String serverPort = pre.getString("serverPort", "");
        String localIP = pre.getString("localIP", "");
        String netmask = pre.getString("netmask", "");
        String gateway = pre.getString("gateway", "");
        String getDataText = pre.getString("getDataText", "");
        editServerIP.setText(serverIP);
        editServerPort.setText(serverPort);
        editLocalIP.setText(localIP);
        editNetmask.setText(netmask);
        editGateway.setText(gateway);
        if (getRecieveState())
            butGetData.setText(R.string.button_stop_get_data);
        else
            butGetData.setText(R.string.button_start_get_data);

        if (mainActivity.getBluetoothState() == BluetoothChatService.STATE_CONNECTED){
            checkBox.setEnabled(true);
            //butStartServer.setEnabled(true);
            butGetData.setEnabled(true);
            butEditConf.setEnabled(true);
        }
       // if (getServiceState())
       //     butStartServer.setEnabled(true);

    }

    public boolean getRecieveState(){
        return isRecieving;
    }
    public boolean getCheckBoxState(){
        return checkBox.isChecked();
    }
    public boolean getServiceState() { return  downloadBinder != null; }
    public void setRecieveState(boolean flag){
        isRecieving = flag;
    }
    public void setFileSize(String size){
        fileSize = size;
    }
    public void disableAllView(){
        //butStartServer.setText(R.string.button_start_server);
        butEditConf.setText(R.string.button_edit_tcp);
        checkBox.setEnabled(false);
        editServerIP.setEnabled(false);
        editServerPort.setEnabled(false);
        //butStartServer.setEnabled(false);
        editLocalIP.setEnabled(false);
        editNetmask.setEnabled(false);
        editGateway.setEnabled(false);
        butEditConf.setEnabled(false);
        butGetData.setEnabled(false);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
           /* case R.id.network_button_start_server:
                if (butStartServer.getText().toString().
                        equals(mainActivity.getString(R.string.button_start_server)))
                    butStartServerClicked();
                else
                    butStopServerClicked();
                break;*/
            case R.id.network_button_edit:
                if (butEditConf.getText().toString().
                        equals(mainActivity.getString(R.string.button_edit_tcp)))
                    butEditConfClicked();
                else
                    butSaveConfClicked();
                toggleButtonText(v.getId());
                break;
            case R.id.network_button_get_data:
                butGetDataClicked();
        }
    }

    private String getLocalIP(){
        String ipString;
        WifiManager wifiManager = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        ipString = String.format("%d.%d.%d.%d",ip & 0xff, (ip >> 8) & 0xff, (ip >> 16) & 0xff, (ip >> 24) & 0xff);
        return ipString;
    }
/*
    private void butStartServerClicked(){
        final EditText editFileName = new EditText(mainActivity);
        new AlertDialog.Builder(mainActivity)
                .setTitle("请输入存储文件名")
                .setView(editFileName)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileName = editFileName.getText().toString();
                        if(fileName.equals(""))
                            mainActivity.displayToast("文件名不能为空");
                        else{
                            mainActivity.sendCommand("server/"+editServerIP.getText().toString()+"/"
                                +editServerPort.getText().toString());
                            if (downloadBinder == null) {
                                Intent intent = new Intent(getActivity(), DownloadService.class);
                                mainActivity.startService(intent);
                                mainActivity.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                            }
                            toggleButtonText(R.id.network_button_start_server);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void butStopServerClicked(){
        stopServer();
        toggleButtonText(R.id.network_button_start_server);
    }
    */
    private void showFilenameDialog(){
        final EditText editFileName = new EditText(mainActivity);
        new AlertDialog.Builder(mainActivity)
                .setTitle("请输入存储文件名")
                .setView(editFileName)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileName = editFileName.getText().toString();
                        if(fileName.equals(""))
                            mainActivity.displayToast("文件名不能为空");
                        else{
                            new Thread(){
                                @Override
                                public void run() {
                                    try{
                                        sleep(100);
                                        mainActivity.sendCommand("server/"+editServerIP.getText()
                                                .toString()+"/" +editServerPort.getText().toString()+"\r\n");
                                        sleep(100);
                                        if (downloadBinder == null) {
                                            Intent intent = new Intent(getActivity(), DownloadService.class);
                                            mainActivity.startService(intent);
                                            mainActivity.getApplicationContext()
                                                    .bindService(intent, connection, Context.BIND_AUTO_CREATE);
                                            sleep(100);
                                        }
                                        Log.e(TAG, "run: filesize: "+fileSize );
                                        downloadBinder.startDownload(fileName, fileSize);
                                        sleep(100);
                                        mainActivity.sendCommand(Constants.CMD_SEND_DATA);
                                        setRecieveState(true);
                                        Message msg = new Message();
                                        msg.what = UPDATE_VIEW;
                                        handler.sendMessage(msg);
                                        new Thread(){
                                            @Override
                                            public void run() {
                                                    while (RECIEVE_STATE == Constants.RECIEVE_STATE_NONE);
                                                    if (RECIEVE_STATE != Constants.RECIEVE_STATE_CANCELED) {
                                                        Message msg = new Message();
                                                        msg.what = UPDATE_VIEW;
                                                        handler.sendMessage(msg);
                                                    }
                                                }
                                        }.start();
                                    }
                                    catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }
    public void stopServer(){
        if(downloadBinder != null && getRecieveState())
                downloadBinder.cancelDownload();
    }

    private void butEditConfClicked(){
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
        butEditConf.setEnabled(true);

        if(!checkBox.isChecked() && serverIP.equals(getLocalIP())){
            mainActivity.displayToast("服务器IP与本机IP冲突");
            return;
        }
        String cmd = "netcfg/"+localIP+"/"+netmask+"/"+gateway;
        if (!checkBox.isChecked())
            cmd += "/" + editServerIP.getText().toString() + "/" +editServerPort.getText().toString();
        mainActivity.sendCommand(cmd+"\r\n");
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

    private void butGetDataClicked(){
        if (butGetData.getText().toString().equals(mainActivity.getString(R.string.button_start_get_data))) {
            if (checkBox.isChecked()) {
                mainActivity.sendCommand(Constants.CMD_REQ_FILE_SIZE);
                showFilenameDialog();
            }
            else {
                mainActivity.sendCommand(Constants.CMD_SEND_DATA);
                setRecieveState(true);
                toggleButtonText(R.id.network_button_get_data);
            }
        }
        else{
            if (checkBox.isChecked())
                downloadBinder.cancelDownload();
            else
                mainActivity.sendCommand(Constants.CMD_STOP);
            toggleButtonText(R.id.network_button_get_data);
            if (mainActivity.getBluetoothState() != BluetoothChatService.STATE_CONNECTED)
                butGetData.setEnabled(false);
            setRecieveState(false);
        }
    }

    private boolean checkIP(String ip) {
        String[] ipSplit = ip.split("\\.");//单纯使用"."将得不到正确结果 因为传入的参数不是简单的字符串而是匹配模式 某些符号有特殊含义 需要使用转义符号
        if (ipSplit.length != 4)
            return false;
        for(byte i = 0; i<4; i++){
            int ipNum = Integer.parseInt(ipSplit[i]);
            if(ipNum>255)
                return false;
        }
        return true;
    }

    //提供公共方法供mainactivity在蓝牙连接状态改变时改变按钮状态
    public void setViewEnabled(int id, boolean flag){
         mainActivity.findViewById(id).setEnabled(flag);
    }
    public void toggleButtonText(int id){
        switch (id){
            case R.id.network_button_get_data:
                if (butGetData.getText().toString()
                        .equals(mainActivity.getString(R.string.button_start_get_data)))
                    butGetData.setText(mainActivity.getString(R.string.button_stop_get_data));
                else
                    butGetData.setText(R.string.button_start_get_data);
                break;
            /*
            case R.id.network_button_start_server:
                if (butStartServer.getText().toString()
                        .equals(mainActivity.getString(R.string.button_start_server)))
                    butStartServer.setText(R.string.button_stop_server);
                else
                    butStartServer.setText(R.string.button_start_server);
                break;
                */
            case R.id.network_button_edit:
                if (butEditConf.getText().toString()
                        .equals(mainActivity.getString(R.string.button_edit_tcp)))
                    butEditConf.setText(R.string.button_save_tcp);
                else
                    butEditConf.setText(R.string.button_edit_tcp);
                break;
        }
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
        editor.putString("getDataText", butGetData.getText().toString());
        editor.apply();
    }
}
