package com.example.lance.btcontroller;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Lance on 2017/5/23.
 */

public class NetworkFragment extends Fragment implements View.OnClickListener{

    /*NetworkFragment的控件*/
    private EditText editServerIP;
    private EditText editServerPort;
    private EditText editLocalIP;
    private EditText editNetmask;
    private EditText editGateway;
    private Button butEditConf;
    private Button butSaveConf;

    private MainActivity mainActivity;

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

        butEditConf = (Button) mainActivity.findViewById(R.id.network_button_edit);
        butSaveConf = (Button) mainActivity.findViewById(R.id.network_button_save);
        editServerIP = (EditText) mainActivity.findViewById(R.id.network_edit_server_ip);
        editServerPort = (EditText) mainActivity.findViewById(R.id.network_edit_server_port);
        editLocalIP = (EditText) mainActivity.findViewById(R.id.network_edit_local_ip);
        editNetmask = (EditText) mainActivity.findViewById(R.id.network_edit_netmask);
        editGateway = (EditText) mainActivity.findViewById(R.id.network_edit_gateway);
        butEditConf.setOnClickListener(this);
        butSaveConf.setOnClickListener(this);

        initNetworkFragment();
    }

    private void initNetworkFragment(){
        SharedPreferences pre = mainActivity.getSharedPreferences("netinfo", 0);

        String serverIP = pre.getString("serverIP", "");
        String serverPort = pre.getString("serverPort", "");
        String localIP = pre.getString("localIP", "");
        String netmask = pre.getString("netmask", "");
        String gateway = pre.getString("gateway", "");

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
        butSaveConf.setEnabled(false);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.network_button_edit:
                butEditConfClicked();
                break;
            case R.id.network_button_save:
                butSaveConfClicked();
                break;
        }
    }
    private void butEditConfClicked(){
        if(mainActivity.getBluetoothState() != BluetoothChatService.STATE_CONNECTED){
            mainActivity.displayToast("蓝牙未连接，无法更改配置");
            return;
        }
        butEditConf.setEnabled(false);
        butSaveConf.setEnabled(true);
        editServerIP.setEnabled(true);
        editServerPort.setEnabled(true);
        editLocalIP.setEnabled(true);
        editNetmask.setEnabled(true);
        editGateway.setEnabled(true);
    }

    private void butSaveConfClicked(){
        String serverIP = editServerIP.getText().toString();
        String serverPort = editServerPort.getText().toString();
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
        editServerPort.setEnabled(false);
        editLocalIP.setEnabled(false);
        editNetmask.setEnabled(false);
        editGateway.setEnabled(false);
        butSaveConf.setEnabled(false);
        butEditConf.setEnabled(true);

        SharedPreferences.Editor editor = mainActivity.getSharedPreferences("netinfo",0).edit();
        editor.putString("serverIP", serverIP);
        editor.putString("serverPort", serverPort);
        editor.putString("localIP",localIP);
        editor.putString("netmask", netmask);
        editor.putString("gateway", gateway);
        editor.apply();

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
                }
                catch(InterruptedException e){
                    Log.e(TAG, "run: delay failed");
                }
                finally {
                    dia.dismiss();
                }
            }
        }.start();
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
