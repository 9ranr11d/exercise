package com.example.exercise;

import static com.example.exercise.MainActivity.maxSet;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
    private String eSeq = "", popupDate = "";
    private String[] volumeAry = null, numberAry = null;
    private int editSize = MainActivity.DPI * 60;
    private int setNum = 0, texColor = 0, highlightColor = 0, hintColor = 0;

    private EditText eNameTex;
    private EditText[] volumeEdit = new EditText[maxSet], numberEdit = new EditText[maxSet];
    private TextView popupTex, eSetTex;
    private TextView[] slash = new TextView[maxSet];
    private Button okBtn, cancelBtn, deleteBtn, eNameDelBtn, popPBtn, popMBtn, insertBtn;
    private Spinner pTypeSpi;

    private GridLayout popVolNumGridLay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        texColor = ContextCompat.getColor(this, R.color.color_text);
        Log.d(TAG, "text color = " + Integer.toHexString(texColor));
        highlightColor = ContextCompat.getColor(this, R.color.color_primary);
        Log.d(TAG, "highlight color = " + Integer.toHexString(texColor));
        hintColor = ContextCompat.getColor(this, R.color.color_hint);
        Log.d(TAG, "hint text color = " + Integer.toHexString(texColor));
        //TextView
        eNameTex = findViewById(R.id.eNameTex);
        eSetTex = findViewById(R.id.eSetTex);
        popupTex = findViewById(R.id.popupDate);
        //버튼
        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        eNameDelBtn = findViewById(R.id.eNameDelBtn);
        popPBtn = findViewById(R.id.popPBtn);
        popMBtn = findViewById(R.id.popMBtn);
        insertBtn = findViewById(R.id.insertBtn);

        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        eNameDelBtn.setOnClickListener(this);
        popPBtn.setOnClickListener(this);
        popMBtn.setOnClickListener(this);
        insertBtn.setOnClickListener(this);
        //레이아웃
        popVolNumGridLay = findViewById(R.id.popVolnNumGridLay);
        //스피너
        pTypeSpi = findViewById(R.id.pTypeSpi);
        //운동부위, 운동무게의 스피너 목록
        String[] type = getResources().getStringArray(R.array.exerciseType);
        //스피너 테마
        ArrayAdapter pTypeAdapter = new ArrayAdapter(getApplication(), R.layout.spinner_cover, type);
        pTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pTypeSpi.setAdapter(pTypeAdapter);
        //운동 부위 스피너 리스너
        pTypeSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String  eType = "", eName = "",eVolume = "", eNumber = "";  //선택된 기록으로부터 날짜, 부위, 무게, 이름, 세트수 받아옴
        int eSet = 0;
        Intent intent = getIntent();
        popupDate = intent.getStringExtra("select_day");

        popupTex.setText(popupDate);    //받아온 날짜를 제목으로 지정
        //1 : 수정, 삭제, 0 : 추가
        int mode = intent.getIntExtra("mode", 0);
        if(mode == 1) {
            //선택된 항목의 데이터 받아옴
            eSeq = intent.getStringExtra("e_seq");
            eType = intent.getStringExtra("e_type");
            eName = intent.getStringExtra("e_name");
            eSet = intent.getIntExtra("e_set", 0);
            eVolume = intent.getStringExtra("e_volume");
            eNumber = intent.getStringExtra("e_number");
            //받아온 운동 부위 스피너의 선택값
            int pTypeNum = Arrays.asList(type).indexOf(eType);          //받아온 운동부위 배열 type을
                                                                        //리스트형식으로 바꾸고 위치를 찾는다
            pTypeSpi.setSelection(pTypeNum);
            eNameTex.setText(eName);
            setNum = eSet;
            eSetTex.setText(String.valueOf(eSet));
            //,를 기준으로 무게를 잘라서 표시
            volumeAry = eVolume.split(",");
            numberAry = eNumber.split(",");
            //자른 무게를 나누어 Edit에 표시
            for (int i = 0; i < volumeAry.length; i++) {
                volumeEdit[i] = new EditText(getApplication());
                volumeEdit[i].setHint("무게");
                volumeEdit[i].setHintTextColor(hintColor);
                volumeEdit[i].setText(volumeAry[i]);
                volumeEdit[i].setTextColor(texColor);                                   //글자 색
                volumeEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));  //밑줄 색
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    volumeEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));  //커서 색
//                    volumeEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));         //커서 심볼
                }
                volumeEdit[i].setHighlightColor(highlightColor);            //드래그 시 배경색
                volumeEdit[i].setWidth(editSize);
                volumeEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);

                popVolNumGridLay.addView(volumeEdit[i]);

                slash[i] = new TextView(getApplication());
                slash[i].setText((i + 1) + "회");
                slash[i].setTextColor(texColor);

                popVolNumGridLay.addView(slash[i]);

                numberEdit[i] = new EditText(getApplication());
                numberEdit[i].setText(numberAry[i]);
                numberEdit[i].setHint("횟수");
                numberEdit[i].setHintTextColor(hintColor);
                numberEdit[i].setTextColor(texColor);
                numberEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    numberEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                    numberEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
                }
                numberEdit[i].setHighlightColor(highlightColor);
                numberEdit[i].setWidth(editSize);
                numberEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);

                popVolNumGridLay.addView(numberEdit[i]);
            }
            insertBtn.setEnabled(false);        //추가 버튼 비활성화
        }else {                                 //mode가 0일때
            eNameTex.setHint("이름");            //힌트
            eSetTex.setText(String.valueOf(setNum));
            okBtn.setEnabled(false);            //수정 버튼
            deleteBtn.setEnabled(false);        //삭제 버튼 비활성화
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.okBtn:        //수정
                //에딧텍스트에 적힌 데이터를 다시 보냄
                setNum = Integer.parseInt(eSetTex.getText().toString());

                setPopupData(0);
                break;
            case R.id.cancelBtn:    //취소
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.deleteBtn:    //삭제
                DBHelper helper = new DBHelper(getApplication(), "record.db", null, 1);
                if(helper.dataDelete(eSeq)) {
                    MainActivity.exerciseDAO.deleteObj(Integer.parseInt(eSeq));
                    Log.d(TAG, "delete seq = " + eSeq);
                    makeToast("삭제되었습니다.");              //delete
                }else {
                    makeToast("삭제 실패하였습니다.");
                }
                helper.close();

                setResult(2, intent);
                finish();
                break;
            case R.id.eNameDelBtn:  //운동 이름 삭제
                eNameTex.setText("");       //운동 이름 삭제
                eNameTex.requestFocus();    //운동 이름 텍뷰로 포커스
                //포커스 시 키보드 표시
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.showSoftInput(eNameTex, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.popPBtn:      //세트수 추가
                if(setNum < maxSet) {
                    setNum++;
                    eSetTex.setText(String.valueOf(setNum));

                    int i = setNum - 1;

                    volumeEdit[i] = new EditText(getApplication());
                    volumeEdit[i].setHint("무게");
                    volumeEdit[i].setHintTextColor(hintColor);
                    volumeEdit[i].setTextColor(texColor);
                    volumeEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        volumeEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                        volumeEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
                    }
                    volumeEdit[i].setHighlightColor(highlightColor);
                    volumeEdit[i].setWidth(editSize);
                    volumeEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);    //입력값은 오직 숫자만

                    popVolNumGridLay.addView(volumeEdit[i]);                    //선택된 Edit 추가

                    slash[i] = new TextView(getApplication());
                    slash[i].setText((i + 1) + "회");
                    slash[i].setTextColor(texColor);

                    popVolNumGridLay.addView(slash[i]);

                    numberEdit[i] = new EditText(getApplication());
                    numberEdit[i].setHint("횟수");
                    numberEdit[i].setHintTextColor(hintColor);
                    numberEdit[i].setTextColor(texColor);
                    numberEdit[i].setBackgroundTintList(ColorStateList.valueOf(texColor));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        numberEdit[i].setTextCursorDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_view_cursor));
