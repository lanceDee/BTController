package com.example.lance.btcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Lance on 2017/5/24.
 */

public class ControllerFragment extends Fragment implements View.OnClickListener{

    private Button butSave;
    private Button butSend;
    private Button butStop;

    private MainActivity mainActivity;

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
        butSave = (Button) mainActivity.findViewById(R.id.controller_button_save);
        butSend = (Button) mainActivity.findViewById(R.id.controller_button_send);
        butStop = (Button) mainActivity.findViewById(R.id.controller_button_stop);

        butSave.setOnClickListener(this);
        butSend.setOnClickListener(this);
        butStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.controller_button_save:
                mainActivity.sendCommand(Constants.CMD_SAVE_DATA);
                break;
            case R.id.controller_button_send:
                mainActivity.sendCommand(Constants.CMD_SEND_DATA);
                break;
            case R.id.controller_button_stop:
                mainActivity.sendCommand(Constants.CMD_STOP);
                break;
            default:
                break;
        }
    }
}
