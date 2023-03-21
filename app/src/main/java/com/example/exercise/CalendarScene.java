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
//해당 날짜의 기록 조회
public class CalendarScene extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CalendarScene";
    private String selectedDate = "";
    private int backgroundColor = 0, texColor = 0, recordBtnSize = MainActivity.dotsPerInch * 130;

    private TextView titleTex;
    private Button addBtn, topBtn, closeBtn, recordBtn;
    private ScrollView scroll;
    private GridLayout gridLay;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_scene);

        backgroundColor = ContextCompat.getColor(this, R.color.color_primary);    //배경색 가져오기
        texColor = ContextCompat.getColor(this, R.color.color_text);        //글자색 가져오기

        Log.i(TAG, "background color = " + Integer.toHexString(backgroundColor));
        Log.i(TAG, "text color = " + Integer.toHexString(texColor));
        Log.i(TAG, "record btn size = " + recordBtnSize + "px");
        //런처 처리
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_CANCELED)    //취소
                makeToast("취소되었습니다.");

            inquiryRecord(selectedDate);
        });
        //레이아웃
        gridLay = findViewById(R.id.sCalendarGridLay);
        //스크롤뷰
        scroll = findViewById(R.id.sCalendarScroll);
        //텍스트뷰
        titleTex = findViewById(R.id.sCalendarTitleBtn);
        //버튼
        addBtn = findViewById(R.id.sCalendarAddBtn);
        topBtn = findViewById(R.id.sCalendarTopBtn);
        closeBtn = findViewById(R.id.sCalendarCloseBtn);

        addBtn.setOnClickListener(this);
        topBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        //선택한 날짜 받아와서 처리
        Intent recdCalendarIntent = getIntent();
        selectedDate = recdCalendarIntent.getStringExtra("SELECTED_DATE");
        titleTex.setText(selectedDate);
        Log.i(TAG, "selected date from RecordMenu = " + selectedDate);
        //기록조회
        inquiryRecord(selectedDate);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sCalendarAddBtn:     //운동 추가
                Intent addIntent = new Intent(getApplication(), PopupActivity.class);
                addIntent.putExtra("IS_ADD_FLAG", true);
                addIntent.putExtra("IS_ONLY_READ", false);
                addIntent.putExtra("SELECTED_DATE", selectedDate);    //선택된 날짜를 포함

                launcher.launch(addIntent);
                break;
            case R.id.sCalendarTopBtn:     //포커스 최상단
                scroll.fullScroll(View.FOCUS_UP);                 //스크롤뷰 맨 위로
                break;
            case R.id.sCalendarCloseBtn:   //닫기
                finish();
                break;
        }
    }
    //기록 조회
    private void inquiryRecord(String date) {
        gridLay.removeAllViews();   //기록 모두 지움
        //선택 날짜의 모든 기록 담아옴
        ArrayList<Exercise> allRecordList = MainActivity.exerciseDAO.searchDateObj(date);

        if(allRecordList.size() == 0)
            makeToast("기록이 없습니다.");
        else {
            for (int i = 0; i < allRecordList.size(); i++) {
                String tempSeq = String.valueOf(allRecordList.get(i).getSeq()),  //고유번호
                        tempType = allRecordList.get(i).getType(),               //운동부위
                        tempName = allRecordList.get(i).getName(),               //운동이름
                        tempVol = allRecordList.get(i).getVolume(),           //무게
                        tempNum = allRecordList.get(i).getNumber();           //횟수
                int tempSet = allRecordList.get(i).getSetNum();                  //세트 수
                //기록 조회 버튼의 내용 (운동 부위, 운동이름, 무게/횟수)
                StringBuilder recordCoverBuilder = new StringBuilder();
                recordCoverBuilder.append(tempType).append("\n")
                        .append(tempName).append("\n")
                        .append(tempSet).append("\n")
                        .append(MainActivity.setStrFormat(tempVol, tempNum));

                recordBtn = new Button(getApplication());
                recordBtn.setText(recordCoverBuilder.toString());     //버튼의 내용
                recordBtn.setTextColor(texColor);                 //글자 색
                recordBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background));   //버튼 백그라운드
                recordBtn.setMaxLines(4);                         //4줄까지만 띄우도록 설정
                recordBtn.setEllipsize(TextUtils.TruncateAt.END); //정해진 길이가 넘어가면 ...로 띄우게 설정
                recordBtn.setWidth(recordBtnSize);                  //pixel이 아닌 dp단위로 사이즈 설정

                gridLay.addView(recordBtn);
                //버튼 클릭시 해당 정보를 들고 PopupActivity로 이동
                recordBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "select date from CalendarScene" + tempSeq);

                        Intent toPopupIntent = new Intent(getApplication(), PopupActivity.class);
                        toPopupIntent.putExtra("IS_ADD_FLAG", false);
                        toPopupIntent.putExtra("IS_ONLY_READ", false);
                        toPopupIntent.putExtra("SEQ", tempSeq);
                        toPopupIntent.putExtra("SELECTED_DATE", selectedDate);
                        toPopupIntent.putExtra("TYPE", tempType);
                        toPopupIntent.putExtra("NAME", tempName);
                        toPopupIntent.putExtra("SET_NUM", tempSet);
                        toPopupIntent.putExtra("VOLUME", tempVol);
                        toPopupIntent.putExtra("NUMBER", tempNum);

                        launcher.launch(toPopupIntent);
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