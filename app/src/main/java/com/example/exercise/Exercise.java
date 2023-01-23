package com.example.exercise;

import androidx.annotation.NonNull;
//운동기록 객체
public class Exercise implements Comparable<Exercise> {
    private int seq, setN;                                  //seq
    private String eDate, eType, eName, eVolume, eNumber;   //날짜, 운동 부위, 운동 이름, 무게
    private final String division = ":";                    //구분점

    Exercise(int seq, String eDate, String eType, String eName, int setN, String eVolume, String eNumber) {
        this.seq = seq;
        this.eDate = eDate;
        this.eType = eType;
        this.eName = eName;
        this.setN = setN;
        this.eVolume = eVolume;
        this.eNumber = eNumber;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public String geteType() {
        return eType;
    }

    public void seteType(String eType) {
        this.eType = eType;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public int getSetN() {
        return setN;
    }

    public void setSetN(int setN) {
        this.setN = setN;
    }

    public String geteVolume() {
        return eVolume;
    }

    public void seteVolume(String eVolume) {
        this.eVolume = eVolume;
    }

    public String geteNumber() {
        return eNumber;
    }

    public void seteNumber(String eNumber) {
        this.eNumber = eNumber;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(seq).append(division)
                .append(eDate).append(division)
                .append(eType).append(division)
                .append(eName).append(division)
                .append(setN).append(division)
                .append(eVolume).append(division)
                .append(eNumber);

        return result.toString();
    }

    //날짜를 기준으로 비교
    @Override
    public int compareTo(Exercise exercise) {
        return this.eDate.compareTo(exercise.eDate);
    }
}
