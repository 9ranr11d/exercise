package com.example.exercise;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Exercise 객체 DAO
public class ExerciseDAO {
    private final static String TAG = "ExerciseDAO";

    public ArrayList<Exercise> eList;

    ExerciseDAO() {
        eList = new ArrayList<>();
    }
    //eList에 추가하기
    public void insertObj(int seq, String date, String type, String name, int setN, String volume, String number) {
        Exercise exer = new Exercise(seq, date, type, name, setN, volume, number);
        eList.add(exer);
        //추가 후 정렬
        Collections.sort(eList, new Comparator<Exercise>() {        //seq로 1차 날짜로 2차 정렬
            @Override
            public int compare(Exercise exer1, Exercise exer2) {
                return Integer.compare(exer1.getSeq(), exer2.getSeq());
            }
        });
        Collections.sort(eList, Exercise::compareTo);
    }
    //id와 일치하는 객체 내보내기
    public Exercise searchSeqObj(int seq) {
        Exercise exer = null;
        for(int i = 0; i < eList.size(); i++) {
            exer = eList.get(i);
            if(exer.getSeq() == seq)
                return exer;
        }

        return exer;
    }
    //일치하는 날짜의 운동목록 객체 가져오기
    public ArrayList<Exercise> searchDateObj(String date) {
        ArrayList<Exercise> sDList = new ArrayList<>();
        for(int i = 0; i < eList.size(); i++) {
            Exercise exer = eList.get(i);
            Log.d(TAG, "exer date = " + exer.geteDate() + ", search date = " + date);
            if(exer.geteDate().equals(date))
                sDList.add(exer);
        }

        return sDList;
    }
    //객체 삭제
    public void deleteObj(int seq) {
        for(int i = 0; i < eList.size(); i++) {
            Exercise exer = eList.get(i);
            if(exer.getSeq() == seq)
                eList.remove(i);
        }
    }
    //객체 업데이트
    public void updateObj(int seq, String date, String type, String name, int setN, String volume, String number) {
        deleteObj(seq);
        insertObj(seq, date, type, name, setN, volume, number);
    }
}