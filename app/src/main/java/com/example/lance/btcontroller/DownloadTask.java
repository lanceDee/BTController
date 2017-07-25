package com.example.lance.btcontroller;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by admin on 2017/7/4.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "Download Task";
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_CANCELED = 1;
    public static final int TYPE_FAILED = 2;

    private DownloadListener listener;
    private Context context;
    private ServerSocket ss;

    private boolean isCanceled = false;
    private int lastprogress;

    public DownloadTask(DownloadListener listener){
        this.listener = listener;
        lastprogress = 0;
    }
    @Override
    protected Integer doInBackground(String... strings) {
        String filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getPath() +"/"+"com.example.lance.btcontroller";
        File file = new File(filename);
        if (!file.exists())
                file.mkdirs();
        filename += "/"+ strings[0];
        Log.e(TAG, "Filename: "+filename);
        file = new File(filename);
        RandomAccessFile savedFile;
        if ((strings[1] == null))
            return TYPE_FAILED;
        int filesize = Integer.parseInt(strings[1]);
        try{
            ss = new ServerSocket(8001);
            Socket s = ss.accept();
            InputStream is = s.getInputStream();
            savedFile = new RandomAccessFile(file, "rw");
            byte[] buffer = new byte[1024];
            int total = 0;
            int len;
            while ( (len = is.read(buffer) ) != -1 ){
                if(isCanceled){
                    file.delete();
                    return TYPE_CANCELED;
                }
                total += len;
                Log.e(TAG, "Data recieved: "+total);
                savedFile.write(buffer, 0, len);
                //os.write(buffer, 0, len);
                publishProgress(total*100/filesize);
                if (total == filesize)
                    break;

            }
            return TYPE_SUCCESS;
        }

        catch (IOException e){
            Log.e(TAG, "Failed to write file");
            e.printStackTrace();
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastprogress){
            listener.onProgress(lastprogress);
            lastprogress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch(status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }

    }
    public void cancelDownload(){
        if (ss != null){
            try {
                ss.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        isCanceled = true;
    }
}
