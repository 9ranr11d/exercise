package com.example.exercise;

import static com.example.exercise.MainActivity.MaxSetNum;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
//타이머 메뉴
public class TimerMenu extends Fragment implements View.OnClickListener {
    private final String TAG = "TimerMenu";
    private int timeMNum = 1, timeSNum = 0, setNum = 0, routine = 0;
    private long now;
    private Date nowDate;
    private StringBuilder repsPerSet = new StringBuilder();

    private TextView setTex;
    private NumberPicker minuteNumPick, secondNumPick;
    private Button setPlusBtn, setMinusBtn, timeStartBtn, dbSaveBtn, tResetBtn;

    private final SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd");

    private ActivityResultLauncher<Intent> timerLauncher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer_menu, container, false);

        timerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent getTScenIntent = result.getData();
            if(result.getResultCode() == Activity.RESULT_OK) {
                setNum = getTScenIntent.getIntExtra("set_n", 0);        //TimerScen으로 부터 세트 수 받아옴
                Log.d(TAG, "set num = " + setNum);
                setSetNum(setNum);
                //세트별 횟수 임시저장값
                int tRPS = Integer.parseInt(getTScenIntent.getStringExtra("reps_per_set"));
                Log.d(TAG, "reps per set = " + tRPS);

                repsPerSet.append(setNum).append(":").append(tRPS).append(",");
            }
            else if(result.getResultCode() == Activity.RESULT_CANCELED) {
                makeToast("취소되었습니다.");
            }else if(result.getResultCode() == 2) {
                setNum = getTScenIntent.getIntExtra("set_n", setNum);
                String timerVol = getTScenIntent.getStringExtra("volume");         //TimerScen에서 RecordScen으로 받아온 것을 받아옴
                String timerNum = getTScenIntent.getStringExtra("number");
                String timerType = getTScenIntent.getStringExtra("type");
                String timerName = getTScenIntent.getStringExtra("name");
                //저장
                tInsertSeq(getTime(), timerType, timerName, timerVol, timerNum);

                routine++;
            }
        });
        //textView
        setTex = v.findViewById(R.id.setTex);
        //NumberPicker
        minuteNumPick = v.findViewById(R.id.minuteNumPick);
        secondNumPick = v.findViewById(R.id.secondNumPick);

        minuteNumPick.setMaxValue(60);
        minuteNumPick.setMinValue(0);
        secondNumPick.setMaxValue(59);
        secondNumPick.setMinValue(0);

        minuteNumPick.setValue(1);
        //Btn
        setPlusBtn = v.findViewById(R.id.setPlusBtn);
        setMinusBtn = v.findViewById(R.id.setMinusBtn);
        timeStartBtn = v.findViewById(R.id.timeStartBtn);
        tResetBtn = v.findViewById(R.id.tResetBtn);
        dbSaveBtn = v.findViewById(R.id.dbSaveBtn);

        setPlusBtn.setOnClickListener(this);
        setMinusBtn.setOnClickListener(this);
        timeStartBtn.setOnClickListener(this);
        tResetBtn.setOnClickListener(this);
        dbSaveBtn.setOnClickListener(this);
        //세이브 데이터 받음
        timeSNum = this.getArguments().getInt("timeSNum");
        timeMNum = this.getArguments().getInt("timeMNum");
        setNum = this.getArguments().getInt("setNum");
        routine = this.getArguments().getInt("routine");
        //다른 프레그먼트로 넘어 갔다 오더라도 세트 개수 유지
        if(setNum == 0)
            setNum = MainActivity.setNum;
        //세이브 데이터 적용
        minuteNumPick.setValue(timeMNum);
        secondNumPick.setValue(timeSNum);
        setSetNum(setNum);

        return v;
    }
    //버튼 클릭 리스너
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setPlusBtn:   //세트수 증가
                if(setNum < MaxSetNum)
                    setTex.setText(String.valueOf(++setNum));
                break;
            case R.id.setMinusBtn:  //세트수 감소
                if(setNum > 0)      //세트수가 0 이상일 때만 감소
                    setTex.setText(String.valueOf(--setNum));
                break;
            case R.id.timeStartBtn: //타이머 시작
                Intent timerIntent = new Intent(getActivity(), TimerScene.class);
                timerIntent.putExtra("minute", minuteNumPick.getValue());
                timerIntent.putExtra("second", secondNumPick.getValue());
                timerIntent.putExtra("set_n", setNum);
                timerIntent.putExtra("routine", routine);
                timerIntent.putExtra("reps_per_set", repsPerSet.toString());

                timerLauncher.launch(timerIntent);
                break;
            case R.id.dbSaveBtn:    //기록
                if(setNum != 0) {   //세트수가 없으면 기록 되지 않음
                    int[] iRPS = new int[setNum];
                    //저장된 임시저장값이 있을 때만 실행
                    if(repsPerSet.length() != 0) {
                        String[] tRPS = repsPerSet.toString().split(",");

                        //세트별 횟수 임시저장값을 배열의 맞는 위치에 대입
                        for (String str : tRPS) {
                            String[] tStr = str.split(":");
                            int tInt = Integer.parseInt(tStr[0]) - 1;
                            iRPS[tInt] = Integer.parseInt(tStr[1]);
                        }
                        Log.d(TAG, "reps per set = " + Arrays.toString(iRPS));
                    }

                    Intent recordIntent = new Intent(getActivity(), RecordScene.class);
                    recordIntent.putExtra("set_n", setNum);
                    recordIntent.putExtra("eName", routine);
                    recordIntent.putExtra("reps_per_set", iRPS);


                    timerLauncher.launch(recordIntent);
                }else {
                    makeToast("세트 수가 없습니다.");
                }
                break;
            case R.id.tResetBtn:    //초기화
                //시간을 초기값으로
                timeMNum = 1;
                timeSNum = 0;
                minuteNumPick.setValue(timeMNum);
                secondNumPick.setValue(timeSNum);
                //세트수 초기화
                setNum = 0;
                setSetNum(setNum);
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
    }

    private void tInsertSeq(String selectDate, String selectType, String selectName, String selectVolume, String selectNumber) {
        DBHelper helper = new DBHelper(getActivity(), "record.db", null, 1);
        try {
            if (helper.dataInsert(MainActivity.seq, selectDate, selectType, selectName, setNum, selectVolume, selectNumber)) {
                MainActivity.exerciseDAO.insertObj(MainActivity.seq, selectDate, selectType, selectName, setNum, selectVolume, selectNumber);
                Log.d(TAG, "record seq = " + MainActivity.seq);
                MainActivity.seq++;

                makeToast(selectName + " (으)로 기록되었습니다.");

                setNum = 0;
                setSetNum(setNum);
            }
        }catch (SQLiteConstraintException uniqueE) {
            Log.e(TAG, "unique exception");
            Log.e(TAG, "seq = " + MainActivity.seq + " -> " + (MainActivity.lastSeq + 1));
            MainActivity.seq = MainActivity.lastSeq + 1;

            tInsertSeq(selectDate, selectType, selectName, selectVolume, selectNumber);
        }catch (Exception e) {
            e.printStackTrace();
            makeToast("예기치 못한 오류입니다");
        }
        helper.close();
    }
}