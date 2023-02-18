package com.example.exercise;

import static com.example.exercise.MainActivity.maxSet;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class TimerScene extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "TimerScene";
    private int minute = 0, second = 0, setNum = 0, defMin = 0, defSec = 0, routine = 0;
    private String strRPS = "";
    private boolean isReserveFlag = false;
    public static boolean isSRecordFlag = false, isManageFlag = false;

    private TextView minTex, secTex, setTex, numTex;
    private EditText numEdit;
    private Button stopBtn, reserveBtn, reserveCancelBtn, managementBtn;
    private ProgressBar progressBar;

    private Timer timer;
    private TimerTask restTimerTask = null;
    private ActivityResultLauncher<Intent> launcher;

    private Intent reserveIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_scene);
        //기존 푸쉬 알림 제거
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent launcherIntent = result.getData();
            if(result.getResultCode() == 2) {           //예약
                String timerVol = launcherIntent.getStringExtra("VOLUME");        //RecordScen으로 부터
                String timerNum = launcherIntent.getStringExtra("NUMBER");
                String timerType = launcherIntent.getStringExtra("TYPE");
                String timerName = launcherIntent.getStringExtra("NAME");
                
                reserveIntent.putExtra("VOLUME", timerVol);
                reserveIntent.putExtra("NUMBER", timerNum);
                reserveIntent.putExtra("TYPE", timerType);
                reserveIntent.putExtra("NAME", timerName);

                Toast.makeText(getApplication(), "기록이 예약되었습니다.", Toast.LENGTH_SHORT).show();

                isReserveFlag = true;
            }else if(result.getResultCode() == RESULT_CANCELED) {
                reserveBtn.setEnabled(true);
                reserveCancelBtn.setEnabled(false);
            }
        });
        //텍스트뷰
        minTex = findViewById(R.id.sTimerMinTex);
        secTex = findViewById(R.id.sTimerSecTex);
        setTex = findViewById(R.id.sTimerSetTex);
        numTex = findViewById(R.id.sTimerNumTex);
        //에딧텍스트뷰
        numEdit = findViewById(R.id.sTimerNumEdit);

        numEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);  //키보드 내려가게
        //버튼
        stopBtn = findViewById(R.id.sTimerStopBtn);
        reserveBtn = findViewById(R.id.sTimerReserveBtn);
        reserveCancelBtn = findViewById(R.id.sTimerReserveCancelBtn);
        managementBtn = findViewById(R.id.sTimerManagementBtn);

        stopBtn.setOnClickListener(this);
        reserveBtn.setOnClickListener(this);
        reserveCancelBtn.setOnClickListener(this);
        managementBtn.setOnClickListener(this);

        reserveCancelBtn.setEnabled(false);
        //프레스바
        progressBar = findViewById(R.id.sTimerProgress);
        //TimeMenu로부터
        Intent recdSTimerIntent = getIntent();
        defMin = recdSTimerIntent.getIntExtra("MINUTE", 1);
        defSec = recdSTimerIntent.getIntExtra("SECOND", 0);
        setNum = recdSTimerIntent.getIntExtra("SET_NUM", 0);
        routine = recdSTimerIntent.getIntExtra("ROUTINE", 1);
        strRPS = recdSTimerIntent.getStringExtra("REPS_PER_SET");

        minute = defMin;
        second = defSec;
        //프레스바
        progressBar.setMax(getMaxTime(minute, second));
        progressBar.setMin(0);
        progressBar.setProgress(getMaxTime(minute, second));

        setTex.setText(String.valueOf(++setNum));
        numTex.setText(setNum + "회째 : ");
        //타이머 시작
        restTimerTask = createTimerTask();
        timer.schedule(restTimerTask, 0, 1000);
    }

    private TimerTask createTimerTask() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {                 //타이머를 메인스레드 X 새로운 스레드 O
                runOnUiThread(new Runnable() {  //UI 변경은 메인스레드에서
                    @Override
                    public void run() {
                        if(second != 0)         //초가 0이 아닐 시 감소
                            second--;
                        else if(minute != 0) {  //분이 0이 아닐시 초, 분 감소
                            second = 59;
                            minute--;
                        }
                        //분, 시 한자리 일때 빈자리 0으로 채움
                        setTenFormat(second, secTex);
                        setTenFormat(minute, minTex);
                        progressBar.setProgress(getMaxTime(minute, second));
                        //분, 초가 0되면 종료
                        if(minute == 0 && second == 0) {
                            timer.cancel();

                            setTenFormat(second, secTex);
                            setTenFormat(minute, minTex);

                            if(setNum > maxSet) {
                                setNum = maxSet;
                                Toast.makeText(getApplication(), maxSet + "세트가 넘어서 카운트가 되지 않습니다.", Toast.LENGTH_SHORT).show();;
                            }

                            reserveIntent.putExtra("SET_NUM", setNum);

                            if(!isReserveFlag) {   //예약 X
                                reserveIntent.putExtra("REPS_PER_SET", numEdit.getText().toString());
                                setResult(RESULT_OK, reserveIntent);
                            }
                            else                   //예약 O
                                setResult(2, reserveIntent);

                            showNotify(TAG, "쉬는 시간 끝");

                            if(isSRecordFlag) {   //위에 창이 떠있으면 종료
                                RecordScene recordScene = (RecordScene) RecordScene.recordScene;
                                recordScene.finish();
                                Toast.makeText(getApplication(), "시간이 초과되어 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            }else if(isManageFlag) {
                                DBManagePopup dbManagePopup = (DBManagePopup) DBManagePopup.dbManagePopup;
                                dbManagePopup.finish();
                                Toast.makeText(getApplication(), "시간이 초과되어 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            finish();
                        }
                    }
                });
            }
        };
        return timerTask;
    }
    //10이하로 내려갈때 남은자리를 0으로 채움
    private void setTenFormat(int time, TextView textView) {
        if(time < 10) {
            textView.setText("0" + time);
        }else {
            textView.setText(String.valueOf(time));
        }
    }
    //분, 초 합
    private int getMaxTime(int mit, int sec) {
        int sum = 0;
        sum = (mit * 60) + sec;

        return sum;
    }
    //쉬는 시간 끝을 알리는 푸쉬알림
    private void showNotify(String title, String content) {
        String onceNum = "";
        if(numEdit.getText().toString().equals(""))
            onceNum = "0";
        else
            onceNum = numEdit.getText().toString();

        Intent toNotifyIntent = new Intent(getApplication(), MainActivity.class);
        toNotifyIntent.putExtra("MINUTE", defMin);
        toNotifyIntent.putExtra("SECOND", defSec);
        toNotifyIntent.putExtra("SET_NUM", setNum);
        toNotifyIntent.putExtra("ROUTINE", routine);
        toNotifyIntent.putExtra("REPS_PER_SET", strRPS + setNum + ":" + onceNum + ",");

        PendingIntent notifyPendingIntent;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getActivity(getApplication(), 99, toNotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            notifyPendingIntent = PendingIntent.getActivity(getApplication(), 99, toNotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("DEFAULT", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            notificationBuilder = new NotificationCompat.Builder(getApplication(), "DEFAULT");
        }else {
            notificationBuilder = new NotificationCompat.Builder(getApplication());
        }

        notificationBuilder.setSmallIcon(R.drawable.exercise);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(content);
        notificationBuilder.setContentIntent(notifyPendingIntent);
        notificationBuilder.setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sTimerStopBtn:      //정지
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.sTimerReserveBtn:   //예약
                isSRecordFlag = true;       //위에 창이 떠있음
                int[] rpsIntAry = new int[setNum];
                //저장된 임시저장값이 있을 때만 실행
                if(strRPS.length() != 0) {
                    String[] rpsStrAry = strRPS.split(",");
                    //세트별 횟수 임시저장값을 배열의 맞는 위치에 대입
                    for (String str : rpsStrAry) {
                        String[] onceSetNNum = str.split(":");
                        int onceSet = Integer.parseInt(onceSetNNum[0]) - 1;
                        rpsIntAry[onceSet] = Integer.parseInt(onceSetNNum[1]);
                    }
                }

                if(!numEdit.getText().toString().equals(""))
                    rpsIntAry[setNum - 1] = Integer.parseInt(numEdit.getText().toString());

                Intent toSRecordIntent = new Intent(getApplication(), RecordScene.class);
                toSRecordIntent.putExtra("SET_NUM", setNum);
                toSRecordIntent.putExtra("NAME", routine);
                toSRecordIntent.putExtra("REPS_PER_SET", rpsIntAry);
                toSRecordIntent.putExtra("IS_S_TIMER_FLAG", true);

                launcher.launch(toSRecordIntent);
                //예약 버튼 비활성화, 예약취소 버튼 활성화
                reserveBtn.setEnabled(false);
                reserveCancelBtn.setEnabled(true);
                break;
            case R.id.sTimerReserveCancelBtn:   //예약취소
                isReserveFlag = false;
                Toast.makeText(getApplication(), "예약 취소되었습니다.", Toast.LENGTH_SHORT).show();
                //예약 버튼 활성화, 예약취소 버튼 비활성화
                reserveBtn.setEnabled(true);
                reserveCancelBtn.setEnabled(false);
                break;
            case R.id.sTimerManagementBtn:
                isManageFlag = true;       //위에 창이 떠있음
                Intent toManageIntent = new Intent(this, DBManagePopup.class);
                toManageIntent.putExtra("IS_SAVE_FLAG", false);

                launcher.launch(toManageIntent);
                break;
        }
    }
    @Override   //TimeScen 종료 시
    protected void onDestroy() {
        super.onDestroy();
        //시작된 타이머가 있을 시 정지, onStop은 백그라운드에서 실행 불가능하기에
        if(restTimerTask != null) {
            restTimerTask.cancel();
            restTimerTask = null;
        }
    }
}