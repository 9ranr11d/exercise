package com.example.exercise;

import static com.example.exercise.MainActivity.maxSet;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
//타이머 메뉴
public class TimerMenu extends Fragment implements View.OnClickListener {
    private final String TAG = "TimerMenu";
    private int minute = 0, second = 0, setNum = 0, routine = 0;
    private boolean isVisitFlag = true;
    private long now;
    private Date nowDate;
    private StringBuilder rpsBuilder = new StringBuilder();

    private final SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd");

    private TextView setTex;
    private NumberPicker minPick, secPick;
    private Button plusBtn, minusBtn, startBtn, saveBtn, resetBtn;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer_menu, container, false);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent launcherIntent = result.getData();
            if(result.getResultCode() == Activity.RESULT_OK) {      //쉬는 시간 끝
                setNum = launcherIntent.getIntExtra("SET_NUM", 0);        //TimerScen으로 부터 세트 수 받아옴
                setSetNum(setNum);
                //세트별 횟수 임시저장값
                String recdRPS = launcherIntent.getStringExtra("REPS_PER_SET");
                rpsBuilder.append(setNum).append(":");

                if(recdRPS.equals(""))
                    rpsBuilder.append("0");
                else {
                    int strToIntRPS = Integer.parseInt(recdRPS);
                    Log.i(TAG, "receive RPS from TimerScene = " + strToIntRPS);
                    rpsBuilder.append(strToIntRPS);
                }

                rpsBuilder.append(",");
            }
            else if(result.getResultCode() == Activity.RESULT_CANCELED)
                makeToast("취소되었습니다.");
            else if(result.getResultCode() == 2) {                 //기록 예약
                setNum = launcherIntent.getIntExtra("SET_NUM", setNum);
                String timerVol = launcherIntent.getStringExtra("VOLUME");         //TimerScen에서 RecordScen으로 받아온 것을 받아옴
                String timerNum = launcherIntent.getStringExtra("NUMBER");
                String timerType = launcherIntent.getStringExtra("TYPE");
                String timerName = launcherIntent.getStringExtra("NAME");
                //저장
                insertSeq(getTime(), timerType, timerName, timerVol, timerNum);

                rpsBuilder = new StringBuilder();
                routine++;
            }
        });
        //텍스트뷰
        setTex = v.findViewById(R.id.timerSetTex);
        //넘버픽커
        minPick = v.findViewById(R.id.timerMinPick);
        secPick = v.findViewById(R.id.timerSecPick);

        minPick.setMaxValue(60);
        minPick.setMinValue(0);
        secPick.setMaxValue(59);
        secPick.setMinValue(0);
        //버튼
        plusBtn = v.findViewById(R.id.timerPlusBtn);
        minusBtn = v.findViewById(R.id.timerMinusBtn);
        startBtn = v.findViewById(R.id.timerStartBtn);
        resetBtn = v.findViewById(R.id.timerResetBtn);
        saveBtn = v.findViewById(R.id.timerSaveBtn);

        plusBtn.setOnClickListener(this);
        minusBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        //다른 프레그먼트로 넘어 갔다 오더라도 세트 개수, 쉬는시간 유지
        setNum = MainActivity.setNum;
        minute = MainActivity.defTime / 60;
        second = MainActivity.defTime % 60;

        if(isVisitFlag) {
            isVisitFlag = false;      //처음에만 데이터를 받기 위해
            //세이브 데이터 받음
            minute = this.getArguments().getInt("MINUTE");
            second = this.getArguments().getInt("SECOND");
            setNum = this.getArguments().getInt("SET_NUM");
            routine = this.getArguments().getInt("ROUTINE");
            String tempStrRPS = this.getArguments().getString("REPS_PER_SET");
            if(!(tempStrRPS == null))
                rpsBuilder.append(tempStrRPS);
            Log.i(TAG, "receive RPS from MainActivity = " + rpsBuilder.toString());
        }

        //세이브 데이터 적용
        minPick.setValue(minute);
        secPick.setValue(second);
        setSetNum(setNum);

        return v;
    }
    //버튼 클릭 리스너
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.timerPlusBtn:   //세트수 증가
                if(setNum < maxSet)
                    setTex.setText(String.valueOf(++setNum));
                break;
            case R.id.timerMinusBtn:  //세트수 감소
                if(setNum > 0)        //세트수가 0 이상일 때만 감소
                    setTex.setText(String.valueOf(--setNum));
                break;
            case R.id.timerStartBtn: //타이머 시작
                Intent toSTimerIntent = new Intent(getActivity(), TimerScene.class);
                toSTimerIntent.putExtra("MINUTE", minPick.getValue());
                toSTimerIntent.putExtra("SECOND", secPick.getValue());
                toSTimerIntent.putExtra("SET_NUM", setNum);
                toSTimerIntent.putExtra("ROUTINE", routine);
                toSTimerIntent.putExtra("REPS_PER_SET", rpsBuilder.toString());

                launcher.launch(toSTimerIntent);
                break;
            case R.id.timerSaveBtn:     //기록
                if(setNum != 0) {       //세트수가 없으면 기록 되지 않음
                    int[] rpsIntAry = new int[setNum];
                    //저장된 임시저장값이 있을 때만 실행
                    if(!rpsBuilder.toString().isEmpty()) {
                        String[] rpsStrAry = rpsBuilder.toString().split(",");
                        //세트별 횟수 임시저장값을 배열의 맞는 위치에 대입
                        for (String str : rpsStrAry) {
                            String[] onceSetNNum = str.split(":");
                            int onceSet = Integer.parseInt(onceSetNNum[0]);
                            //임시저장된 값의 크기와 세트 수가 맞지 않을 때를 방지
                            if(onceSet <= setNum) {
                                int tempInt = onceSet - 1;
                                rpsIntAry[tempInt] = Integer.parseInt(onceSetNNum[1]);
                            }
                        }
                    }

                    Intent toSRecordIntent = new Intent(getActivity(), RecordScene.class);
                    toSRecordIntent.putExtra("SET_NUM", setNum);
                    toSRecordIntent.putExtra("NAME", routine);
                    toSRecordIntent.putExtra("REPS_PER_SET", rpsIntAry);

                    launcher.launch(toSRecordIntent);
                }else
                    makeToast("세트 수가 없습니다.");

                break;
            case R.id.timerResetBtn:    //초기화
                //시간을 초기값으로
                minute = MainActivity.defTime / 60;
                second = MainActivity.defTime % 60;
                minPick.setValue(minute);
                secPick.setValue(second);
                //세트수 초기화
                setNum = 0;
                setSetNum(setNum);
                //횟수 임시저장값 초기화
                rpsBuilder = new StringBuilder();
                break;
        }
    }
    //토스트
    private void makeToast(String str) {
        Toast.makeText(getActivity() , str, Toast.LENGTH_SHORT).show();
    }
    //현재시간
    private String getTime() {
        now = System.currentTimeMillis();
        nowDate = new Date(now);

        return nowFormat.format(nowDate);
    }

    private void setSetNum(int setNum) {
        setTex.setText(String.valueOf(setNum));
    }
    @Override
    public void onStop() {
        super.onStop();
        MainActivity.setNum = setNum;
        MainActivity.defTime = (minPick.getValue() * 60) + secPick.getValue();
    }

    private void insertSeq(String date, String type, String name, String volume, String number) {
        DBHelper helper = new DBHelper(getActivity(), "record.db", null, 1);
        try {
            if (helper.dataInsert(MainActivity.seq, date, type, name, setNum, volume, number)) {
                Log.i(TAG, "record seq = " + MainActivity.seq);
                MainActivity.exerciseDAO.insertObj(MainActivity.seq, date, type, name, setNum, volume, number);
                MainActivity.seq++;

                makeToast(name + " (으)로 기록되었습니다.");

                setNum = 0;
                setSetNum(setNum);
            }
        }catch (SQLiteConstraintException uniqueE) {
            Log.e(TAG, "unique exception");
            Log.d(TAG, "seq = " + MainActivity.seq + " -> " + (MainActivity.lastSeq + 1));
            MainActivity.seq = MainActivity.lastSeq + 1;

            insertSeq(date, type, name, volume, number);
        }catch (Exception e) {
            e.printStackTrace();
            makeToast("예기치 못한 오류입니다");
        }
        helper.close();
    }
}