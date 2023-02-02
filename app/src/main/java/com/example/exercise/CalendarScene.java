package com.example.exercise;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CalendarScene extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CalendarScene";
    private String selectDay = "";
    private int backColor = 0, texColor = 0;    //backColor : background color, texColor : text color

    private TextView cSTitleTex;
    private Button cSAddBtn, cSTopBtn, cSCloseBtn, exerciseBtn;
    private ScrollView scrollView;
    private GridLayout gridLay;

    private ActivityResultLauncher<Intent> calLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_scene);

        calLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_CANCELED) {  //취소
                makeToast("취소되었습니다.");  //화면 바깥을 클릭 시에도 RESULT_CANCELED이 옴
            }
            lookupDate(selectDay);
        });
        //레이아웃
        gridLay = findViewById(R.id.gridLay);

        scrollView = findViewById(R.id.scrollView);
        //텍스트뷰
        cSTitleTex = findViewById(R.id.cSTitleTex);
        //버튼
        cSAddBtn = findViewById(R.id.cSAddBtn);
        cSTopBtn = findViewById(R.id.cSTopBtn);
        cSCloseBtn = findViewById(R.id.cSCloseBtn);

        cSAddBtn.setOnClickListener(this);
        cSTopBtn.setOnClickListener(this);
        cSCloseBtn.setOnClickListener(this);
        //선택한 날짜 받아옴
        Intent receiveCalIntent = getIntent();
        selectDay = receiveCalIntent.getStringExtra("selectDay");

        cSTitleTex.setText(selectDay);

        backColor = ContextCompat.getColor(this, R.color.color_primary);
        Log.d(TAG, "background color = " + Integer.toHexString(backColor));
        texColor = ContextCompat.getColor(this, R.color.color_text);
        Log.d(TAG, "text color = " + Integer.toHexString(texColor));

        lookupDate(selectDay);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cSAddBtn:     //운동 추가
                Intent addIntent = new Intent(getApplication(), PopupActivity.class);
                addIntent.putExtra("mode", 0);
                addIntent.putExtra("select_day", selectDay);    //선택된 날짜를 포함

                calLauncher.launch(addIntent);
                break;
            case R.id.cSTopBtn:     //스크롤 최상단 포커스
                scrollView.fullScroll(View.FOCUS_UP);   //스크롤뷰 맨 위로
                break;
            case R.id.cSCloseBtn:   //닫기
                finish();
                break;
        }
    }
    //기록 조회
    public void lookupDate(String date) {
        gridLay.removeAllViews();   //기록 모두 지움
        //선택 날짜 검색
        ArrayList<Exercise> rSDL = MainActivity.exerciseDAO.searchDateObj(date);
        //버튼 사이즈 (DP단위)
        int exerBtnSize = MainActivity.DPI * 130;
        Log.d(TAG, "exerBtn size = " + exerBtnSize + "px");

        if(rSDL.size() == 0)
            makeToast("기록이 없습니다.");
        else {
            for (int i = 0; i < rSDL.size(); i++) {
                //운동부위, 운동이름, 세트수, 무게/횟수
                String tempSeq = String.valueOf(rSDL.get(i).getSeq()),
                        tempType = rSDL.get(i).geteType(),
                        tempName = rSDL.get(i).geteName(),
                        tempVolume = rSDL.get(i).geteVolume(),
                        tempNumber = rSDL.get(i).geteNumber();
                int tempSet = rSDL.get(i).getSetN();

                StringBuilder btnStr = new StringBuilder();
                btnStr.append(tempType).append("\n")
                        .append(tempName).append("\n")
                        .append(tempSet).append("\n")
                        .append(MainActivity.stringFormat(tempVolume, tempNumber));

                exerciseBtn = new Button(getApplication());
                exerciseBtn.setText(btnStr.toString());
                exerciseBtn.setTextColor(texColor);                 //글자 색
                exerciseBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background));   //버튼 백그라운드
                exerciseBtn.setMaxLines(4);                         //4줄까지만 띄우도록 설정
                exerciseBtn.setEllipsize(TextUtils.TruncateAt.END); //정해진 길이가 넘어가면 ...로 띄우게 설정
                exerciseBtn.setWidth(exerBtnSize);                  //pixel이 아닌 dp단위로 사이즈 설정

                gridLay.addView(exerciseBtn);
                //버튼 클릭시 해당 정보를 들고 PopupActivity로 이동
                exerciseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplication(), PopupActivity.class);
                        intent.putExtra("mode", 1);
                        intent.putExtra("e_seq", tempSeq);
                        intent.putExtra("select_day", selectDay);
                        intent.putExtra("e_type", tempType);
                        intent.putExtra("e_name", tempName);
                        intent.putExtra("e_set", tempSet);
                        intent.putExtra("e_volume", tempVolume);
                        intent.putExtra("e_number", tempNumber);
                        Log.d(TAG, "select btn seq = " + tempSeq);

                        calLauncher.launch(intent);
                    }
                });
            }
        }
    }
    //토스트
    private void makeToast(String str) {
        Toast.makeText(getApplication(), str, Toast.LENGTH_SHORT).show();
    }
}