package com.example.exercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
//기록 메뉴
public class RecordMenu extends Fragment implements View.OnClickListener {
    private static final String TAG = "RecordMenu";
    private String sTempDate = "";
    private String tenFormatDate = "";
    private int selectYear = 0, selectMonth = 0, selectDay = 0, selectDateColor = 0, todayColor = 0;

    private Button dataManagementBtn;

    private MaterialCalendarView mCalendarView;
    private CalendarDay selectedDay;
    private ActivityResultLauncher<Intent> launcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record_menu, container, false);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            updateLay();
        });
        //색상
        selectDateColor = ContextCompat.getColor(getContext(), R.color.white);  //오늘날짜
        todayColor = ContextCompat.getColor(getContext(), R.color.lightColor);
        //버튼
        dataManagementBtn = v.findViewById(R.id.dataManagementBtn);

        dataManagementBtn.setOnClickListener(this);
        //캘런더뷰
        mCalendarView = v.findViewById(R.id.mCalendarView);

        mCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.months))); //요일 한글로
        mCalendarView.setTitleFormatter(new TitleFormatter() {  //년도, 월 표시형식 변환 ex)2022 07
            @Override
            public CharSequence format(CalendarDay day) {
                LocalDate inputText = day.getDate();
                String[] calendarHeaderElements = inputText.toString().split("-");
                StringBuilder calendarHeaderBuilder = new StringBuilder();
                calendarHeaderBuilder.append(calendarHeaderElements[0]).append(" ").append(calendarHeaderElements[1]);

                return calendarHeaderBuilder.toString();
            }
        });
        mCalendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.weekdays))); //요일 한글로
        selectedDay = CalendarDay.today();  //오늘 날짜 초기값
        mCalendarView.setSelectedDate(CalendarDay.today()); //오늘 날짜를 선택

        sTempDate = selectedDay.toString().replace("CalendarDay{","").replace("}", "");
        //오늘 날짜를 년, 월, 일로 나누어 초기값을 지정
        String[] splitTemp = sTempDate.split("-");
        selectYear = Integer.parseInt(splitTemp[0]);
        selectMonth = Integer.parseInt(splitTemp[1]);
        selectDay = Integer.parseInt(splitTemp[2]);
        tenFormatDate = setDateFormat(selectYear, selectMonth, selectDay);

        updateLay();

        return v;
    }
    //새로고침
    private void updateLay() {
        setSpan();
        //날짜 선택 리스너
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDay = date;
                setSpan();
                sTempDate = date.toString().replace("CalendarDay{","").replace("}", "");
                String[] mTempDateArray = sTempDate.split("-");
                //선택된 날을 년, 월, 일로 나누어 선택된 날에 삽입
                selectYear = Integer.parseInt(mTempDateArray[0]);
                selectMonth = Integer.parseInt(mTempDateArray[1]);
                selectDay = Integer.parseInt(mTempDateArray[2]);

                tenFormatDate = setDateFormat(selectYear, selectMonth, selectDay);
                Log.d(TAG, "select day = " + tenFormatDate);
                Intent sendCalIntent = new Intent(getActivity(), CalendarScene.class);
                sendCalIntent.putExtra("selectDay", tenFormatDate);

                launcher.launch(sendCalIntent);
            }
        });
    }
    //받은 년, 월, 일을 빈자리를 0으로 채워 하나로 만듦
    private String setDateFormat(int fYear, int fMonth, int fDay) {
        String str = "", tempMstr = "", tempDstr = "";

        tempMstr = tenNumFormat(fMonth);
        tempDstr = tenNumFormat(fDay);

        str = fYear + "-" + tempMstr + "-" + tempDstr;

        return str;
    }

    private String tenNumFormat(int target) {
        String result = "";

        if(target < 10)
            result = "0" + target;
        else
            result = String.valueOf(target);

        return result;
    }
    //점, 오늘날짜 글자색, 선택한 날짜색
    private void setSpan() {
        mCalendarView.removeDecorators();   //모든 Decorator 삭제
        //오늘 날짜 글자색
        mCalendarView.addDecorator(new EventDecorator(todayColor,
                    Collections.singleton(CalendarDay.today()), 2));
        //선택한 날짜 글자색
        mCalendarView.addDecorator(new EventDecorator(selectDateColor,
                Collections.singleton(selectedDay), 2));
        //Strings.xml로 부터 색상과 부위 배열을 가져옴
        String[] typeAry = getResources().getStringArray(R.array.exerciseType);
        int[] colorAry = getResources().getIntArray(R.array.exerTypeColor);
        //distDate set에 중복 없게 날짜를 저장, dateType에는 날짜랑 부위를 짝지어서 저장
        HashSet<String> distDate = new HashSet<>();
        ArrayList<String> dateType = new ArrayList<>();
        for(Exercise exer : MainActivity.exerciseDAO.eList) {
            distDate.add(exer.geteDate());
            dateType.add(exer.geteDate() + ":" + exer.geteType());
        }
        //날짜별로 부위의 빈도수를 구하여 높은 부위별로 색상을 다르게 함
        for(String date : distDate) {
            ArrayList<Integer> typeList = new ArrayList<>();
            for(int i = 0; i < typeAry.length; i++) {
                typeList.add(Collections.frequency(dateType, date + ":" + typeAry[i]));     //부위별 빈도수를 typeList에 저장
            }
            Log.d(TAG, "date type frequency = " + date + typeList);
            int colorNum = typeList.indexOf(Collections.max(typeList));                           //typeList의 최빈수의 위치를 저장

            Log.d(TAG, date + " color = " + Integer.toHexString(colorAry[colorNum]));

            String[] markingDate = date.split("-");

            int markingYear = Integer.parseInt(markingDate[0]);
            int markingMonth = Integer.parseInt(markingDate[1]);
            int markingDay = Integer.parseInt(markingDate[2]);

            mCalendarView.addDecorator(new EventDecorator(colorAry[colorNum],
                    Collections.singleton(CalendarDay.from(markingYear, markingMonth, markingDay)), 1));
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.dataManagementBtn:            //기록 관리 팝업으로
                Intent dataManagementIntent = new Intent(getActivity(), DBManagePopup.class);

                launcher.launch(dataManagementIntent);
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //프레그먼트 변환시 캘린더 초기화를 위해
        mCalendarView.clearSelection();
        mCalendarView.setSelectedDate(CalendarDay.today());
    }
}