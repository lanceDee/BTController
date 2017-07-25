package com.example.lance.btcontroller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "MainActivity";
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_SET_NETWORK = 2;
    //public String filesize;
    private String currentDeviceName;
    private BluetoothChatService mChatService;
    private BluetoothAdapter mBluetoothAdapter;

    private MenuItem itemConnect;

    private View logLayout;
    private ImageView logImage;
    private TextView logTextView;
    public String logContent="";
    private View controllerLayout;
    private ImageView controllerImage;
    private View networkLaytout;
    private ImageView networkImage;

    private LogFragment logFragment;
    private NetworkFragment networkFragment;
    private ControllerFragment controllerFragment;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        fragmentManager = getSupportFragmentManager();
        initView();
        setSubtitle("未连接");
        setTabDisplay(0);
    }

    private void initView(){
        logLayout = findViewById(R.id.log_layout);
        logImage = (ImageView) findViewById(R.id.log_image);
        //logTextView = (TextView) findViewById(R.id.log_text_view);
        logFragment = new LogFragment();

        controllerLayout = findViewById(R.id.controller_layout);
        controllerImage = (ImageView) findViewById(R.id.controller_image);

        networkLaytout = findViewById(R.id.network_layout);
        networkImage = (ImageView) findViewById(R.id.network_image);

        logLayout.setOnClickListener(this);

        controllerLayout.setOnClickListener(this);

        networkLaytout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.controller_layout:
                setTabDisplay(0);
                break;
            case R.id.network_layout:
                setTabDisplay(1);
                break;
            case R.id.log_layout:
                setTabDisplay(2);
                break;
            default:
                break;
        }
    }

    private void setTabDisplay(int index){
        clearSelection();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index){
            case 0:
                controllerImage.setImageResource(R.drawable.image_controller_selected);
                if(controllerFragment == null)
                    controllerFragment = new ControllerFragment();
                transaction.replace(R.id.fragment_layout, controllerFragment);
                break;
            case 1:
                networkImage.setImageResource(R.drawable.image_network_selected);
                if(networkFragment == null)
                    networkFragment = new NetworkFragment();
                transaction.replace(R.id.fragment_layout, networkFragment);
                break;
            case 2:
                logImage.setImageResource(R.drawable.image_log_selected);
                if(logFragment == null)
                    logFragment = new LogFragment();
                transaction.replace(R.id.fragment_layout, logFragment);
                break;
            case 3:
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void clearSelection(){
        controllerImage.setImageResource(R.drawable.image_controller_unselected);
        networkImage.setImageResource(R.drawable.image_network_unselected);
        logImage.setImageResource(R.drawable.image_log_unselected);
    }


    public void setSubtitle(String subtitle){
        ActionBar actionBar = MainActivity.this.getSupportActionBar();
        //Log.e(TAG, "setSubtitle: actionBar = "+actionBar);
        if(actionBar != null)
            actionBar.setSubtitle(subtitle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mChatService == null)
            mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatService != null)
            mChatService.stop();
        networkFragment.stopServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        itemConnect = menu.findItem(R.id.menu_connect_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().toString().equals("连接")) {
            item.setEnabled(false);
            Intent intent = new Intent(this, ChooseDeviceActivity.class);
            startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
        }
        else {
            mChatService.stop();
            logAppend("->bluetooth disconnected!\n");
            item.setTitle("连接");
        }
        return true;
    }

    public void sendCommand(String cmd){
        if(mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
            mChatService.write(cmd.getBytes());
        else
            displayToast("蓝牙未连接");
    }

    public int getBluetoothState(){
        return mChatService.getState();
    }

    public void displayToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    public final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){

                case BluetoothChatService.MESSAGE_BT_READ:
                    byte[] buffer = (byte[]) msg.obj;
                    try {
                        String reply = new String(buffer, 0, msg.arg1);
                        String[] infoToDispaly = reply.split("\r\n");
                        //Log.e(TAG, "handleMessage: reply "+reply);
                        //若返回的是文件大小
                        if (reply.contains("filesize")){
                            int firstIndex, lastIndex;
                            firstIndex = reply.indexOf("filesize");
                            lastIndex = reply.indexOf("\r\n", firstIndex);
                            String filesize = reply.substring(firstIndex+8, lastIndex);
                            Log.e(TAG, "handleMessage: bt_read: "+filesize);
                            networkFragment.setFileSize(filesize);
                            break;
                        }
                        for(byte i = 0; i<infoToDispaly.length; i++)
                            logAppend(currentDeviceName+": "+infoToDispaly[i]+"\n");
                        final ScrollView scrollView = (ScrollView) findViewById(R.id.log_scroll_view);
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

                        case BluetoothChatService.STATE_CONNECTING:
                            setSubtitle("正在连接...");
                            break;
                        case BluetoothChatService.STATE_NOT_CONNECTED:
                            itemConnect.setEnabled(true);
                            itemConnect.setTitle("连接");
                            setSubtitle("未连接");
                            if (networkFragment != null) {
                                networkFragment.disableAllView();
                                if (networkFragment.getRecieveState())
                                    networkFragment.setViewEnabled(R.id.network_button_get_data, true);
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTED:
                            setSubtitle("已连接："+ currentDeviceName);
                            if (networkFragment != null) {
                                networkFragment.setViewEnabled(R.id.network_checkbox, true);
                                //networkFragment.setViewEnabled(R.id.network_button_start_server, true);
                                networkFragment.setViewEnabled(R.id.network_button_get_data, true);
                                networkFragment.setViewEnabled(R.id.network_button_edit, true);
                                if (networkFragment.getCheckBoxState())
                                    networkFragment.setViewEnabled(R.id.network_edit_server_port, true);
                            }
                    }
                    break;
                case BluetoothChatService.MESSAGE_TOAST:
                    String string = msg.getData().getString(BluetoothChatService.TOAST);
                    displayToast(string);
                    break;
                case TCPServerService.MESSAGE_TOAST:
                    string = msg.getData().getString(TCPServerService.TOAST);
                    displayToast(string);
                    logAppend("->"+string+"\n");
                    break;
            }
        }
    };

    public void logAppend(String string){
        logContent += string;
        if(logFragment == null)
            logFragment = new LogFragment();
        logTextView =(TextView) findViewById(R.id.log_text_view);
        if(logTextView != null)
            logTextView.append(string);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK) {
                    String deviceAddress = data.getExtras().getString(ChooseDeviceActivity.DEVICE_ADDRESS);
                    currentDeviceName = data.getExtras().getString(ChooseDeviceActivity.DEVICE_NAME);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                    mChatService.connect(device);
                    currentDeviceName = device.getName();
                }
                if(resultCode == Activity.RESULT_CANCELED)
                    itemConnect.setEnabled(true);
                break;

        }
    }
}
