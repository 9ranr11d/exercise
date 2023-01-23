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

    private TextView setTex, minuteTex, secondTex;
    private Button setPlusBtn, setMinusBtn, timeStartBtn, timeStopBtn, sWResetBtn;

    private Timer timer;
    private TimerTask restTimerTask = null;
    private SoundPool soundPool;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stopwatch_menu, container, false);

        setTex = v.findViewById(R.id.setTex);
        minuteTex = v.findViewById(R.id.minuteTex);
        secondTex = v.findViewById(R.id.secondTex);

        setPlusBtn = v.findViewById(R.id.setPlusBtn);
        setMinusBtn = v.findViewById(R.id.setMinusBtn);
        timeStartBtn = v.findViewById(R.id.timeStartBtn);
        timeStopBtn = v.findViewById(R.id.timeStopBtn);
        sWResetBtn = v.findViewById(R.id.sWResetBtn);
        //버튼 클릭 리스너
        setPlusBtn.setOnClickListener(this);
        setMinusBtn.setOnClickListener(this);
        timeStartBtn.setOnClickListener(this);
        timeStopBtn.setOnClickListener(this);
        sWResetBtn.setOnClickListener(this);
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
        timeStopBtn.setEnabled(false);

        return v;
    }
    //버튼 클릭 리스너
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setPlusBtn:   //세트수 증가
                setTex.setText(String.valueOf(++setNum));
                break;
            case R.id.setMinusBtn:  //세트수 감소
                if(setNum > 0)
                    setTex.setText(String.valueOf(--setNum));
                break;
            case R.id.timeStartBtn: //타이머 시작
                //타이머 초기값을 0으로 지정
                second = 0;
                minute = 0;
                //시작된 타이머가 없을 때 타이머 시작
                if(restTimerTask == null) {
                    restTimerTask = createTimerTask();
                    timer.schedule(restTimerTask, 0, 1000);
                }
                //정지 버튼 활성화, 시작 버튼 비활성화
                timeStopBtn.setEnabled(true);
                timeStartBtn.setEnabled(false);
                break;
            case R.id.timeStopBtn:  //타이머 정지
                //타이머가 있을 때 타이머 정지 후 비우기
                if(restTimerTask != null) {
                    restTimerTask.cancel();
                    restTimerTask = null;
                }
                //타이머 표기값을 0으로 지정
                secondTex.setText("00");
                minuteTex.setText("00");
                //세트수 증가 및 표시
                setNum += 1;
                setTex.setText(String.valueOf(setNum));
                //정지 버튼 비활성화, 시작 버튼 활성화
                timeStopBtn.setEnabled(false);
                timeStartBtn.setEnabled(true);
                break;
            case R.id.sWResetBtn:   //초기화 버튼
                //시작된 타이머 있다면 정지
                if(restTimerTask != null) {
                    restTimerTask.cancel();
                    restTimerTask = null;
                }
                //타이머 표기값을 0으로 지정
                secondTex.setText("00");
                minuteTex.setText("00");
                //세트수 초기화
                setNum = 0;
                setTex.setText(String.valueOf(setNum));
                //정지 버튼 비활성화, 시작 버튼 활성화
                timeStopBtn.setEnabled(false);
                timeStartBtn.setEnabled(true);
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
                        if(second < 59) {   //59초 미만일땐 1초 증가
                            second++;
                        }else {             //60초일때 1분 증가
                            second = 0;
                            minute++;
                        }
                        //초나 분이 10초 미만일때 빈자리를 0으로 채움
                        if(second < 10) {
                            secondTex.setText("0" + String.valueOf(second));
                        }else {
                            secondTex.setText(String.valueOf(second));
                        }
                        if(minute < 10) {
                            minuteTex.setText("0" + String.valueOf(minute));
                        }else {
                            minuteTex.setText(String.valueOf(minute));
                        }
                    }
                });
            }
        };
        return timerTask;
    }
}