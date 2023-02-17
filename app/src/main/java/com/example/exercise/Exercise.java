package com.example.exercise;

import androidx.annotation.NonNull;
//운동기록 객체
public class Exercise implements Comparable<Exercise> {
    private int seq, setNum;                                  //seq
    private String date, type, name, volume, number;   //날짜, 운동 부위, 운동 이름, 무게
    private final String division = ":";                    //구분점

    Exercise(int seq, String date, String type, String name, int setNum, String volume, String number) {
        this.seq = seq;
        this.date = date;
        this.type = type;
        this.name = name;
        this.setNum = setNum;
        this.volume = volume;
        this.number = number;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(seq).append(division)
                .append(date).append(division)
                .append(type).append(division)
                .append(name).append(division)
                .append(setNum).append(division)
                .append(volume).append(division)
                .append(number);

        return result.toString();
    }

    //날짜를 기준으로 비교
    @Override
    public int compareTo(Exercise exercise) {
        return this.date.compareTo(exercise.date);
    }
}
