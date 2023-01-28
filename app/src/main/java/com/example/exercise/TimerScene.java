package com.example.exercise;

import static com.example.exercise.MainActivity.MaxSetNum;

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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class TimerScene extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "TimerScene";
    private int minute = 0, second = 0, setNum = 0, timeMNum = 0, timeSNum = 0, routine = 0, tSceneMode = 0;
    private String repsPerSet = "";

    private TextView minuteTex, secondTex, timerSetTex, numTex;
    private EditText numEdit;
    private Button timeStopBtn, reservationBtn, reserCancelBtn;
    private ProgressBar ringProgressBar;

    private Timer timer;
    private TimerTask restTimerTask = null;
    private ActivityResultLauncher<Intent> tSceneLauncher;

    private Intent successIntent = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_scene);

        tSceneLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent getTRecordIntent = result.getData();
            if(result.getResultCode() == 2) {
                String timerVol = getTRecordIntent.getStringExtra("volume");        //RecordScen으로 부터
                String timerNum = getTRecordIntent.getStringExtra("number");
                String timerType = getTRecordIntent.getStringExtra("type");
                String timerName = getTRecordIntent.getStringExtra("name");

                successIntent.putExtra("volume", timerVol);
                successIntent.putExtra("number", timerNum);
                successIntent.putExtra("type", timerType);
                successIntent.putExtra("name", timerName);

                Toast.makeText(getApplication(), "기록이 예약되었습니다.", Toast.LENGTH_SHORT).show();

                tSceneMode = 1;      //1 : 예약, 0 : 예약 취소
            }else if(result.getResultCode() == RESULT_CANCELED) {
                reservationBtn.setEnabled(true);
                reserCancelBtn.setEnabled(false);
            }
        });
        //텍스트뷰
        minuteTex = findViewById(R.id.minuteTex);
        secondTex = findViewById(R.id.secondTex);
        timerSetTex = findViewById(R.id.timerSetTex);
        numTex = findViewById(R.id.numTex);
        //Edit
        numEdit = findViewById(R.id.numEdit);

        numEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);  //키보드 내려가게
        //버튼
        timeStopBtn = findViewById(R.id.timeStopBtn);
        reservationBtn = findViewById(R.id.reservationBtn);
        reserCancelBtn = findViewById(R.id.reserCancelBtn);

        timeStopBtn.setOnClickListener(this);
        reservationBtn.setOnClickListener(this);
        reserCancelBtn.setOnClickListener(this);

        reserCancelBtn.setEnabled(false);
        //프레스바
        ringProgressBar = findViewById(R.id.ringProgressBar);
        //TimeMenu로부터
        Intent getTimerIntent = getIntent();
        timeMNum = getTimerIntent.getIntExtra("minute", 1);
        timeSNum = getTimerIntent.getIntExtra("second", 0);
        setNum = getTimerIntent.getIntExtra("set_n", 0);
        routine = getTimerIntent.getIntExtra("routine", 1);
        repsPerSet = getTimerIntent.getStringExtra("reps_per_set");

        minute = timeMNum;
        second = timeSNum;
        //프레스바
        ringProgressBar.setMax(getMaxTime(minute, second));
        ringProgressBar.setMin(0);
        ringProgressBar.setProgress(getMaxTime(minute, second));

        timerSetTex.setText(String.valueOf(++setNum));
        numTex.setText(setNum + "회째 : ");
        //타이머 시작
        restTimerTask = createTimerTask();
        timer.schedule(restTimerTask, 0, 1000);
    }

    private TimerTask createTimerTask() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {                                 //타이머를 메인스레드 X 새로운 스레드 O
                runOnUiThread(new Runnable() {    //UI 변경은 메인스레드에서
                    @Override
                    public void run() {
                        if(second != 0) {   //초가 0이 아닐 시 감소
                            second--;
                        }else if(minute != 0) { //분이 0이 아닐시 초, 분 감소
                            second = 59;
                            minute--;
                        }
                        //분, 시 한자리 일때 빈자리 0으로 채움
                        tenTimeFormat(second, secondTex);
                        tenTimeFormat(minute, minuteTex);
                        ringProgressBar.setProgress(getMaxTime(minute, second));
                        //분, 초가 0되면 종료
                        if(minute == 0 && second == 0) {
                            timer.cancel();

                            tenTimeFormat(second, secondTex);
                            tenTimeFormat(minute, minuteTex);

                            if(setNum > MaxSetNum) {
                                setNum = MaxSetNum;
                                Toast.makeText(getApplication(), MaxSetNum + "세트가 넘어서 카운트가 되지 않습니다.", Toast.LENGTH_SHORT).show();;
                            }

                            successIntent.putExtra("set_n", setNum);
                            Log.d(TAG, "setNum = " + setNum);

                            if(tSceneMode == 0) {   //예약 X
                                String tRPS = numEdit.getText().toString();
                                successIntent.putExtra("reps_per_set", tRPS);
                                Log.d(TAG, "reps per set = " + tRPS);

                                setResult(RESULT_OK, successIntent);
                            }
                            else                    //예약 O
                                setResult(2, successIntent);

                            showNoti(TAG, "쉬는 시간 끝");

                            finish();
                        }
                    }
                });
            }
        };
        return timerTask;
    }
    //10이하로 내려갈때 남은자리를 0으로 채움
    private void tenTimeFormat(int time, TextView textView) {
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
    private void showNoti(String title, String content) {
        Intent notiIntent = new Intent(getApplication(), MainActivity.class);
        notiIntent.putExtra("timeMNum", timeMNum);
        notiIntent.putExtra("timeSNum", timeSNum);
        notiIntent.putExtra("setNum", setNum);
        notiIntent.putExtra("routine", routine);

        PendingIntent notiPendingIntent;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notiPendingIntent = PendingIntent.getActivity(getApplication(), 99, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            notiPendingIntent = PendingIntent.getActivity(getApplication(), 99, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            notificationBuilder = new NotificationCompat.Builder(getApplication(), "default");
        }else {
            notificationBuilder = new NotificationCompat.Builder(getApplication());
        }

        notificationBuilder.setSmallIcon(R.drawable.exercise);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(content);
        notificationBuilder.setContentIntent(notiPendingIntent);
        notificationBuilder.setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onClick(View view) {
        Intent timerIntent;
        switch (view.getId()) {
            case R.id.timeStopBtn:      //정지
                timerIntent = new Intent();
                setResult(RESULT_CANCELED, timerIntent);
                finish();
                break;
            case R.id.reservationBtn:   //예약
                int[] iRPS = new int[setNum];
                //저장된 임시저장값이 있을 때만 실행
                if(repsPerSet.length() != 0) {
                    String[] tRPS = repsPerSet.split(",");
                    //세트별 횟수 임시저장값을 배열의 맞는 위치에 대입
                    for (String str : tRPS) {
                        String[] tStr = str.split(":");
                        int tInt = Integer.parseInt(tStr[0]) - 1;
                        iRPS[tInt] = Integer.parseInt(tStr[1]);
                    }
                    Log.d(TAG, "reps per set = " + Arrays.toString(iRPS));
                }
                Intent recordIntent = new Intent(getApplication(), RecordScene.class);
                recordIntent.putExtra("set_n", setNum);
                recordIntent.putExtra("eName", routine);
                recordIntent.putExtra("reps_per_set", iRPS);

                tSceneLauncher.launch(recordIntent);
                //예약 버튼 비활성화, 예약취소 버튼 활성화
                reservationBtn.setEnabled(false);
                reserCancelBtn.setEnabled(true);
                break;
            case R.id.reserCancelBtn:   //예약취소
                tSceneMode = 0;
                Toast.makeText(getApplication(), "예약 취소되었습니다.", Toast.LENGTH_SHORT).show();
                //예약 버튼 활성화, 예약취소 버튼 비활성화
                reservationBtn.setEnabled(true);
                reserCancelBtn.setEnabled(false);
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