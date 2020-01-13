package com.young.word;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private ScreenListener screenListener;
    private FragmentTransaction transaction;
    private StudyFragment studyFragment;
    private SetFragment setFragment;
    private Button wrongBtn;
    private Button btnStudy, btnSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_layout);
        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);
        wrongBtn = findViewById(R.id.wrong_btn);
        wrongBtn.setOnClickListener(this);
        btnStudy = findViewById(R.id.btn_study);
        btnStudy.setOnClickListener(this);
        btnSet = findViewById(R.id.btn_set);
        btnSet.setOnClickListener(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                if (sharedPreferences.getBoolean("btnIf", false)) {

                    if (sharedPreferences.getBoolean("If", false)) {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onScreenOff() {
                editor.putBoolean("If", true);
                editor.commit();
                BaseApplication.destroyActivity("mainActivity");
            }

            @Override
            public void onUserPresent() {
                editor.putBoolean("If", false);
                editor.commit();
            }
        });

        studyFragment = new StudyFragment();
        setFragment(studyFragment);
    }

    private void setFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    public void study() {
        if (studyFragment == null) {
            studyFragment = new StudyFragment();
        }
        setFragment(studyFragment);
    }

    public void set() {
        if (setFragment == null) {
            setFragment = new SetFragment();
        }
        setFragment(setFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wrong_btn:
                YoungToast.showTextToast("跳转到错题界面", HomeActivity.this);
                break;
            case R.id.btn_study:
                study();
                break;
            case R.id.btn_set:
                set();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenListener.unregisterListener();
    }
}
