package com.app.tutorialapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class CountDownService extends Service {
    private Ringtone ringtone;
    private long endTime;
    private Handler handler=new Handler(Looper.getMainLooper());
    private NotificationCompat.Builder timeNotification_builder;

    public void updateTimer() {
        long remainingTime=endTime-System.currentTimeMillis();
        sendTime(remainingTime);
        sendTimeNotification(remainingTime);

        if (remainingTime <= 0) {
            if (!ringtone.isPlaying()) {
                Log.d("dev", "Ringtone Started!");
                ringtone.play();
            }
            handler.removeCallbacks(this::updateTimer);
            return;
        }

        handler.postDelayed(this::updateTimer, 90);
    }

    private void sendTimeNotification(long time) {
        timeNotification_builder.setContentText(ContextCompat.getString(getApplicationContext(), R.string.time_left)+TimerMainFragment.millisToText(time, false));
        startForeground(1, timeNotification_builder.build());
    }

    private void sendTime(long time) {
        Intent intent=new Intent("UPDATE_UI");
        intent.putExtra("time", time);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel notificationChannel=new NotificationChannel("time_channel", "Time Notifications", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);

        Intent activityIntent=new Intent(getApplicationContext(), MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activityIntent.setAction("SHOW_MAIN");
        PendingIntent activityPendingIntent=PendingIntent.getActivity(getApplicationContext(), 1, activityIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent serviceIntent=new Intent(getApplicationContext(), CountDownService.class);
        serviceIntent.setAction("STOP_TIMER");
        PendingIntent stopPendingIntent=PendingIntent.getService(getApplicationContext(), 2, serviceIntent, PendingIntent.FLAG_IMMUTABLE);

        timeNotification_builder=new NotificationCompat.Builder(getApplicationContext(), "time_channel")
                .setContentTitle(ContextCompat.getString(getApplicationContext(), R.string.time_is_running))
                .setContentIntent(activityPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher_round, "Stop", stopPendingIntent);

        Uri ringtoneLink= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone=RingtoneManager.getRingtone(getApplicationContext(), ringtoneLink);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null)
            return START_STICKY;

        String action= Objects.requireNonNull(intent.getAction());
        handler.removeCallbacks(this::updateTimer);
        switch(action) {
            case "START_TIMER":
                endTime=intent.getLongExtra("time", 1000)+System.currentTimeMillis();
                Log.d("dev", "Timer started!");
                Log.d("dev", "Timer will stop in "+(endTime-System.currentTimeMillis()));
                handler.post(this::updateTimer);
                MainActivity.instance.setFragment(MainActivity.instance.mainFragment);
                break;
            case "STOP_TIMER":
                Log.d("dev", "Stopping ringtone...");
                ringtone.stop();
                stopForeground(true);
                stopSelf();
                MainActivity.instance.setFragment(MainActivity.instance.setterFragment);
                break;
            default:
                throw new IllegalArgumentException("unknown action "+action);
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
