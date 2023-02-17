package com.example.exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Exercise 객체 DAO
public class ExerciseDAO {
    public ArrayList<Exercise> exerciseList;

    ExerciseDAO() {
        exerciseList = new ArrayList<>();
    }
    //exerList에 추가하기
    public void insertObj(int seq, String date, String type, String name, int setN, String volume, String number) {
        Exercise exercise = new Exercise(seq, date, type, name, setN, volume, number);
        exerciseList.add(exercise);
        //추가 후 정렬
        Collections.sort(exerciseList, new Comparator<Exercise>() {        //seq로 1차 날짜로 2차 정렬
            @Override
            public int compare(Exercise exer1, Exercise exer2) {
                return Integer.compare(exer1.getSeq(), exer2.getSeq());
            }
        });
        Collections.sort(exerciseList, Exercise::compareTo);
    }
    //id와 일치하는 객체 내보내기
    public Exercise searchSeqObj(int seq) {
        Exercise resultExercise = null;

        for(Exercise exercise : exerciseList) {
            if(exercise.getSeq() == seq)
                resultExercise = exercise;
        }

        return resultExercise;
    }
    //날짜와 일치하는 운동목록 객체 가져오기
    public ArrayList<Exercise> searchDateObj(String date) {
        ArrayList<Exercise> resultList = new ArrayList<>();

        for(Exercise exercise : exerciseList) {
            if(exercise.getDate().equals(date))
                resultList.add(exercise);
        }

        return resultList;
    }
    //객체 삭제
    public void deleteObj(int seq) {
        for(int i = 0; i < exerciseList.size(); i++) {
            Exercise exercise = exerciseList.get(i);

            if(exercise.getSeq() == seq)
                exerciseList.remove(i);
        }
    }
    //객체 업데이트
    public void updateObj(int seq, String date, String type, String name, int setN, String volume, String number) {
        deleteObj(seq);
        insertObj(seq, date, type, name, setN, volume, number);
    }
    //이름과 일치하는 객체목록 내보내기
    public ArrayList<Exercise> searchNameObj(String str) {
        ArrayList<Exercise> resultList = new ArrayList<>();
        for(Exercise exercise : exerciseList) {
            if(exercise.getName().toLowerCase().contains(str))      //포함되는 단어 모두
                resultList.add(exercise);
        }

        return resultList;
    }
}
