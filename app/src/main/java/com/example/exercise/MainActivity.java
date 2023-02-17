package com.example.exercise;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public final static String fileName = "save_value";
    public static int dotsPerInch = 0, seq = 0, setNum = 0, themeMode = 0, defTime = 0, lastSeq = 0, maxSet = 0;

    private Button setBtn;
    private BottomNavigationView bottomNavigationView;

    public static ExerciseDAO exerciseDAO;
    private FragmentManager fragmentManager = getSupportFragmentManager();      //프래그먼트 매니저
    private StopwatchMenu stopwatchMenu = new StopwatchMenu();                  //스탑워치 프래그먼트
    private TimerMenu timerMenu = new TimerMenu();                              //타이머 프래그먼트
    public RecordMenu recordMenu = new RecordMenu();                            //기록 프래그먼트

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });
        //DPI 구함
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int densityDPI = displayMetrics.densityDpi;
        dotsPerInch = densityDPI / 160;
        
        Log.i(TAG, "density DPI = " + densityDPI);
        Log.i(TAG, "DPI = " + dotsPerInch);
        //DB로 부터 데이터 받아옴
        exerciseDAO = new ExerciseDAO();        //앱을 껐다 킬때마다 새로 만들기 위해, 뒤로가기로 종료시 초기화가 안되서 여기로 옮김
        //저장되있는 DB의 모든 데이터를 exercise객체에 담음
        DBHelper helper = new DBHelper(this, "record.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM EXERCISE_TB;";
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()) {
            lastSeq = cursor.getInt(0);
            exerciseDAO.insertObj(
                    lastSeq,
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6));
        }

        cursor.close();
        db.close();
        helper.close();
        //SharedPreferences에서 변수를 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, MODE_PRIVATE);
        seq = sharedPreferences.getInt("SEQ", 0);
        themeMode = sharedPreferences.getInt("THEME_MODE", -1);
        defTime = sharedPreferences.getInt("DEFAULT_TIME", 60);
        maxSet = sharedPreferences.getInt("MAX_SET", 20);

        Log.i(TAG, "in seq = " + seq);
        Log.i(TAG, "in theme mode = " + themeMode);
        Log.i(TAG, "in default time = " + defTime);
        Log.i(TAG, "in max set = " + maxSet);
        //저장된 테마 모드로 변경
        switch (MainActivity.themeMode) {
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
        setBtn = findViewById(R.id.mainactSetBtn);

        setBtn.setOnClickListener(this);
        int defMit = defTime / 60, defSec = defTime % 60;
        //푸시알림으로부터 데이터를 받음
        Intent recdMainIntent = getIntent();
        int tempMit = recdMainIntent.getIntExtra("MINUTE", defMit);
        int tempSec = recdMainIntent.getIntExtra("SECOND", defSec);
        int tempSetNum = recdMainIntent.getIntExtra("SET_NUM", 0);
        int tempRoutine = recdMainIntent.getIntExtra("ROUTINE", 1);
        String tempRPS = recdMainIntent.getStringExtra("REPS_PER_SET");
        //데이터를 프레그먼트로 전송을 위해 번들에 담음
        Bundle toTimerBundle = new Bundle();
        toTimerBundle.putInt("MINUTE", tempMit);
        toTimerBundle.putInt("SECOND", tempSec);
        toTimerBundle.putInt("SET_NUM", tempSetNum);
        toTimerBundle.putInt("ROUTINE", tempRoutine);
        toTimerBundle.putString("REPS_PER_SET", tempRPS);

        timerMenu.setArguments(toTimerBundle);    //세이브 데이터 보냄
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
    public static String setStrFormat(String str1, String str2) {
        StringBuilder resultBuilder = new StringBuilder();

        String[] strAry1 = str1.split(",");
        String[] strAry2 = str2.split(",");

        for(int i = 0; i < strAry1.length; i++) {
            resultBuilder.append(strAry1[i]).append("/").append(strAry2[i]);

            if(i != strAry1.length - 1) {
                resultBuilder.append(", ");
            }
        }
        return resultBuilder.toString();     //('str1'/'str2', 'str1'/'str2, ...)
    }
    @Override   //앱 종료시 테마모드와 SEQ를 저장
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SEQ", seq);
        editor.putInt("THEME_MODE", themeMode);
        editor.commit();

        Log.i(TAG, "out seq = " + seq);
        Log.i(TAG, "out theme mode = " + themeMode);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mainactSetBtn:      //설정창으로 이동
                Intent settingIntent = new Intent(this, SettingScene.class);

                launcher.launch(settingIntent);
                break;
        }
    }
}