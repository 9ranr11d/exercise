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
    private int color;
    private boolean isDotFlag;

    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates, boolean isDotFlag) {
        this.color = color;
        this.dates = new HashSet<>(dates);
        this.isDotFlag = isDotFlag;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if(isDotFlag) {
            view.addSpan(new DotSpan(8, color));    //signal 1 : 점
        }else {
            view.addSpan(new ForegroundColorSpan(color)); //signal 2 : 글자 색
        }
    }
}
