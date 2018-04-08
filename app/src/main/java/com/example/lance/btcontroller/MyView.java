package com.example.lance.btcontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Created by Administrator on 2017\11\17 0017.
 */
public class MyView extends RelativeLayout{
    public MyView(Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.choose_carli_rate_layout, this);
    }

    public Object getSelectedRate(){
        return ((Spinner) findViewById(R.id.controller_spinner_choose_rate)).getSelectedItem();
    }

    public int getSelectedRatePosition(){
        return ((Spinner) findViewById(R.id.controller_spinner_choose_rate)).getSelectedItemPosition();
    }
    public Object getSelectedAmplitude(){
        return ((Spinner) findViewById(R.id.controller_spinner_choose_amplitude)).getSelectedItem();
    }

    public int getSelectedAmplitudePosition(){
        return ((Spinner) findViewById(R.id.controller_spinner_choose_amplitude)).getSelectedItemPosition();
    }
}
