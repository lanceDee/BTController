package com.example.lance.btcontroller;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Created by Lance on 2017/5/24.
 */

public class ControllerFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener{

    private Button butCalibration;
    private Button butAquisition;
    private CheckBox checkSetAll;
    private CheckBox checkFilter;
    private CheckBox checkGainCarli;
    private CheckBox checkOffsetCarli;
    private MainActivity mainActivity;

    private ContentKeeper mContentKeeper;//仅供保存按钮文本

    private Spinner[] gainSpinners = new Spinner[6];
    private Spinner[] samplingSpinners = new Spinner[6];

    private static final String TAG = "ControllerFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View controllerFragment = inflater.inflate(R.layout.controller_fragment_layout, container, false);
        return controllerFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mContentKeeper = new ContentKeeper();

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mainActivity,
                R.array.channel_gain, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Resources res = getResources();
        for (byte i = 0; i < 6; i++){
            int id = res.getIdentifier("controller_spinner_gain"+(i+1), "id", mainActivity.getPackageName());
            gainSpinners[i] = (Spinner) mainActivity.findViewById(id);
            gainSpinners[i].setAdapter(adapter1);
        }
        gainSpinners[0].setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(mainActivity,
                R.array.sampling_rate, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (byte i = 0; i < 6; i++){
            int id = res.getIdentifier("controller_spinner_sampling"+(i+1), "id", mainActivity.getPackageName());
            samplingSpinners[i] = (Spinner) mainActivity.findViewById(id);
            samplingSpinners[i].setAdapter(adapter2);
            samplingSpinners[i].setSelection(2);
        }
        samplingSpinners[0].setOnItemSelectedListener(this);

        butCalibration = (Button) mainActivity.findViewById(R.id.controller_button_calibration);
        butAquisition = (Button) mainActivity.findViewById(R.id.controller_button_aquisition);
        butAquisition.setOnClickListener(this);
        butCalibration.setOnClickListener(this);

        checkSetAll = (CheckBox) mainActivity.findViewById(R.id.controller_checkbox_set_all);
        checkFilter = (CheckBox) mainActivity.findViewById(R.id.controller_checkbox_filter);
        checkGainCarli = (CheckBox) mainActivity.findViewById(R.id.controller_checkbox_gain_carli);
        checkOffsetCarli = (CheckBox) mainActivity.findViewById(R.id.controller_checkbox_offset_carli);

        checkSetAll.setOnCheckedChangeListener(this);

        checkGainCarli.setChecked(true);
        checkOffsetCarli.setChecked(true);

    }

    @Override
    public void onDetach() {
//        SharedPreferences.Editor pre = mainActivity.getSharedPreferences("butState", 0).edit();
//        pre.putString("butCaliText", butCalibration.getText().toString());
//        pre.putString("butAquiText", butAquisition.getText().toString());
//        pre.apply();

        Log.e(TAG, "onDetach: ");
        mContentKeeper.setButAquiText(butAquisition.getText().toString());
        mContentKeeper.setButCarliText(butCalibration.getText().toString());
        super.onDetach();
    }

    @Override
    public void onResume() {
//        SharedPreferences pre = mainActivity.getSharedPreferences("butState", 0);
//        String butCaliText = pre.getString("butCaliText", "");
//        String butAquiText = pre.getString("butAquiText", "");
        Log.e(TAG, mContentKeeper.getButCarliText());
        butCalibration.setText(mContentKeeper.getButCarliText());
        butAquisition.setText(mContentKeeper.getButAquiText());
        super.onResume();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.controller_button_calibration:
                if (butCalibration.getText().toString().equals(mainActivity.getString(R.string.button_start_carlibration))) {
                    butStartCaliClicked();
                }
                else {
                    butStopCaliClicked();
                    butCalibration.setText(R.string.button_start_carlibration);
                }
                break;
            //case R.id.controller_button_send:
              //  mainActivity.sendCommand(Constants.CMD_SEND_DATA);
             //   break;
            case R.id.controller_button_aquisition:

                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            for (byte i = 1; i < 6; i++)
                gainSpinners[i].setEnabled(false);
            for (byte i = 1; i < 6; i++)
                samplingSpinners[i].setEnabled(false);
        }
        else{
            for (byte i = 1; i < 6; i++)
                gainSpinners[i].setEnabled(true);
            for (byte i = 1; i < 6; i++)
                samplingSpinners[i].setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (checkSetAll.isChecked()){
            switch (parent.getId()){
                case R.id.controller_spinner_gain1:
                    for (byte i = 1; i < 6; i++)
                        gainSpinners[i].setSelection(position);
                    break;
                case R.id.controller_spinner_sampling1:
                    for (byte i = 1; i < 6; i++)
                        samplingSpinners[i].setSelection(position);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void butStartCaliClicked() {
        //MyView是自定义控件 继承自RelativeLayout
        final MyView myView = new MyView(getContext());

        new AlertDialog.Builder(mainActivity)
        .setTitle("请选择校准频率")
        .setView(myView)
        .setNegativeButton("取消", null)
        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mainActivity.displayToast("正在校准");
                //使用自定义控件的自定义方法
                mainActivity.logAppend("->开始校准（使用频率："+myView.getSelectedItem().toString()+"Hz)\n");
                Log.e(TAG, "Item Position: "+myView.getSelectedItemPosition());

               // 发送开始校准命令


                butCalibration.setText(R.string.button_stop_carlibration);
            }
        }).show();
    }

    private void butStopCaliClicked(){
        mainActivity.displayToast("校准已停止");
        mainActivity.logAppend("->停止校准\n");
        //发送停止校准命令
    }

}
