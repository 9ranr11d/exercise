package com.example.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
//리사이클뷰
public class DBListAdapter extends RecyclerView.Adapter<DBListAdapter.ViewHolder> {
    private ArrayList<Exercise> localDataSet;
    private HashMap<Integer, String> checkedMap = new HashMap<>();
    private HashMap<Integer, String> allListMap = new HashMap<>();
    //목록의 한줄의 틀
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dbDateListTex, dbTypeListTex, dbNameListTex, dbSetNListTex, dbVolumeListTex;
        private CheckBox dbDataListCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dbDateListTex = itemView.findViewById(R.id.dbDateListTex);
            dbTypeListTex = itemView.findViewById(R.id.dbTypeListTex);
            dbNameListTex = itemView.findViewById(R.id.dbNameListTex);
            dbSetNListTex = itemView.findViewById(R.id.dbSetNListTex);
            dbVolumeListTex = itemView.findViewById(R.id.dbVolumeListTex);
            dbDataListCheck = itemView.findViewById(R.id.dbDataListCheck);
        }
    }
    //목록에 삽입할 데이터리스트
    public DBListAdapter(ArrayList<Exercise> dataSet) {
        localDataSet = dataSet;
        for(int i = 0; i < localDataSet.size(); i++) {
            String[] localData = localDataSet.get(i).toString().split(":");
            allListMap.put(i, localData[0]);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list_recycler_item, parent, false);

        DBListAdapter.ViewHolder viewHolder = new DBListAdapter.ViewHolder(view);

        return viewHolder;
    }
    //각 목록 생성
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = localDataSet.get(position).toString();
        String[] tempText = text.split(":");

        holder.dbDateListTex.setText(tempText[1].substring(2).replace("-", ""));
        holder.dbTypeListTex.setText(tempText[2]);
        holder.dbNameListTex.setText(tempText[3]);
        holder.dbSetNListTex.setText(tempText[4]);
        holder.dbVolumeListTex.setText(MainActivity.stringformat(tempText[5], tempText[6]));

//        holder.dbDataListCheck.setOnCheckedChangeListener(null);
        //checkedMap에 저장되어있는 체크유무를 판별
        if(checkedMap.containsKey(position)) {
            holder.dbDataListCheck.setChecked(true);
        }else {
            holder.dbDataListCheck.setChecked(false);
        }

        int tempPosition = position;
        //체크시 checkedMap에 저장
        holder.dbDataListCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.dbDataListCheck.isChecked()) {
                    checkedMap.put(tempPosition, tempText[0]);
                }else {
                    checkedMap.remove(tempPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
    //체크되어 있는 항목들의 position과 seq를 내보니기
    public HashMap<Integer, String> lookupCheckedList() {
        HashMap<Integer, String> tempCM = new HashMap<>(checkedMap);

        checkedMap.clear();

        return tempCM;
    }
    //선택된 항목을 리사이클뷰에서 삭제
    public void deleteChecked() {
        Set<Integer> cMKeySet = checkedMap.keySet();

        ArrayList<Integer> deleteSortList = new ArrayList<>(cMKeySet);
        Collections.sort(deleteSortList, Collections.reverseOrder());

        Iterator deleteCheckPositIter = deleteSortList.iterator();

        while (deleteCheckPositIter.hasNext()) {
            int deleteCheckId = (int) deleteCheckPositIter.next();
            notifyItemRemoved(deleteCheckId);
            localDataSet.remove(deleteCheckId);
        }
    }
    //전체선택
    public void setSelectAll(int mode) {
        if(mode == 0) {
            for (int i = localDataSet.size() - 1; i >= 0; i--) {
                checkedMap.put(i, allListMap.get(i));
            }
        }else {
            checkedMap.clear();
        }
        notifyDataSetChanged();
    }
}
