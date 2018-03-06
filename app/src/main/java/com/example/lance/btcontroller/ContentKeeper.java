package com.example.lance.btcontroller;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2018\1\3 0003.
 */

public class ContentKeeper {
    private String mButCarliText;
    private String mButAquiText;

    public ContentKeeper(){
        mButCarliText = "开始校准";
        mButAquiText = "开始采集";
    }

    public String getButCarliText() {
        return mButCarliText;
    }

    public void setButCarliText(String butCarliText) {
        mButCarliText = butCarliText;
    }

    public String getButAquiText() {
        return mButAquiText;
    }

    public void setButAquiText(String butAquiText) {
        mButAquiText = butAquiText;
    }
}
