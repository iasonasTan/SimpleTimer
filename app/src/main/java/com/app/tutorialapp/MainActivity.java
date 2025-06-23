package com.app.tutorialapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    public final TimerSetFragment setterFragment=new TimerSetFragment();
    public final TimerMainFragment mainFragment=new TimerMainFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED&&
                Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        instance=this;
        FragmentContainerView containerView = findViewById(R.id.container);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, setterFragment)
                .add(R.id.container, mainFragment)
                .hide(mainFragment)
                .show(setterFragment)
                .commit();

        Intent intent=getIntent();
        if(intent!=null&&"SHOW_MAIN".equals(intent.getAction())) {
            setFragment(mainFragment);
        }
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(setterFragment)
                .hide(mainFragment)
                .show(fragment)
                .commit();
    }
}
