package com.example.exercise;

import static com.example.exercise.MainActivity.maxSet;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
//운동 데이터 추가, 수정, 삭제 팝업
public class PopupActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "PopupActivity";
    private String seq = "", date = "";
    private String[] volStrAry = null, numStrAry = null;
    private int editSize = MainActivity.dotsPerInch * 60;
    private int setNum = 0, texColor = 0, highlightColor = 0, hintColor = 0;
    private boolean isAddFlag = true;

    private EditText nameTex;
    private EditText[] volEdit = new EditText[maxSet], numEdit = new EditText[maxSet];
    private TextView dateTex, setTex;
    private TextView[] slash = new TextView[maxSet];
    private Button okBtn, cancelBtn, delBtn, nameDelBtn, plusBtn, minusBtn;
    private Spinner typeSpi;
    private GridLayout gridLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        texColor = ContextCompat.getColor(this, R.color.color_text);
        highlightColor = ContextCompat.getColor(this, R.color.color_primary);
        hintColor = ContextCompat.getColor(this, R.color.color_hint);

        Log.i(TAG, "text color = " + Integer.toHexString(texColor));
        Log.i(TAG, "highlight color = " + Integer.toHexString(highlightColor));
        Log.i(TAG, "hint text color = " + Integer.toHexString(texColor));
        //텍스트뷰
        nameTex = findViewById(R.id.popupNameTex);
        setTex = findViewById(R.id.popupSetTex);
        dateTex = findViewById(R.id.popupDate);

        nameTex.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //버튼
        okBtn = findViewById(R.id.popupOkBtn);
        cancelBtn = findViewById(R.id.popupCancelBtn);
        delBtn = findViewById(R.id.popupDelBtn);
        nameDelBtn = findViewById(R.id.popupNameDelBtn);
        plusBtn = findViewById(R.id.popupPlusBtn);
        minusBtn = findViewById(R.id.popupMinusBtn);

        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        delBtn.setOnClickListener(this);
        nameDelBtn.setOnClickListener(this);
        plusBtn.setOnClickListener(this);
        minusBtn.setOnClickListener(this);
        //레이아웃
        gridLay = findViewById(R.id.popupGridLay);
        //스피너
        typeSpi = findViewById(R.id.popupTypeSpi);
        //운동부위, 운동무게의 스피너 목록
        String[] typeStrAry = getResources().getStringArray(R.array.exerciseType);
        //스피너 테마
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplication(), R.layout.spinner_cover, typeStrAry);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpi.setAdapter(arrayAdapter);
        //운동 부위 스피너 리스너
        typeSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String  type = "", name = "",volume = "", number = "";
        Intent recdPopupIntent = getIntent();
        date = recdPopupIntent.getStringExtra("SELECTED_DATE");
        dateTex.setText(date);    //받아온 날짜를 제목으로 지정
        if(recdPopupIntent.getBooleanExtra("IS_ONLY_READ", false)) {
            okBtn.setEnabled(false);
            delBtn.setEnabled(false);
            plusBtn.setEnabled(false);
            minusBtn.setEnabled(false);
            nameDelBtn.setEnabled(false);
            nameTex.setEnabled(false);
            typeSpi.setEnabled(false);
        }else {
            okBtn.setEnabled(true);
            delBtn.setEnabled(true);
            plusBtn.setEnabled(true);
            minusBtn.setEnabled(true);
            nameDelBtn.setEnabled(true);
            nameTex.setEnabled(true);
            typeSpi.setEnabled(true);
        }
        //1 : 수정, 삭제, 0 : 추가
        isAddFlag = recdPopupIntent.getBooleanExtra("IS_ADD_FLAG", true);
        if(!isAddFlag) {
            okBtn.setText("수정");
            //선택된 항목의 데이터 받아옴
            seq = recdPopupIntent.getStringExtra("SEQ");
            type = recdPopupIntent.getStringExtra("TYPE");
            name = recdPopupIntent.getStringExtra("NAME");
            setNum = recdPopupIntent.getIntExtra("SET_NUM", 0);
            volume = recdPopupIntent.getStringExtra("VOLUME");
            number = recdPopupIntent.getStringExtra("NUMBER");
            //받아온 운동 부위 스피너의 선택값
            int typeSpiNum = Arrays.asList(typeStrAry).indexOf(type);          //받아온 운동부위 배열 type을 리스트형식으로 바꾸고 위치를 찾는다
            typeSpi.setSelection(typeSpiNum);
            nameTex.setText(name);
            setTex.setText(String.valueOf(setNum));
            //,를 기준으로 무게를 잘라서 표시
            volStrAry = volume.split(",");
            numStrAry = number.split(",");
            //자른 무게를 나누어 Edit에 표시
            for (int i = 0; i < volStrAry.length; i++) {
                volEdit[i] = new EditText(getApplication());
                volEdit[i].setHint("무게");
                volEdit[i].setHintTextColor(hintColor);
                volEdit[i].setText(volStrAry[i]);
                volEdit[i].setTextColor(texColor);                                   //글자 색
                volEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));  //밑줄 색
                volEdit[i].setHighlightColor(highlightColor);            //드래그 시 배경색
                volEdit[i].setWidth(editSize);
                volEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                volEdit[i].setImeOptions(EditorInfo.IME_ACTION_DONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    volEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));  //커서 색
                    volEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.image_text_handle));         //커서 심볼
                }

                gridLay.addView(volEdit[i]);

                slash[i] = new TextView(getApplication());
                slash[i].setText((i + 1) + "회");
                slash[i].setTextColor(texColor);

                gridLay.addView(slash[i]);

                numEdit[i] = new EditText(getApplication());
                numEdit[i].setText(numStrAry[i]);
                numEdit[i].setHint("횟수");
                numEdit[i].setHintTextColor(hintColor);
                numEdit[i].setTextColor(texColor);
                numEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
                numEdit[i].setHighlightColor(highlightColor);
                numEdit[i].setWidth(editSize);
                numEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                numEdit[i].setImeOptions(EditorInfo.IME_ACTION_DONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    numEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
                    numEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.image_text_handle));
                }

                gridLay.addView(numEdit[i]);
            }
        }else {
            okBtn.setText("추가");
            nameTex.setHint("이름");             //힌트
            setTex.setText(String.valueOf(setNum));
            delBtn.setEnabled(false);           //삭제 버튼 비활성화
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.popupOkBtn:           //isAddFlag true : 추가, isAddFlag false : 수정
                if(isAddFlag)
                    insertRecord(1);
                else {
                    setNum = Integer.parseInt(setTex.getText().toString());     //에딧텍스트에 적힌 데이터를 다시 보냄
                    insertRecord(0);
                }
                break;
            case R.id.popupCancelBtn:       //취소
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.popupDelBtn:          //삭제
                DBHelper helper = new DBHelper(getApplication(), "record.db", null, 1);
                if(helper.dataDelete(seq)) {
                    Log.i(TAG, "delete seq = " + seq);
                    MainActivity.exerciseDAO.deleteObj(Integer.parseInt(seq));
                    makeToast("삭제되었습니다.");              //delete
                }else
                    makeToast("삭제 실패하였습니다.");

                helper.close();

                setResult(2, intent);
                finish();
                break;
            case R.id.popupNameDelBtn:     //운동 이름 삭제
                nameTex.setText("");       //운동 이름 삭제
                nameTex.requestFocus();    //운동 이름 텍뷰로 포커스
                //포커스 시 키보드 표시
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(nameTex, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.popupPlusBtn:        //세트수 추가
                if(setNum < maxSet) {
                    setNum++;
                    setTex.setText(String.valueOf(setNum));

                    int i = setNum - 1;

                    volEdit[i] = new EditText(getApplication());
                    volEdit[i].setHint("무게");
                    volEdit[i].setHintTextColor(hintColor);
                    volEdit[i].setTextColor(texColor);
                    volEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
                    volEdit[i].setHighlightColor(highlightColor);
                    volEdit[i].setWidth(editSize);
                    volEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);    //입력값은 오직 숫자만
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        volEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                        volumeEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
                    }

                    gridLay.addView(volEdit[i]);                    //선택된 Edit 추가

                    slash[i] = new TextView(getApplication());
                    slash[i].setText((i + 1) + "회");
                    slash[i].setTextColor(texColor);

                    gridLay.addView(slash[i]);

                    numEdit[i] = new EditText(getApplication());
                    numEdit[i].setHint("횟수");
                    numEdit[i].setHintTextColor(hintColor);
                    numEdit[i].setTextColor(texColor);
                    numEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
                    numEdit[i].setHighlightColor(highlightColor);
                    numEdit[i].setWidth(editSize);
                    numEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        numEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                    numberEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
                    }

                    gridLay.addView(numEdit[i]);
                }
                break;
            case R.id.popupMinusBtn:      //세트수 감소
                if(setNum > 0) {
                    setNum--;
                    setTex.setText(String.valueOf(setNum));
                    gridLay.removeView(volEdit[setNum]);         //선택된 Edit 삭제
                    gridLay.removeView(slash[setNum]);
                    gridLay.removeView(numEdit[setNum]);         //선택된 Edit 삭제
                }
                break;
        }
    }
    //Edit null 여부 조사
    private boolean chkEmpty(EditText[] checkEdit) {
        for(int i = 0; i < setNum; i++) {
            if(checkEdit[i].getText().toString().equals("") || checkEdit[i].getText().toString().equals(null)) {
                return false;
            }
        }
        return true;
    }
    //토스트
    private void makeToast(String str) {
        Toast.makeText(getApplication(), str, Toast.LENGTH_SHORT).show();
    }
    //수정, 추가
    private void insertRecord(int mode) {
        if(nameTex.getText().toString().equals("") || nameTex.getText().toString().equals(null))
            Toast.makeText(getApplication(), "운동 이름을 적어주세요", Toast.LENGTH_SHORT).show();
        else if(setNum == 0)
            makeToast("세트 수가 없습니다.");
        else {
            StringBuilder volBuilder = new StringBuilder(), numBuilder = new StringBuilder();

            if(chkEmpty(volEdit) && chkEmpty(numEdit)) {
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

            String selectedType = typeSpi.getSelectedItem().toString(),      //선택된 스피너
                    selectedName = nameTex.getText().toString(),             //이름 체크
                    selectedVol = volBuilder.toString(),                     //무게 체크
                    selectedNum = numBuilder.toString();

            DBHelper helper = new DBHelper(getApplication(), "record.db", null, 1);
            if(mode == 0) { //mode 0 : update
                if (helper.dataUpdate(seq, date, selectedType, selectedName, setNum, selectedVol, selectedNum)) {
                    Log.i(TAG, "update seq = " + seq);
                    MainActivity.exerciseDAO.updateObj(Integer.parseInt(seq), date, selectedType, selectedName, setNum, selectedVol, selectedNum);

                    makeToast("수정 되었습니다.");

                    Intent backIntent = new Intent();
                    setResult(RESULT_OK, backIntent);
                    finish();
                } else
                    makeToast("수정 실패하였습니다.");

            }else           //mode 1 : add
                checkException(selectedType, selectedName, selectedVol, selectedNum);

            helper.close();
        }
    }

    private void checkException(String type, String name, String volume, String number) {
        DBHelper helper = new DBHelper(getApplication(), "record.db", null, 1);
        try {
            if (helper.dataInsert(MainActivity.seq, date, type, name, setNum, volume, number)) {
                Log.d(TAG, "record seq = " + MainActivity.seq);
                MainActivity.exerciseDAO.insertObj(MainActivity.seq, date, type, name, setNum, volume, number);
                MainActivity.seq++;

                makeToast("기록되었습니다.");

                Intent backIntent = new Intent();
                setResult(3, backIntent);
                finish();
            }
        }catch (SQLiteConstraintException uniqueE) {
            Log.e(TAG, "unique exception");
            Log.d(TAG, "seq = " + MainActivity.seq + " -> " + (MainActivity.lastSeq + 1));
            MainActivity.seq = MainActivity.lastSeq + 1;
            checkException(type, name, volume, number);      //다른 예외는 없는지 재귀함수
        }catch (Exception e) {
            e.printStackTrace();
            makeToast("예기치 못한 오류입니다");
        }
        helper.close();
    }
    //화면 밖에 선택시 취소를 false
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }
}