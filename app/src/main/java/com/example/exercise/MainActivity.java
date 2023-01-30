package com.example.exercise;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = "MainActivity";
    public final static String sfFileName = "save_value";
    public static int DPI = 0, seq = 0, setNum = 0, themeNum = 0, timeDefault = 0, lastSeq = 0, maxSet = 0;

    private Button settingBtn;

    public static ExerciseDAO exerciseDAO;

    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager = getSupportFragmentManager();      //프래그먼트 매니저
    private StopwatchMenu stopwatchMenu = new StopwatchMenu();                  //스탑워치 프래그먼트
    private TimerMenu timerMenu = new TimerMenu();                              //타이머 프래그먼트
    public RecordMenu recordMenu = new RecordMenu();                            //기록 프래그먼트

    private ActivityResultLauncher<Intent> settingLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_CANCELED) {  //취소

            }
        });
        //DPI 구함
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        int densityDpi = metrics.densityDpi;
        Log.d(TAG, "densityDpi = " + densityDpi);
        DPI = densityDpi / 160;
        Log.d(TAG, "DPI = " + DPI);
        //DB로 부터 데이터 받아옴
        exerciseDAO = new ExerciseDAO();        //앱을 껐다 킬때마다 새로 만들기 위해, 뒤로가기로 종료시 초기화가 안되서 여기로 옮김
        //저장되있는 DB의 모든 데이터를 exercise객체에 담음
        DBHelper helper = new DBHelper(this, "record.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM exercise;";
        Cursor lookupCursor = db.rawQuery(sql,null);
        while(lookupCursor.moveToNext()) {
            lastSeq = lookupCursor.getInt(0);
            exerciseDAO.insertObj(
                    lastSeq,
                    lookupCursor.getString(1),
                    lookupCursor.getString(2),
                    lookupCursor.getString(3),
                    lookupCursor.getInt(4),
                    lookupCursor.getString(5),
                    lookupCursor.getString(6));
        }
        lookupCursor.close();
        db.close();
        helper.close();
        //db seq, theme mode, time default value를 받아옴
        SharedPreferences sf = getSharedPreferences(sfFileName, MODE_PRIVATE);
        seq = sf.getInt("seq", 0);
        Log.d(TAG, "in seq = " + seq);
        themeNum = sf.getInt("themeNum", -1);
        Log.d(TAG, "in themeNum = " + themeNum);
        timeDefault = sf.getInt("timeDefault", 60);
        Log.d(TAG, "in timeDefault = " + timeDefault);
        maxSet = sf.getInt("maxSet", 20);
        Log.d(TAG, "in maxSet = " + maxSet);
        //저장된 테마 모드로 변경
        switch (MainActivity.themeNum) {
            case -1:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
        //버튼
        settingBtn = findViewById(R.id.settingBtn);

        settingBtn.setOnClickListener(this);
        int tDTM = timeDefault / 60, tDTS = timeDefault % 60;
        //푸시알림으로부터 데이터를 받음
        Intent getNotiIntent = getIntent();
        int tempTimeMNum = getNotiIntent.getIntExtra("timeMNum", tDTM);
        int tempTimeSNum = getNotiIntent.getIntExtra("timeSNum", tDTS);
        int tempSetNum = getNotiIntent.getIntExtra("setNum", 0);
        int tempRoutine = getNotiIntent.getIntExtra("routine", 1);
        //데이터를 프레그먼트로 전송을 위해 번들에 담음
        Bundle timerBundle = new Bundle();
        timerBundle.putInt("timeMNum", tempTimeMNum);
        timerBundle.putInt("timeSNum", tempTimeSNum);
        timerBundle.putInt("setNum", tempSetNum);
        timerBundle.putInt("routine", tempRoutine);

        timerMenu.setArguments(timerBundle);    //세이브 데이터 보냄
        //바텀네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavi);

        fragmentManager.beginTransaction().add(R.id.mainFrame, timerMenu).commit();
        //바텀네비게이터 리스너
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.stopwatchItem:
                        fragmentManager.beginTransaction().replace(R.id.mainFrame, stopwatchMenu).commit();
                        break;
                    case R.id.timerItem:
                        fragmentManager.beginTransaction().replace(R.id.mainFrame, timerMenu).commit();
                        break;
                    case R.id.recordItem:
                        fragmentManager.beginTransaction().replace(R.id.mainFrame, recordMenu).commit();
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.timerItem);     //바텀 네비게이션 기본값을 TimerMenu로 설정
    }
    //두개의 배열을 하나씩 엮음
    public static String stringFormat(String str1, String str2) {
        StringBuilder mergeStr = new StringBuilder();

        String[] tempStr1 = str1.split(",");
        String[] tempStr2 = str2.split(",");

        for(int i = 0; i < tempStr1.length; i++) {
            mergeStr.append(tempStr1[i]).append("/").append(tempStr2[i]);

            if(i != tempStr1.length - 1) {
                mergeStr.append(", ");
            }
        }
        return mergeStr.toString();
    }
    @Override   //앱 종료시 테마모드와 seq를 저장
    protected void onStop() {
        super.onStop();
        SharedPreferences sf = getSharedPreferences(sfFileName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putInt("seq", seq);
        Log.d(TAG, "out seq = " + seq);
        editor.putInt("themeNum", themeNum);
        Log.d(TAG, "out themeNum = " + themeNum);
        editor.commit();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingBtn:      //설정창으로 이동
                Intent settingIntent = new Intent(this, SettingScene.class);

                settingLauncher.launch(settingIntent);
                break;
        }
    }
}