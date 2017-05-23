package com.example.lance.btcontroller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_SET_NETWORK = 2;
    private String currentDeviceName = null;
    private BluetoothChatService mChatService;
    private BluetoothAdapter mBluetoothAdapter;

    private TextView textView = null;
    private MenuItem itemConnect = null;
    private MenuItem itemSetting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setSubtitle("未连接");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mChatService == null)
            mChatService = new BluetoothChatService(this, mHandler);

        textView = (TextView) findViewById(R.id.text_view_main);
        Button butSave = (Button) findViewById(R.id.button_save);
        Button butSend = (Button) findViewById(R.id.button_send);
        Button butStop = (Button) findViewById(R.id.button_stop);

        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.CMD_SAVE_DATA);
            }
        });

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.CMD_SEND_DATA);
            }
        });

        butStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.CMD_STOP);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatService != null)
            mChatService.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        itemConnect = menu.findItem(R.id.menu_connect_item);
        itemSetting = menu.findItem(R.id.menu_setting_item);
        itemSetting.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_connect_item:{
                if(item.getTitle().toString().equals("连接")) {
                    item.setEnabled(false);
                    Intent intent = new Intent(this, ChooseDeviceActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                }
                else {
                    mChatService.stop();
                    textView.setText("");
                    item.setTitle("连接");
                }

                return true;
            }
            case R.id.menu_clear_item:{
                textView.setText("");
                return true;
            }
            case R.id.menu_setting_item:{
                Intent intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_SET_NETWORK);
                return true;
            }
            default:
                return false;
        }
    }

    private void sendCommand(String cmd){
        if(mChatService != null)
            mChatService.write(cmd.getBytes());
    }

    public void displayToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    public void setSubtitle(String subtitle){
        ActionBar actionBar = MainActivity.this.getSupportActionBar();
        //Log.e(TAG, "setSubtitle: actionBar = "+actionBar);
        if(actionBar != null)
            actionBar.setSubtitle(subtitle);
    }

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case BluetoothChatService.MESSAGE_BT_READ:
                    byte[] buffer = (byte[]) msg.obj;
                    try {
                        String reply = new String(buffer, 0, msg.arg1);
                        String[] infoToDispaly = reply.split("\r\n");
                        for(byte i = 0; i<infoToDispaly.length; i++)
                            textView.append("->"+infoToDispaly[i]+"\n");
                        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });

                    }
                    catch(Exception e){

                    }

                    break;
                case BluetoothChatService.MESSAGE_DEVICE_NAME:
                    itemConnect.setEnabled(true);
                    itemConnect.setTitle("断开");
                    break;
                case BluetoothChatService.MESSAGE_STATE_CHANGE:
                    //更新连接状态显示

                    switch (msg.arg1){

                        case BluetoothChatService.STATE_CONNECTED:
                            itemSetting.setEnabled(true);
                            setSubtitle("已连接:"+currentDeviceName);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setSubtitle("正在连接...");
                            break;
                        case BluetoothChatService.STATE_NOT_CONNECTED:
                            itemConnect.setEnabled(true);
                            itemConnect.setTitle("连接");
                            itemSetting.setEnabled(false);
                            setSubtitle("未连接");
                            break;
                    }
                    break;
                case BluetoothChatService.MESSAGE_TOAST:
                    String string = msg.getData().getString(BluetoothChatService.TOAST);
                    Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK) {
                    String deviceAddress = data.getExtras().getString(ChooseDeviceActivity.DEVICE_ADDRESS);
                    currentDeviceName = data.getExtras().getString(ChooseDeviceActivity.DEVICE_NAME);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                    mChatService.connect(device);
                }
                if(resultCode == Activity.RESULT_CANCELED)
                    itemConnect.setEnabled(true);
                break;

            case REQUEST_SET_NETWORK:
                SharedPreferences pre = getSharedPreferences("netinfo", 0);
                String netinfo = pre.getString("localIP", "")+"/"+pre.getString("netmask", "")+"/"+pre.getString("gateway", "");
                //Log.e(TAG, "onActivityResult: netinfo "+netinfo);
                sendCommand("netcfg/"+netinfo+"\r\n");
                final ProgressDialog dia = new ProgressDialog(this);
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
                break;
        }
    }
}
