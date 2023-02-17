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

    private RadioGroup radioGroup;
    private RadioButton lightThemeBtn, darkThemeBtn, defThemeBtn;
    private EditText defTimeEdit, maxSetEdit;
    private Button defTimeOkBtn, closeBtn, maxSetOkBtn;
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

        //텍스트뷰
        producedByTex = findViewById(R.id.producedByTex);

        String verName = packageInfo.versionName;
        //Ver.버전 prod.by 닉네임
        StringBuilder prodStr = new StringBuilder();
        prodStr.append("Ver.").append(verName).append(" ").append("prod.by ").append(nickname);

        producedByTex.setText(prodStr.toString());
        //에딧텍스트뷰
        defTimeEdit = findViewById(R.id.settingDefTimeEdit);
        maxSetEdit = findViewById(R.id.settingMaxSetEdit);

        defTimeEdit.setText(String.valueOf(MainActivity.defTime));      //쉬는 시간 기본값
        maxSetEdit.setText(String.valueOf(MainActivity.maxSet));                //최대 세트 수
        //라디오그룹
        radioGroup = findViewById(R.id.settingThemeGroup);
        //버튼
        lightThemeBtn = findViewById(R.id.settingLightThemeBtn);
        darkThemeBtn = findViewById(R.id.settingDarkThemeBtn);
        defThemeBtn = findViewById(R.id.settingDefThemeBtn);
        defTimeOkBtn = findViewById(R.id.settingDefTimeBtn);
        closeBtn = findViewById(R.id.settingCloseBtn);
        maxSetOkBtn = findViewById(R.id.settingMaxSetBtn);

        lightThemeBtn.setOnClickListener(this);
        darkThemeBtn.setOnClickListener(this);
        defThemeBtn.setOnClickListener(this);
        defTimeOkBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        maxSetOkBtn.setOnClickListener(this);
        //현재 선택된 테마
        themeColor = AppCompatDelegate.getDefaultNightMode();
        Log.i(TAG, "theme color = " + themeColor);
        //-1 : Default, 1 : Light, 2 : Dark
        switch (MainActivity.themeMode) {
            case -1:
                radioGroup.check(R.id.settingDefThemeBtn);
                break;
            case 1:
                radioGroup.check(R.id.settingLightThemeBtn);
                break;
            case 2:
                radioGroup.check(R.id.settingDarkThemeBtn);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingLightThemeBtn:        //밝은 테마
                MainActivity.themeMode = 1;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.settingDarkThemeBtn:         //어두운 테마
                MainActivity.themeMode = 2;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.settingDefThemeBtn:          //폰 기본 테마
                MainActivity.themeMode = -1;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            case R.id.settingDefTimeBtn:           //쉬는 시간 기본값 저장
                showRestartDialog(Integer.parseInt(defTimeEdit.getText().toString()), "DEFAULT_TIME");
                break;
            case R.id.settingMaxSetBtn:            //최대 세트 수 저장
                showRestartDialog(Integer.parseInt(maxSetEdit.getText().toString()), "MAX_SET");
                break;
            case R.id.settingCloseBtn:             //닫기
                finish();
                break;
        }
    }
    //앱 재실행
    private void showRestartDialog(int editStr, String varName) {
        AlertDialog.Builder restartDialog = new AlertDialog.Builder(this);
        restartDialog.setTitle("주의")
                .setMessage("확인을 누르면 앱 재시작됍니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "out " + varName + " = " + editStr);
                        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.fileName, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(varName, editStr);
                        editor.commit();        //SharedPreference 값 저장

                        PackageManager packageManager = getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        startActivity(mainIntent);
                        System.exit(0);   //시스템 종료
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        restartDialog.show();
    }
}