package com.example.lance.btcontroller;

/**
 * Created by admin on 2017/7/4.
 */

public interface DownloadListener {
    void onSuccess();
    void onCanceled();
    void onFailed();
    void onProgress(int progress);
}
