package com.example.exercise;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
//기록 메뉴
public class RecordMenu extends Fragment implements View.OnClickListener {
    private static final String TAG = "RecordMenu";
    private String date = "";
    private String tenFormatDate = "";
    private int selectedYear = 0, selectedMonth = 0, selectedDay = 0, selectedDateColor = 0, todayColor = 0;

    private Button dataManagementBtn;
    private MaterialCalendarView materialCalendarView;

    private CalendarDay selectedCalendar;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record_menu, container, false);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            updateLay();    //새로고침
        });
        //색상
        selectedDateColor = ContextCompat.getColor(getContext(), R.color.white);  //오늘날짜
        todayColor = ContextCompat.getColor(getContext(), R.color.lightColor);

        Log.i(TAG, "selected date color = " + Integer.toHexString(selectedDateColor));
        Log.i(TAG, "today color = " + Integer.toHexString(todayColor));
        //버튼
        dataManagementBtn = v.findViewById(R.id.recordManagementBtn);

        dataManagementBtn.setOnClickListener(this);
        //캘런더뷰
        materialCalendarView = v.findViewById(R.id.mCalendarView);

        materialCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.months))); //요일 한글로
        materialCalendarView.setTitleFormatter(new TitleFormatter() {  //년도, 월 표시형식 변환 ex)2022 07
            @Override
            public CharSequence format(CalendarDay day) {
                LocalDate inputText = day.getDate();
                String[] calendarHeaderElements = inputText.toString().split("-");
                StringBuilder calendarHeaderBuilder = new StringBuilder();
                calendarHeaderBuilder.append(calendarHeaderElements[0]).append(" ").append(calendarHeaderElements[1]);

                return calendarHeaderBuilder.toString();
            }
        });
        materialCalendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.weekdays))); //요일 한글로
        selectedCalendar = CalendarDay.today();  //오늘 날짜 초기값
        materialCalendarView.setSelectedDate(CalendarDay.today()); //오늘 날짜를 선택

        date = selectedCalendar.toString().replace("CalendarDay{","").replace("}", "");
        //오늘 날짜를 년, 월, 일로 나누어 초기값을 지정
        String[] splitTemp = date.split("-");
        selectedYear = Integer.parseInt(splitTemp[0]);
        selectedMonth = Integer.parseInt(splitTemp[1]);
        selectedDay = Integer.parseInt(splitTemp[2]);
        tenFormatDate = setDateFormat(selectedYear, selectedMonth, selectedDay);

        updateLay();

        return v;
    }
    //새로고침
    private void updateLay() {
        setSpan();
        //날짜 선택 리스너
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedCalendar = date;
                setSpan();
                RecordMenu.this.date = date.toString().replace("CalendarDay{","").replace("}", "");
                String[] mTempDateArray = RecordMenu.this.date.split("-");
                //선택된 날을 년, 월, 일로 나누어 선택된 날에 삽입
                selectedYear = Integer.parseInt(mTempDateArray[0]);
                selectedMonth = Integer.parseInt(mTempDateArray[1]);
                selectedDay = Integer.parseInt(mTempDateArray[2]);

                tenFormatDate = setDateFormat(selectedYear, selectedMonth, selectedDay);

                Log.i(TAG, "selected day = " + tenFormatDate);
                Intent toCalendarIntent = new Intent(getActivity(), CalendarScene.class);
                toCalendarIntent.putExtra("SELECTED_DATE", tenFormatDate);

                launcher.launch(toCalendarIntent);
            }
        });
    }
    //받은 년, 월, 일을 빈자리를 0으로 채워 하나로 만듦
    private String setDateFormat(int year, int month, int day) {
        String result = "", tempMonth = "", tempDay = "";

        tempMonth = tenNumFormat(month);
        tempDay = tenNumFormat(day);

        result = year + "-" + tempMonth + "-" + tempDay;

        return result;
    }

    private String tenNumFormat(int number) {
        String result = "";

        if(number < 10)
            result = "0" + number;
        else
            result = String.valueOf(number);

        return result;
    }
    //점, 오늘날짜 글자색, 선택한 날짜색
    private void setSpan() {
        materialCalendarView.removeDecorators();   //모든 Decorator 삭제
        //오늘 날짜 글자색
        materialCalendarView.addDecorator(new EventDecorator(todayColor,
                    Collections.singleton(CalendarDay.today()), 2));
        //선택한 날짜 글자색
        materialCalendarView.addDecorator(new EventDecorator(selectedDateColor,
                Collections.singleton(selectedCalendar), 2));
        //Strings.xml로 부터 색상과 부위 배열을 가져옴
        String[] typeStrAry = getResources().getStringArray(R.array.exerciseType);
        int[] colorIntAry = getResources().getIntArray(R.array.exerTypeColor);
        //distDate set에 중복 없게 날짜를 저장, dateType에는 날짜랑 부위를 짝지어서 저장
        HashSet<String> distinctDateSet = new HashSet<>();
        ArrayList<String> dateNTypeList = new ArrayList<>();
        for(Exercise exercise : MainActivity.exerciseDAO.exerList) {
            distinctDateSet.add(exercise.getDate());                          //모든 기록의 날짜를 중복없이 나열
            dateNTypeList.add(exercise.getDate() + ":" + exercise.getType());  //모든 기록을 ('날짜':'운동부위')쌍으로 나열
        }
        //날짜별로 부위의 빈도수를 구하여 높은 부위별로 색상을 다르게 함
        for(String date : distinctDateSet) {
            ArrayList<Integer> typeList = new ArrayList<>();
            for(int i = 0; i < typeStrAry.length; i++)
                typeList.add(Collections.frequency(dateNTypeList, date + ":" + typeStrAry[i]));     //부위별 빈도수를 typeList에 저장

            int colorNum = typeList.indexOf(Collections.max(typeList));                        //typeList의 최빈수의 위치를 저장

            Log.i(TAG, date + " type frequency = " + typeList);
            Log.i(TAG, date + " color = " + Integer.toHexString(colorIntAry[colorNum]));

            String[] markingDate = date.split("-");

            int markingYear = Integer.parseInt(markingDate[0]);
            int markingMonth = Integer.parseInt(markingDate[1]);
            int markingDay = Integer.parseInt(markingDate[2]);

            materialCalendarView.addDecorator(new EventDecorator(colorIntAry[colorNum],
                    Collections.singleton(CalendarDay.from(markingYear, markingMonth, markingDay)), 1));
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.recordManagementBtn:            //기록 관리 팝업으로
                Intent intent = new Intent(getActivity(), DBManagePopup.class);

                launcher.launch(intent);
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //프레그먼트 변환시 캘린더 초기화를 위해
        materialCalendarView.clearSelection();
        materialCalendarView.setSelectedDate(CalendarDay.today());
    }
}