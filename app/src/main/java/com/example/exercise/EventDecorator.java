package com.example.exercise;

import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;
//캘린더의 디지인
public class EventDecorator implements DayViewDecorator {
    private int color, flag;      //color : 표시할 색, signal : 1 -> 점, 2 -> 글자 색

    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates, int flag) {
        this.color = color;
        this.dates = new HashSet<>(dates);
        this.flag = flag;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if(flag == 1) {
            view.addSpan(new DotSpan(8, color));    //signal 1 : 점
        }else if(flag == 2) {
            view.addSpan(new ForegroundColorSpan(color)); //signal 2 : 글자 색
        }
    }
}
