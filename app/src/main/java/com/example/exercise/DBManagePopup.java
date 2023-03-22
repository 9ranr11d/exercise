package com.example.exercise;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
//데이터 관리 팝업
public class DBManagePopup extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = "DBManagePopup";
    private Button delBtn, saveBtn, closeBtn;
    private SearchView nameSearch;
    private CheckBox allChk;

    private boolean isSaveFlag = false;

    private DBListAdapter listAdapter;

    public static Activity dbManagePopup;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmanage_popup);

        dbManagePopup = DBManagePopup.this;
        //버튼
        delBtn = findViewById(R.id.manageDelBtn);
        saveBtn = findViewById(R.id.manageSaveBtn);
        closeBtn = findViewById(R.id.manageCloseBtn);

        delBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        //체크박스
        allChk = findViewById(R.id.manageAllChk);

        allChk.setOnClickListener(this);
        //서치뷰
        nameSearch = findViewById(R.id.manageNameSearch);

        nameSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                makeRecycler(MainActivity.exerciseDAO.searchNameObj(s));
                return false;
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_CANCELED) {
                makeToast("취소 되었습니다.");
            }
        });

        makeRecycler(MainActivity.exerciseDAO.exerciseList);

        Intent recdManageIntent = getIntent();
        isSaveFlag = recdManageIntent.getBooleanExtra("IS_SAVE_FLAG", true);

        if(isSaveFlag) {
            saveBtn.setEnabled(true);
            delBtn.setEnabled(true);
        }
        else {
            saveBtn.setEnabled(false);
            delBtn.setEnabled(false);
        }
    }

    private void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    //리사이클뷰 생성
    private void makeRecycler(ArrayList<Exercise> list) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        recyclerView.setLayoutManager(linearLayoutManager);
        listAdapter = new DBListAdapter(list);
        recyclerView.setAdapter(listAdapter);

        listAdapter.setItemClickListener(new DBListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Exercise target, int position) {
                Intent intentOfClicked = new Intent(getApplication(), PopupActivity.class);
                intentOfClicked.putExtra("IS_ADD_FLAG", false);
                intentOfClicked.putExtra("IS_ONLY_READ", true);
                intentOfClicked.putExtra("SELECTED_DATE", target.getDate());
                intentOfClicked.putExtra("SEQ", target.getSeq());
                intentOfClicked.putExtra("TYPE", target.getType());
                intentOfClicked.putExtra("NAME", target.getName());
                intentOfClicked.putExtra("SET_NUM", target.getSetNum());
                intentOfClicked.putExtra("VOLUME", target.getVolume());
                intentOfClicked.putExtra("NUMBER", target.getNumber());

                launcher.launch(intentOfClicked);
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manageDelBtn:         //삭제하기
                try {
                    listAdapter.delList();  //목록 삭제

                    Collection<String> delCheckedCollection = listAdapter.getCheckedList().values();    //체크된 항목의 SEQ 가져오기
                    ArrayList<String> delCheckedList = new ArrayList<>(delCheckedCollection);           //체크된 항목을 ArrayList형식으로 바꿈
                    Log.i(TAG, "delete checked list = " + delCheckedList);

                    DBHelper helper = new DBHelper(this, "record.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();

                    if(delCheckedList.toString() == "[]")
                        makeToast("선택된 항목이 없습니다.");
                    else {
                        Iterator delCheckedIterator = delCheckedList.iterator();
                        while (delCheckedIterator.hasNext()) {
                            String dataSeq = (String) delCheckedIterator.next();
                            db.delete("EXERCISE_TB", "SEQ = ?", new String[]{dataSeq}); //DB 속 데이터 삭제
                            MainActivity.exerciseDAO.deleteObj(Integer.parseInt(dataSeq));              //DAO list 속 데이터 삭제
                        }
                        makeToast("삭제되었습니다.");
                    }

                    db.close();
                    helper.close();
                }catch (IndexOutOfBoundsException e) {
                    makeToast("선택된 항목이 없습니다.");
                }

                allChk.setChecked(false);   //전체선택 체크박스 체크 해제
                break;
            case R.id.manageSaveBtn:     //내보내기
                LayoutInflater layoutInflater = getLayoutInflater();                                //안에 삽입할 layout
                View fileDialogView = layoutInflater.inflate(R.layout.dialog_file_name, null); //findViewById를 위해
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);

                saveDialog.setTitle("엑셀로 내보내기")
                        .setView(fileDialogView)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText fileEdit = fileDialogView.findViewById(R.id.dialogFileEdit);
                                String fileName = fileEdit.getText().toString();    //파일이름 가져오기
                                if(fileName.equals("") || fileName.equals(null))
                                    makeToast("파일 이름을 정해주세요");                //파일이름이 공백일 시
                                else {
                                    Collection<String> saveCheckedCollection = listAdapter.getCheckedList().values();   //체크된 항목의 seq 가져오기
                                    ArrayList<String> saveCheckedList = new ArrayList<>(saveCheckedCollection);

                                    if (saveCheckedList.toString() == "[]")
                                        makeToast("선택된 항목이 없습니다.");
                                    else {
                                        ArrayList<String> saveList = new ArrayList<>();
                                        Iterator saveCheckedIterator = saveCheckedList.iterator();
                                        while (saveCheckedIterator.hasNext()) {
                                            String seq = (String) saveCheckedIterator.next();
                                            saveList.add(MainActivity.exerciseDAO.searchSeqObj(Integer.parseInt(seq)).toString());
                                        }
                                        saveExcel(saveList, fileName);    //엑셀파일로 내보내기

                                        makeToast("엑셀파일로 내보냈습니다.");
                                        listAdapter.setSelectAll(1);            //리사이클뷰에 체크여부 판단하는 리스트 초기화
                                        allChk.setChecked(false);               //전체선택 체크박스 체크 해제
                                    }
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                saveDialog.show();
                break;
            case R.id.manageAllChk:   //전체선택
                if(allChk.isChecked())
                    listAdapter.setSelectAll(0);          //전체선택 체크박스 체크시 모든 항목 리스트에 추가
                else
                    listAdapter.setSelectAll(1);          //전체선택 체크박스 체크 해제시 리스트 초기화

                break;
            case R.id.manageCloseBtn:         //닫기
                setResult(RESULT_CANCELED);
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

        String[] titleStrAry = {"날짜", "부위", "이름", "세트", "무게", "횟수"}; //제목(첫행)
        for(int i = 0; i < titleStrAry.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(titleStrAry[i]);
        }

        Collections.sort(data); //데이터 정렬
        for(int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            String[] dataValues = data.get(i).split(":");

            for(int j = 1; j < dataValues.length; j++) {
                cell = row.createCell(j - 1);
                cell.setCellValue(dataValues[j]);
            }
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File xlsFile = new File(dir, fileName + ".xls");

        if (xlsFile.exists())
            xlsFile.delete();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(xlsFile);
            workbook.write(fileOutputStream);
            makeToast(fileName + ".xls가 " + Environment.DIRECTORY_DOWNLOADS + "에 저장 되었습니다.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isSaveFlag)
            TimerScene.isManageFlag = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();         //현재 포커스하고 있는 뷰 가져오기
        if(focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);   //포커스 있는 뷰를 영역으로 지정
            int x = (int) ev.getX(), y = (int) ev.getY();
            if(!rect.contains(x, y)) {              //클릭한 위치가 영역내에 없으면 실행?
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}