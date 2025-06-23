package com.app.tutorialapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class TimerMainFragment extends Fragment {
    private TextView timeOutput_textview;

    private final BroadcastReceiver timeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time=intent.getLongExtra("time", -1);
            timeOutput_textview.setText(ContextCompat.getString(requireContext(),
                    R.string.time_left)+": "+millisToText(time, true));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextCompat.registerReceiver(requireContext(), timeReceiver,
                new IntentFilter("UPDATE_UI"), ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireContext().unregisterReceiver(timeReceiver);
    }

    public static String millisToText(final long millis_origin, boolean showMillis) {
        long millis=Math.abs(millis_origin);
        long hours=0, mins=0, secs=0;
        while (millis>=1000) { secs++; millis-=1000; }
        while (secs>=60) { mins++; secs-=60; }
        while (mins>=60) { hours++; mins-=60; }
        String out=String.format(Locale.getDefault(),
                (millis_origin<0?"-":"+")+"%d:%d:%d", hours, mins, secs);
        return showMillis?out+"."+millis:out;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timeOutput_textview=view.findViewById(R.id.remaining_time_textview);
        view.findViewById(R.id.stop_button).setOnClickListener(v -> {
            Log.d("dev", "Stopping timer...");
            Log.d("dev", "returning to setter page...");
            Intent intent=new Intent(requireContext(), CountDownService.class);
            intent.setAction("STOP_TIMER");
            requireContext().startForegroundService(intent);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.time_main_layout, container, false);
    }
}
