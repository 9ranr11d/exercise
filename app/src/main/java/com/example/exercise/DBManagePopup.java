package com.example.exercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
//데이터 관리 팝업
public class DBManagePopup extends AppCompatActivity implements View.OnClickListener {
    private Button listDeleteBtn, listSendBtn, closeBtn;

    private EditText fileNameEdit;
    private CheckBox selectAllCheck;

    private DBListAdapter dbListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmanage_popup);
        //버튼
        listDeleteBtn = findViewById(R.id.listDeleteBtn);
        listSendBtn = findViewById(R.id.listSendBtn);
        closeBtn = findViewById(R.id.closeBtn);

        listDeleteBtn.setOnClickListener(this);
        listSendBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        //에딧
        fileNameEdit = findViewById(R.id.fileNameEdit);
        //체크
        selectAllCheck = findViewById(R.id.selectAllCheck);

        selectAllCheck.setOnClickListener(this);

        lookupDB();
    }

    public void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    //데이터 목록 조회
    private void lookupDB() {
        //라사이클뷰 생성
        RecyclerView dataListRecycler = findViewById(R.id.dataListRecycler);
        LinearLayoutManager deleteListLayManager = new LinearLayoutManager((Context) this);
        dataListRecycler.setLayoutManager(deleteListLayManager);
        dbListAdapter = new DBListAdapter(MainActivity.exerciseDAO.eList);
        dataListRecycler.setAdapter(dbListAdapter);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.listDeleteBtn:    //삭제하기
                try {
                    dbListAdapter.deleteChecked();  //리사이클뷰 목록 삭제

                    Collection<String> tempDelCList = dbListAdapter.lookupCheckedList().values();   //체크된 항목의 seq 가져오기
                    ArrayList<String> deleteCheckedSet = new ArrayList<>(tempDelCList); //체크된 항목을 ArrayList형식으로 바꿈

                    DBHelper helper = new DBHelper(this, "record.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();

                    if(deleteCheckedSet.toString() == "[]") {
                        makeToast("선택된 항목이 없습니다.");
                    }else {
                        Iterator deleteIter = deleteCheckedSet.iterator();

                        while (deleteIter.hasNext()) {
                            String dataSeq = (String) deleteIter.next();
                            //DB 속 데이터 삭제
                            db.delete("exercise", "seq = ?", new String[]{dataSeq});
                        }
                        makeToast("삭제되었습니다.");
                    }
                    db.close();
                    helper.close();
                }catch (IndexOutOfBoundsException e) {
                    makeToast("선택된 항목이 없습니다.");
                }
                selectAllCheck.setChecked(false);   //전체선택 체크박스 체크 해제
                break;
            case R.id.listSendBtn:  //내보내기
                String fileName = fileNameEdit.getText().toString();    //파일이름 가져오기
                if(fileName.equals("") || fileName.equals(null)) {
                    makeToast("파일 이름을 정해주세요");                //파일이름이 공백일 시
                }else {
                    Collection<String> tempSaveList = dbListAdapter.lookupCheckedList().values();   //체크된 항목의 seq 가져오기

                    ArrayList<String> saveCheckedSet = new ArrayList<>(tempSaveList);

                    if (saveCheckedSet.toString() == "[]") {
                        makeToast("선택된 항목이 없습니다.");
                    }else {
                        ArrayList<String> saveExerList = new ArrayList<>();

                        Iterator saveIter = saveCheckedSet.iterator();

                        while (saveIter.hasNext()) {
                            String dataSeq = (String) saveIter.next();
                            //해당되는 seq의 eDate eType eVolume eName setN 가져오기
                            saveExerList.add(MainActivity.exerciseDAO.searchSeqObj(Integer.parseInt(dataSeq)).toString());
                        }
                        saveExcel(saveExerList, fileName);  //엑셀파일로 내보내기

                        makeToast("엑셀파일로 내보냈습니다.");
                        dbListAdapter.setSelectAll(1);      //리사이클뷰에 체크여부 판단하는 리스트 초기화
                        selectAllCheck.setChecked(false);   //전체선택 체크박스 체크 해제
                    }
                }
                break;
            case R.id.selectAllCheck:   //전체선택
                if(selectAllCheck.isChecked()) {
                    dbListAdapter.setSelectAll(0);          //전체선택 체크박스 체크시 모든 항목 리스트에 추가
                }else {
                    dbListAdapter.setSelectAll(1);          //전체선택 체크박스 체크 해제시 리스트 초기화
                }
                break;
            case R.id.closeBtn:         //닫기
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
        }
    }
    //엑셀파일 만들기
    private void saveExcel(ArrayList<String> data, String fileName) {
        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet();

        Row row = sheet.createRow(0);
        Cell cell;

        String[] cellName = {"날짜", "부위", "이름", "세트", "무게", "횟수"}; //첫행

        for(int i = 0; i < cellName.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(cellName[i]);
        }

        Collections.sort(data);
        for(int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            String[] dataValues = data.get(i).split(":");
            for(int j = 1; j < dataValues.length; j++) {
                cell = row.createCell(j - 1);
                cell.setCellValue(dataValues[j]);
            }
        }

        File xlsFile = new File(getExternalFilesDir(null), fileName + ".xls");
        if (xlsFile.exists()) {
            xlsFile.delete();
        }

        try {
            FileOutputStream os = new FileOutputStream(xlsFile);
            workbook.write(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        makeToast(xlsFile.getAbsolutePath() + "에 저장되었습니다.");

        Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", xlsFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/excel");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(shareIntent,"엑셀파일로 내보내기"));
    }
}