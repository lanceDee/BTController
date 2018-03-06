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

    public Object getSelectedItem(){
        return ((Spinner) findViewById(R.id.controller_spinner_choose_rate)).getSelectedItem();
    }

    public int getSelectedItemPosition(){
        return ((Spinner) findViewById(R.id.controller_spinner_choose_rate)).getSelectedItemPosition();
    }
}