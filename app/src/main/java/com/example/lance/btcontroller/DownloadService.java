package com.example.lance.btcontroller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.icu.text.UnicodeSetSpanner;
import android.nfc.Tag;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends Service {
    private DownloadTask downloadTask;

    private static final String TAG = "DownloadService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: Service created!");
    }

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("获取数据成功", -1));
            NetworkFragment.RECIEVE_STATE = Constants.RECIEVE_STATE_SUCCESS;
            Toast.makeText(DownloadService.this, "获取数据成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            getNotificationManager().cancel(1);
            stopForeground(true);
            NetworkFragment.RECIEVE_STATE = Constants.RECIEVE_STATE_CANCELED;
            Toast.makeText(DownloadService.this, "已取消数据传输", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            getNotificationManager().cancel(1);
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("获取数据失败", -1));
            NetworkFragment.RECIEVE_STATE = Constants.RECIEVE_STATE_FAILED;
            Toast.makeText(DownloadService.this, "获取数据失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("正在获取数据...", progress));
        }
    };
    public DownloadService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return mBinder;
    }

    class DownloadBinder extends Binder {

        public void startDownload(String file, String len){
            String filename, filesize;
            if (downloadTask == null){
                filename = file;
                filesize = len;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(filename, filesize);
                startForeground(1, getNotification("正在获取数据...", 0));
                //Toast.makeText(DownloadService.this, "开始获取数据", Toast.LENGTH_SHORT).show();
            }
        }

        public void cancelDownload(){
            if (downloadTask != null){
                downloadTask.cancelDownload();
                downloadTask = null;
            }
            Toast.makeText(DownloadService.this, "已取消获取数据", Toast.LENGTH_SHORT).show();
        }
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("TabNumber", 2);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setContentTitle(title);
        if (progress > 0){
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
