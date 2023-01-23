package com.example.exercise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.ThemeUtils;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingScene extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = "SettingScene";
    private int themeColor = 0;

    private RadioGroup themeBtnGroup;
    private RadioButton lightThemeBtn, darkThemeBtn, defaultThemeBtn;
    private EditText timeDefaultEdit;
    private Button timeDefaultBtn, settingCloseBtn;
    private TextView producedByTex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_scene);
        PackageInfo packageInfo = null;
        try{
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        int versionCode = packageInfo.versionCode;
        String versionName = packageInfo.versionName;

        producedByTex = findViewById(R.id.producedByTex);

        StringBuilder prodStr = new StringBuilder();
        prodStr.append("v.").append(versionName).append(" ").append("prod.by ").append("9_ranr11d");
        producedByTex.setText(prodStr.toString());

        timeDefaultEdit = findViewById(R.id.timeDefaultEdit);
        timeDefaultEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

        themeBtnGroup = findViewById(R.id.themeBtnGroup);

        lightThemeBtn = findViewById(R.id.lightThemeBtn);
        darkThemeBtn = findViewById(R.id.darkThemeBtn);
        defaultThemeBtn = findViewById(R.id.defaultThemeBtn);
        timeDefaultBtn = findViewById(R.id.timeDefaultBtn);
        settingCloseBtn = findViewById(R.id.settingCloseBtn);

        lightThemeBtn.setOnClickListener(this);
        darkThemeBtn.setOnClickListener(this);
        defaultThemeBtn.setOnClickListener(this);
        timeDefaultBtn.setOnClickListener(this);
        settingCloseBtn.setOnClickListener(this);

        themeColor = AppCompatDelegate.getDefaultNightMode();
        Log.d(TAG, "theme color = " + themeColor);

        switch (MainActivity.themeNum) {
            case -1:
                themeBtnGroup.check(R.id.defaultThemeBtn);
                break;
            case 1:
                themeBtnGroup.check(R.id.lightThemeBtn);
                break;
            case 2:
                themeBtnGroup.check(R.id.darkThemeBtn);
                break;
        }
        timeDefaultEdit.setText(String.valueOf(MainActivity.timeDefault));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lightThemeBtn:
                MainActivity.themeNum = 1;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.darkThemeBtn:
                MainActivity.themeNum = 2;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.defaultThemeBtn:
                MainActivity.themeNum = -1;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            case R.id.timeDefaultBtn:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("주의");
                dialog.setMessage("확인을 누르면 앱 재시작됍니다.");

                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int tempTDV = Integer.parseInt(timeDefaultEdit.getText().toString());
                        SharedPreferences sf = getSharedPreferences(MainActivity.sfFileName, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sf.edit();
                        editor.putInt("timeDefault", tempTDV);
                        Log.d(TAG, "out time default value = " + tempTDV);
                        editor.commit();

                        PackageManager packageManager = getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        startActivity(mainIntent);
                        System.exit(0);
                    }
                });

                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialog.show();
                break;
            case R.id.settingCloseBtn :
                finish();
                break;

        }
    }
}