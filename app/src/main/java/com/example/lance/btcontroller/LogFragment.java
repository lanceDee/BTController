package com.example.lance.btcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Lance on 2017/5/23.
 */

public class LogFragment extends Fragment{

    private MainActivity mainActivity;
    private TextView logTextView;
    private Button butClear;
    private static final String TAG = "LogFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View logLayout = inflater.inflate(R.layout.log_fragment_layout, container, false);
        return logLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        logTextView = (TextView) mainActivity.findViewById(R.id.log_text_view);
        butClear = (Button) mainActivity.findViewById(R.id.log_button_clear);
        butClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logTextView.setText("");
                mainActivity.logContent = "";
            }
        });
        //Log.e(TAG, "onActivityCreated: logTextView: "+logTextView.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        logTextView.setText(mainActivity.logContent);
    }
}
