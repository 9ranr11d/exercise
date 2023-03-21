package com.example.exercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private int setNum = 0, editSize = MainActivity.dotsPerInch * 60, selectedSpiNum = 0, texColor = 0, highlightColor = 0, hintColor = 0, routine = 0;
    private int[] rpsIntAry;
    private boolean isSTimerFlag = false;

    private GridLayout gridLay;
    private EditText[] volEdit, numEdit;
    private EditText nameEdit;
    private Button recordOkBtn, recordCancelBtn;
    private Spinner typeSpi;

    public static Activity recordScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_scene);

        recordScene = RecordScene.this;

        texColor = ContextCompat.getColor(this, R.color.color_text);
        highlightColor = ContextCompat.getColor(this, R.color.color_primary);
        hintColor = ContextCompat.getColor(this, R.color.color_hint);

        Log.i(TAG, "text color = " + Integer.toHexString(texColor));
        Log.i(TAG, "highlight color = " + Integer.toHexString(texColor));
        Log.i(TAG, "hint text color = " + Integer.toHexString(texColor));
        //레이아웃
        gridLay = findViewById(R.id.sRecordGridLay);
        //버튼
        recordOkBtn = findViewById(R.id.sRecordOkBtn);
        recordCancelBtn = findViewById(R.id.sRecordCncBtn);

        recordOkBtn.setOnClickListener(this);
        recordCancelBtn.setOnClickListener(this);
        //Edit
        nameEdit = findViewById(R.id.sRecordNameEdit);
        //스피너
        typeSpi = findViewById(R.id.sRecordTypeSpi);
        //스피너 목록
        String[] typeStrAry = getResources().getStringArray(R.array.exerciseType);
        //운동 부위 스피너 테마
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplication(), R.layout.spinner_cover, typeStrAry);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpi.setAdapter(arrayAdapter);
        //운동 부위 스피너 리스너
        typeSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpiNum = i;    //선택된 운동 부위 스피너의 번호
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        typeSpi.setSelection(selectedSpiNum);
        //세트 수를 받아옴
        Intent recdSRecordIntent = getIntent();
        setNum = recdSRecordIntent.getIntExtra("SET_NUM", 0);
        routine = recdSRecordIntent.getIntExtra("NAME", 1);
        isSTimerFlag = recdSRecordIntent.getBooleanExtra("IS_S_TIMER_FLAG", false);
        rpsIntAry = new int[setNum];
        rpsIntAry = recdSRecordIntent.getIntArrayExtra("REPS_PER_SET");

        Log.i(TAG, "receive set num from timer = " + setNum);
        Log.i(TAG, "receive routine num from timer = " + routine);
        Log.i(TAG, "receive reps per set from timer = " + Arrays.toString(rpsIntAry));

        nameEdit.setText("routine" + routine);
        //세트 수만큼 무게 입력 Edit을 만듦
        volEdit = new EditText[setNum];
        numEdit = new EditText[setNum];
        for(int i = 0; i < setNum; i++) {
            volEdit[i] = new EditText(getApplication());
            volEdit[i].setHint("무게");               //각 Edit에 해당 세트 번호를 띄움
            volEdit[i].setHintTextColor(hintColor);
            volEdit[i].setTextColor(texColor);
            volEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
            volEdit[i].setHighlightColor(highlightColor);
            volEdit[i].setWidth(editSize);
            volEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);    //Edit에 숫자만 입력 가능
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                volEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                    numberEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
            }

            gridLay.addView(volEdit[i]);

            TextView slash = new TextView(getApplication());
            slash.setText((i + 1) + "회");
            slash.setTextColor(texColor);

            gridLay.addView(slash);

            numEdit[i] = new EditText(getApplication());
            //임시저장값이 없으면 생략
            if(rpsIntAry[i] != 0)
                numEdit[i].setText(String.valueOf(rpsIntAry[i]));

            numEdit[i].setHint("횟수");               //각 Edit에 해당 세트 번호를 띄움
            numEdit[i].setHintTextColor(hintColor);
            numEdit[i].setTextColor(texColor);
            numEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
            numEdit[i].setHighlightColor(highlightColor);
            numEdit[i].setWidth(editSize);
            numEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);    //Edit에 숫자만 입력 가능
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                numEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                    numberEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
            }

            gridLay.addView(numEdit[i]);
        }
    }
    //EditText null값 여부 조사
    private boolean checkEmpty(EditText[] chkEdit) {
        for(int i = 0; i < chkEdit.length; i++) {
            if(chkEdit[i].getText().toString().equals("") || chkEdit[i].getText().toString().equals(null))
                return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sRecordOkBtn:      //기록
                StringBuilder volBuilder = new StringBuilder(), numBuilder = new StringBuilder();
                if(checkEmpty(volEdit) && checkEmpty(numEdit)) {
                    for (int i = 0; i < setNum; i++) {
                        volBuilder.append(volEdit[i].getText());
                        numBuilder.append(numEdit[i].getText());
                        if (i != setNum - 1) {
                            volBuilder.append(",");                   //입력받은 모든 무게, 횟수 Edit을 ,를 구분점으로 합침
                            numBuilder.append(",");
                        }
                    }
                }else {
                    for (int i = 0; i < setNum; i++) {
                        if(volEdit[i].getText().toString().equals("") || volEdit[i].getText().toString().equals(null))
                            volBuilder.append("0");
                        else
                            volBuilder.append(volEdit[i].getText());

                        if(numEdit[i].getText().toString().equals("") || numEdit[i].getText().toString().equals(null))
                            numBuilder.append("0");
                        else
                            numBuilder.append(numEdit[i].getText());

                        if (i != setNum - 1) {
                            volBuilder.append(",");                   //입력받은 모든 무게, 횟수 Edit을 ,를 구분점으로 합침
                            numBuilder.append(",");
                        }
                    }
                }
                Intent backIntent = new Intent();
                backIntent.putExtra("VOLUME", volBuilder.toString()); //합친 무게, 횟수 String을 돌려줌
                backIntent.putExtra("NUMBER", numBuilder.toString());
                backIntent.putExtra("TYPE", typeSpi.getSelectedItem().toString());
                backIntent.putExtra("NAME", nameEdit.getText().toString());

                setResult(2, backIntent);
                finish();
                break;
            case R.id.sRecordCncBtn:     //취소
                setResult(RESULT_CANCELED);
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
        if(isSTimerFlag)
            TimerScene.isSRecordFlag = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();         //현재 포커스하고 있는 뷰 가져오기
        if(focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);   //포커스 있는 뷰를 영역으로 지정
            int x = (int) ev.getX(), y = (int) ev.getY();
            if(!rect.contains(x, y)) {              //클릭한 위치가 영역내에 없으면 실행?
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}