//                    numberEdit[i].setTextSelectHandle(ContextCompat.getDrawable(this, R.drawable.edit_text_view_handle));
                    }
                    numberEdit[i].setHighlightColor(highlightColor);
                    numberEdit[i].setWidth(editSize);
                    numberEdit[i].setInputType(InputType.TYPE_CLASS_NUMBER);

                    popVolNumGridLay.addView(numberEdit[i]);
                }
                break;
            case R.id.popMBtn:      //세트수 감소
                if(setNum > 0) {
                    setNum--;
                    eSetTex.setText(String.valueOf(setNum));
                    popVolNumGridLay.removeView(volumeEdit[setNum]);         //선택된 Edit 삭제
                    popVolNumGridLay.removeView(slash[setNum]);
                    popVolNumGridLay.removeView(numberEdit[setNum]);         //선`택된 Edit 삭제
                }
                break;
            case R.id.insertBtn:    //insert(추가) 버튼
                setPopupData(1);
                break;
        }
    }
    //Edit null 여부 조사
    private boolean checkEmpty(EditText[] checkEdit) {
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
    private void setPopupData(int mode) {
        if(eNameTex.getText().toString().equals("") || eNameTex.getText().toString().equals(null)) {
            Toast.makeText(getApplication(), "운동 이름을 적어주세요", Toast.LENGTH_SHORT).show();
        }else if(setNum == 0) {
            makeToast("세트 수가 없습니다.");
        }else {
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

            String selectType = pTypeSpi.getSelectedItem().toString(),      //선택된 스피너
                    selectName = eNameTex.getText().toString(),             //이름 체크
                    selectVolume = mergeVol.toString(),                  //무게 체크
                    selectNumber = mergeNum.toString();

            Intent intent = new Intent();

            DBHelper helper = new DBHelper(getApplication(), "record.db", null, 1);
            if(mode == 0) { //mode 0 : update
                if (helper.dataUpdate(eSeq, popupDate, selectType, selectName, setNum, selectVolume, selectNumber)) {
                    MainActivity.exerciseDAO.updateObj(Integer.parseInt(eSeq), popupDate, selectType, selectName, setNum, selectVolume, selectNumber);
                    Log.d(TAG, "update seq = " + eSeq);

                    makeToast("수정 되었습니다.");         //update

                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    makeToast("수정 실패하였습니다.");
                }
            }else {         //mode 1 : add
                insertSeq(selectType, selectName, selectVolume, selectNumber);
            }
            helper.close();
        }
    }

    private void insertSeq(String selectType, String selectName, String selectVolume, String selectNumber) {
        Intent intent = new Intent();

        DBHelper helper = new DBHelper(getApplication(), "record.db", null, 1);
        try {
            if (helper.dataInsert(MainActivity.seq, popupDate, selectType, selectName, setNum, selectVolume, selectNumber)) {
                MainActivity.exerciseDAO.insertObj(MainActivity.seq, popupDate, selectType, selectName, setNum, selectVolume, selectNumber);
                Log.d(TAG, "record seq = " + MainActivity.seq);
                MainActivity.seq++;

                makeToast("기록되었습니다.");

                setResult(3, intent);
                finish();
            }
        }catch (SQLiteConstraintException uniqueE) {
            Log.e(TAG, "unique exception");
            Log.e(TAG, "seq = " + MainActivity.seq + " -> " + (MainActivity.lastSeq + 1));
            MainActivity.seq = MainActivity.lastSeq + 1;

            insertSeq(selectType, selectName, selectVolume, selectNumber);
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