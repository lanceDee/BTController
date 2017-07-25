package com.example.lance.btcontroller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lance on 2017/5/31.
 */

public class TCPServerService {
    private static final String TAG = "TCPServerService";
    private ServerThread serverThread;
    private ServerSocket serverSocket;
    private Socket s;
    private int port;
    private String fileName;
   // private ProgressDialog progressDialog;
    private Context context;
    private Handler mHandler;

    private boolean isRunning;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";

    public TCPServerService(Context context, int p, String name, Handler mHandler){
        this.port = p;
        this.fileName = name;
        this.context = context;
        this.mHandler = mHandler;
        //progressDialog = new ProgressDialog(context);
    }

    public void start(){
        if(serverThread != null)
            stop();
        if(serverThread != null)
            Log.e(TAG, "start: serverThread: "+serverThread.toString());
        serverThread = new ServerThread();
        serverThread.start();
    }

    public void stop(){
        serverThread.cancel();
    }

    private class ServerThread extends Thread{
        @Override
        public void run() {
            try{
                serverSocket = new ServerSocket(port);
                Log.e(TAG, "run: serverthread started" );
                isRunning = true;
                while(true) {
                    s = serverSocket.accept();
                    displayToast(s.getInetAddress().getHostAddress().toString()+" 已连接");
                    FileOutputStream out = context.openFileOutput(fileName, context.MODE_PRIVATE);
                    BufferedOutputStream bufOut = new BufferedOutputStream(out);
                    InputStream in = s.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while (true) {
                        len = in.read(buffer);
                        if (len == -1) {
                            s.close();
                            displayToast("客户端已关闭");
                            break;
                        }
                        bufOut.write(buffer, 0, len);
                        bufOut.flush();
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        public void cancel(){
            if (s == null)
                return;
            try{
                s.close();
                serverSocket.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                serverThread = null;
            }
            isRunning = false;
        }
    }
    public synchronized boolean getState(){
        return isRunning;
    }

    private void displayToast(String string){
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, string);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
}
