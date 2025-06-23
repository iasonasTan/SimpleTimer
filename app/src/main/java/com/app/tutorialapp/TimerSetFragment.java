package com.app.tutorialapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TimerSetFragment extends Fragment {
    private Button startTimer_button;
    private NumberPicker hours_npicker, minutes_npicker, seconds_npicker;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTimer_button=view.findViewById(R.id.start_button);
        hours_npicker=view.findViewById(R.id.hours_npicker);
        hours_npicker.setMaxValue(23);
        minutes_npicker=view.findViewById(R.id.minutes_npicker);
        minutes_npicker.setMaxValue(59);
        seconds_npicker=view.findViewById(R.id.seconds_npicker);
        seconds_npicker.setMaxValue(59);

        startTimer_button.setOnClickListener(v -> {
            long time=getTimeMillis();
            Intent intent = getIntent(time);
            requireContext().startForegroundService(intent);
        });
    }

    private Intent getIntent(long timeMillis) {
        Intent out=new Intent(requireContext(), CountDownService.class);
        out.setAction("START_TIMER");
        out.putExtra("time", timeMillis);
        return out;
    }

    public long getTimeMillis() {
        long out=0L;
        out+=seconds_npicker.getValue()*1000L;
        out+=minutes_npicker.getValue()*1000L*60;
        out+=hours_npicker.getValue()*1000L*60*60;
        Log.d("dev", "Returning time: "+out);
        return out;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.time_set_layout, container, false);
    }
}
