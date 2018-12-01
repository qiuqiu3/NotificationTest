package com.example.notificationtest.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.notificationtest.MainActivity;
import com.example.notificationtest.R;

import static android.app.Notification.PRIORITY_MAX;

public class PlayerService extends Service {

    Notification mNotification;
    private Context mContext;
    private MediaPlayer mMediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification = new Notification.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setTicker("前台服务")
                .setContentTitle("提高推送到达率")
                .setContentText("原理是后台播放空白音乐，可滑掉")
                .setOngoing(true)
                .setPriority(PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build();
        startForeground(100, mNotification);

        play();

        return super.onStartCommand(intent, flags, startId);
    }

    private void play() {
        if(mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this,R.raw.blank);
            mMediaPlayer.setLooping(true);
        }
        mMediaPlayer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        mContext = null;
        mMediaPlayer.release();
        mMediaPlayer = null;
        stopSelf();
        super.onDestroy();
    }
}