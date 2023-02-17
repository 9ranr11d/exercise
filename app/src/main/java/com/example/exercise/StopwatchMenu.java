package com.example.exercise;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
//스탑워치 메뉴
public class StopwatchMenu extends Fragment implements View.OnClickListener {
    private int setNum = 0, second = 0, minute = 0, sound = 0;

    private TextView setTex, minTex, secTex;
    private Button plusBtn, minusBtn, startBtn, stopBtn, resetBtn;

    private Timer timer;
    private TimerTask restTimerTask = null;
    private SoundPool soundPool;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stopwatch_menu, container, false);
        //텍스트뷰
        setTex = v.findViewById(R.id.stopwatchSetTex);
        minTex = v.findViewById(R.id.stopwatchMinTex);
        secTex = v.findViewById(R.id.stopwatchSecTex);
        //버튼
        plusBtn = v.findViewById(R.id.stopwatchPlusBtn);
        minusBtn = v.findViewById(R.id.stopwatchMinusBtn);
        startBtn = v.findViewById(R.id.stopwatchStartBtn);
        stopBtn = v.findViewById(R.id.stopwatchStopBtn);
        resetBtn = v.findViewById(R.id.stopwatchResetBtn);

        plusBtn.setOnClickListener(this);
        minusBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        //소리
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            //USAGE_MEDIA : 볼륨을 미디어 권한으로
            soundPool = new SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build();
        }else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(getActivity(), R.raw.one_second, 1);     //sound에 one_second라는 mp3파일 저장
        //정지 버튼 비활성화
        stopBtn.setEnabled(false);

        return v;
    }
    //버튼 클릭 리스너
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stopwatchPlusBtn:   //세트수 증가
                setTex.setText(String.valueOf(++setNum));
                break;
            case R.id.stopwatchMinusBtn:  //세트수 감소
                if(setNum > 0)
                    setTex.setText(String.valueOf(--setNum));
                break;
            case R.id.stopwatchStartBtn: //타이머 시작
                //타이머 초기값을 0으로 지정
                second = 0;
                minute = 0;
                //시작된 타이머가 없을 때 타이머 시작
                if(restTimerTask == null) {
                    restTimerTask = createTimerTask();
                    timer.schedule(restTimerTask, 0, 1000);
                }
                //정지 버튼 활성화, 시작 버튼 비활성화
                stopBtn.setEnabled(true);
                startBtn.setEnabled(false);
                break;
            case R.id.stopwatchStopBtn:  //타이머 정지
                //타이머가 있을 때 타이머 정지 후 비우기
                if(restTimerTask != null) {
                    restTimerTask.cancel();
                    restTimerTask = null;
                }
                //타이머 표기값을 0으로 지정
                secTex.setText("00");
                minTex.setText("00");
                //세트수 증가 및 표시
                setNum += 1;
                setTex.setText(String.valueOf(setNum));
                //정지 버튼 비활성화, 시작 버튼 활성화
                stopBtn.setEnabled(false);
                startBtn.setEnabled(true);
                break;
            case R.id.stopwatchResetBtn:   //초기화 버튼
                //시작된 타이머 있다면 정지
                if(restTimerTask != null) {
                    restTimerTask.cancel();
                    restTimerTask = null;
                }
                //타이머 표기값을 0으로 지정
                secTex.setText("00");
                minTex.setText("00");
                //세트수 초기화
                setNum = 0;
                setTex.setText(String.valueOf(setNum));
                //정지 버튼 비활성화, 시작 버튼 활성화
                stopBtn.setEnabled(false);
                startBtn.setEnabled(true);
                break;
        }
    }
    //타이머 작동 시 루틴
    private TimerTask createTimerTask() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {                             //타이머를 메인스레드 X 새로운 스레드 O
                getActivity().runOnUiThread(new Runnable() {     //UI 변경은 메인스레드에서
                    @Override
                    public void run() {
                        soundPool.play(sound, 1, 1, 0, 0, 1);   //초마다 재생
                        if(second < 59)    //59초 미만일땐 1초 증가
                            second++;
                        else {             //60초일때 1분 증가
                            second = 0;
                            minute++;
                        }
                        //초나 분이 10초 미만일때 빈자리를 0으로 채움
                        if(second < 10)
                            secTex.setText("0" + second);
                        else
                            secTex.setText(String.valueOf(second));

                        if(minute < 10)
                            minTex.setText("0" + minute);
                        else
                            minTex.setText(String.valueOf(minute));
                    }
                });
            }
        };
        return timerTask;
    }
}