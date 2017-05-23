package com.example.lance.btcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class ChooseDeviceActivity extends AppCompatActivity {
    private static final String TAG = "ChooseDeviceActivity";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String DEVICE_NAME = "device_name";
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        setResult(RESULT_CANCELED);//防止用户中途退出 先将结果设置为取消

        Button butScan = (Button) findViewById(R.id.button_scan);
        butScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
            }
        });

        ArrayAdapter<String> pairedDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        ListView pairedListView = (ListView) findViewById(R.id.paired_list_view);
        pairedListView.setAdapter(pairedDeviceArrayAdapter);
        pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String deviceInfo = ((TextView) view).getText().toString();
                String deviceAddress = deviceInfo.substring(deviceInfo.length()-17);

               // Log.e(TAG, "onItemClick: Address:"+deviceAddress+" position: "+i);

                Intent intent = new Intent();
                intent.putExtra(DEVICE_ADDRESS, deviceAddress);
                intent.putExtra(DEVICE_NAME, deviceInfo.substring(0, deviceInfo.length()-17));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
            for(BluetoothDevice device : pairedDevices)
                pairedDeviceArrayAdapter.add(device.getName()+"\n"+device.getAddress());



    }




}