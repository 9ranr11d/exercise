package com.example.exercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

public class RecordScene extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "RecordScene";
    private int setNum = 0, editSize = MainActivity.DPI * 60, selSpinner = 0, texColor = 0, highlightColor = 0, hintColor = 0, routine = 0;
    private int[] repsPerSet;

    private GridLayout reVolnNumGridLay;

    private EditText[] volumeEdit, numberEdit;
    private EditText eNameEdit;
    private Button recordOkBtn, recordCalBtn;
    private Spinner eTypeSpi;

    public static Activity recordScene;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_scene);

        recordScene = RecordScene.this;

        texColor = ContextCompat.getColor(this, R.color.color_text);
        Log.d(TAG, "text color = " + Integer.toHexString(texColor));
        highlightColor = ContextCompat.getColor(this, R.color.color_primary);
        Log.d(TAG, "highlight color = " + Integer.toHexString(texColor));
        hintColor = ContextCompat.getColor(this, R.color.color_hint);
        Log.d(TAG, "hint text color = " + Integer.toHexString(texColor));
        //레이아웃
        reVolnNumGridLay = findViewById(R.id.reVolnNumGridLay);
        //Btn
        recordOkBtn = findViewById(R.id.recordOkBtn);
        recordCalBtn = findViewById(R.id.recordCanBtn);

        recordOkBtn.setOnClickListener(this);
        recordCalBtn.setOnClickListener(this);
        //Edit
        eNameEdit = findViewById(R.id.eNameEdit);
        //스피너
        eTypeSpi = findViewById(R.id.eTypeSpi);
        //스피너 목록
        String[] eType = getResources().getStringArray(R.array.exerciseType);
        //운동 부위 스피너 테마
        ArrayAdapter eTypeAdapter = new ArrayAdapter(getApplication(), R.layout.spinner_cover, eType);
        eTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eTypeSpi.setAdapter(eTypeAdapter);
        //운동 부위 스피너 리스너
        eTypeSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selSpinner = i;    //선택된 운동 부위 스피너의 번호
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        eTypeSpi.setSelection(selSpinner);
        //세트 수를 받아옴
        Intent getRecordIntent = getIntent();
        setNum = getRecordIntent.getIntExtra("set_n", 0);
        Log.d(TAG, "set num = " + setNum);
        routine = getRecordIntent.getIntExtra("eName", 1);
        Log.d(TAG, "routine nume = " + routine);
        repsPerSet = new int[setNum];
        repsPerSet = getRecordIntent.getIntArrayExtra("reps_per_set");
        Log.d(TAG, "reps per set = " + Arrays.toString(repsPerSet));

        eNameEdit.setText("routine" + routine);
        //세트 수만큼 무게 입력 Edit을 만듦
        volumeEdit = new EditText[setNum];
        numberEdit = new EditText[setNum];
        for(int i = 0; i < setNum; i++) {
            volumeEdit[i] = new EditText(getApplication());
            volumeEdit[i].setHint("무게");               //각 Edit에 해당 세트 번호를 띄움
            volumeEdit[i].setHintTextColor(hintColor);
            volumeEdit[i].setTextColor(texColor);
            volumeEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                volumeEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                    numberEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
            }
            volumeEdit[i].setHighlightColor(highlightColor);
            volumeEdit[i].setWidth(editSize);
            volumeEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);    //Edit에 숫자만 입력 가능

            reVolnNumGridLay.addView(volumeEdit[i]);

            TextView slash = new TextView(getApplication());
            slash.setText((i + 1) + "회");
            slash.setTextColor(texColor);

            reVolnNumGridLay.addView(slash);

            numberEdit[i] = new EditText(getApplication());
            //임시저장값이 없으면 생략
            if(repsPerSet[i] != 0)
                numberEdit[i].setText(String.valueOf(repsPerSet[i]));

            numberEdit[i].setHint("횟수");               //각 Edit에 해당 세트 번호를 띄움
            numberEdit[i].setHintTextColor(hintColor);
            numberEdit[i].setTextColor(texColor);
            numberEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
            //커서, 핸들러
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                numberEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                    numberEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
            }

            numberEdit[i].setHighlightColor(highlightColor);
            numberEdit[i].setWidth(editSize);
            numberEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);    //Edit에 숫자만 입력 가능

            reVolnNumGridLay.addView(numberEdit[i]);
        }
    }
    //EditText null값 여부 조사
    private boolean checkEmpty(EditText[] checkEdit) {
        for(int i = 0; i < checkEdit.length; i++) {
            if(checkEdit[i].getText().toString().equals("") || checkEdit[i].getText().toString().equals(null)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        Intent reRecordIntent = new Intent();
        switch (view.getId()) {
            case R.id.recordOkBtn:      //기록
                StringBuilder mergeVol = new StringBuilder(), mergeNum = new StringBuilder();
                if(checkEmpty(volumeEdit) && checkEmpty(numberEdit)) {
                    for (int i = 0; i < setNum; i++) {
                        mergeVol.append(volumeEdit[i].getText());
                        mergeNum.append(numberEdit[i].getText());
                        if (i != setNum - 1) {
                            mergeVol.append(",");                   //입력받은 모든 무게, 횟수 Edit을 ,를 구분점으로 합침
                            mergeNum.append(",");
                        }
                    }
                }else {
                    for (int i = 0; i < setNum; i++) {
                        if(volumeEdit[i].getText().toString().equals("") || volumeEdit[i].getText().toString().equals(null))
                            mergeVol.append("0");
                        else
                            mergeVol.append(volumeEdit[i].getText());

                        if(numberEdit[i].getText().toString().equals("") || numberEdit[i].getText().toString().equals(null))
                            mergeNum.append("0");
                        else
                            mergeNum.append(numberEdit[i].getText());

                        if (i != setNum - 1) {
                            mergeVol.append(",");                   //입력받은 모든 무게, 횟수 Edit을 ,를 구분점으로 합침
                            mergeNum.append(",");
                        }
                    }
                }
                reRecordIntent.putExtra("volume", mergeVol.toString()); //합친 무게, 횟수 String을 돌려줌
                reRecordIntent.putExtra("number", mergeNum.toString());
                reRecordIntent.putExtra("type", eTypeSpi.getSelectedItem().toString());
                reRecordIntent.putExtra("name", eNameEdit.getText().toString());

                setResult(2, reRecordIntent);
                finish();
                break;
            case R.id.recordCanBtn:     //취소
                setResult(RESULT_CANCELED, reRecordIntent);
                finish();
                break;
        }
    }
    //화면 밖에 선택시 취소를 false
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerScene.rSFlag = false;
    }
}