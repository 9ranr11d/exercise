package com.example.exercise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingScene extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = "SettingScene";
    public final String nickname = "9_ranr11d";
    private int themeColor = 0;

    private RadioGroup themeBtnGroup;
    private RadioButton lightThemeBtn, darkThemeBtn, defaultThemeBtn;
    private EditText timeDefaultEdit, maxSetEdit;
    private Button timeDefaultBtn, settingCloseBtn, maxSetBtn;
    private TextView producedByTex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_scene);
        //앱 버전 출력
        PackageInfo packageInfo = null;
        try{
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        int versionCode = packageInfo.versionCode;
        String versionName = packageInfo.versionName;
        //Ver.버전 prod.by 닉네임
        StringBuilder prodStr = new StringBuilder();
        prodStr.append("Ver.").append(versionName).append(" ").append("prod.by ").append(nickname);
        //TextView
        producedByTex = findViewById(R.id.producedByTex);

        producedByTex.setText(prodStr.toString());
        //EditText
        timeDefaultEdit = findViewById(R.id.timeDefaultEdit);
        maxSetEdit = findViewById(R.id.maxSetEdit);

        timeDefaultEdit.setText(String.valueOf(MainActivity.timeDefault));      //쉬는 시간 기본값
        maxSetEdit.setText(String.valueOf(MainActivity.maxSet));                //최대 세트 수
        //ridio
        themeBtnGroup = findViewById(R.id.themeBtnGroup);
        //Btn
        lightThemeBtn = findViewById(R.id.lightThemeBtn);
        darkThemeBtn = findViewById(R.id.darkThemeBtn);
        defaultThemeBtn = findViewById(R.id.defaultThemeBtn);
        timeDefaultBtn = findViewById(R.id.timeDefaultBtn);
        settingCloseBtn = findViewById(R.id.settingCloseBtn);
        maxSetBtn = findViewById(R.id.maxSetBtn);

        lightThemeBtn.setOnClickListener(this);
        darkThemeBtn.setOnClickListener(this);
        defaultThemeBtn.setOnClickListener(this);
        timeDefaultBtn.setOnClickListener(this);
        settingCloseBtn.setOnClickListener(this);
        maxSetBtn.setOnClickListener(this);
        //현재 선택된 테마
        themeColor = AppCompatDelegate.getDefaultNightMode();
        Log.d(TAG, "theme color = " + themeColor);
        //-1 : Default, 1 : Light, 2 : Dark
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lightThemeBtn:        //밝은 테마
                MainActivity.themeNum = 1;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.darkThemeBtn:         //어두운 테마
                MainActivity.themeNum = 2;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.defaultThemeBtn:      //폰 기본 테마
                MainActivity.themeNum = -1;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            case R.id.timeDefaultBtn:       //쉬는 시간 기본값 저장
                showRestartDialog(timeDefaultEdit.getText().toString(), "timeDefault");
                break;
            case R.id.maxSetBtn:            //최대 세트 수 저장
                showRestartDialog(maxSetEdit.getText().toString(), "maxSet");
                break;
            case R.id.settingCloseBtn :     //닫기
                finish();
                break;
        }
    }
    //앱 재실행
    private void showRestartDialog(String editStr, String valueName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("주의");
        dialog.setMessage("확인을 누르면 앱 재시작됍니다.");

        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int tempTDV = Integer.parseInt(editStr);
                SharedPreferences sf = getSharedPreferences(MainActivity.sfFileName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.putInt(valueName, tempTDV);
                Log.d(TAG, "out " + valueName + " = " + tempTDV);
                editor.commit();        //SharedPreference 값 저장

                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                startActivity(mainIntent);
                System.exit(0);   //시스템 종료
            }
        });

        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }
}