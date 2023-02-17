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
    private HashMap<Integer, String> chkMap = new HashMap<>();
    private HashMap<Integer, String> allListMap = new HashMap<>();
    //목록의 한줄의 틀
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTex, typeTex, nameTex, setNumTex, volumeTex;
        private CheckBox chk;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTex = itemView.findViewById(R.id.recyclerDateTex);
            typeTex = itemView.findViewById(R.id.recyclerTypeTex);
            nameTex = itemView.findViewById(R.id.recyclerNameTex);
            setNumTex = itemView.findViewById(R.id.recyclerSetNumTex);
            volumeTex = itemView.findViewById(R.id.recyclerVolumeTex);
            chk = itemView.findViewById(R.id.recyclerChk);
        }
    }
    //목록에 삽입할 데이터리스트
    public DBListAdapter(ArrayList<Exercise> dataSet) {
        localDataSet = dataSet;
        for(int i = 0; i < localDataSet.size(); i++) {
            String[] localData = localDataSet.get(i).toString().split(":");
            allListMap.put(i, localData[0]);    //('i', 'SEQ')
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
        String str = localDataSet.get(position).toString();
        String[] tempStr = str.split(":");

        holder.dateTex.setText(tempStr[1].substring(2).replace("-", ""));  //날짜 (yyMMdd)
        holder.typeTex.setText(tempStr[2]);                                                //운동부위
        holder.nameTex.setText(tempStr[3]);                                                //운동이름
        holder.setNumTex.setText(tempStr[4]);                                              //세트 수
        holder.volumeTex.setText(MainActivity.stringFormat(tempStr[5], tempStr[6]));      //무게
        //checkedMap에 저장되어있는 체크유무를 판별(처음에는 체크돼있는게 없으니 모두 false)
        if(chkMap.containsKey(position))    //해당되는 키가 있으면 true
            holder.chk.setChecked(true);
        else
            holder.chk.setChecked(false);
        //체크시 checkedMap에 저장
        int tempPosition = position;
        holder.chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.chk.isChecked())
                    chkMap.put(tempPosition, tempStr[0]);  //('순서', 'SEQ')
                else
                    chkMap.remove(tempPosition);            //해당 위치의 Map 데이터 삭제
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
    //체크되어 있는 항목들의 position과 SEQ를 내보니기
    public HashMap<Integer, String> getCheckedList() {
        HashMap<Integer, String> tempCheckedMap = new HashMap<>(chkMap);

        chkMap.clear();

        return tempCheckedMap;
    }
    //선택된 항목을 리사이클뷰에서 삭제
    public void delList() {
        Set<Integer> checkedKeySet = chkMap.keySet();                           //체크된 내역('순서', 'SEQ')을 가져옴
        ArrayList<Integer> checkedKeyList = new ArrayList<>(checkedKeySet);
        Collections.sort(checkedKeyList, Collections.reverseOrder());           //'SEQ'가 아니라 현재 리스트의 '순서'로 삭제하기에 밑에서부터 삭제해야 순서가 바뀌지 않음

        Iterator checkedKeyIterator = checkedKeyList.iterator();
        while (checkedKeyIterator.hasNext()) {
            int checkedSeq = (int) checkedKeyIterator.next();
            notifyItemRemoved(checkedSeq);                                      //리스트 새로고침
            localDataSet.remove(checkedSeq);
        }
    }
    //전체선택
    public void setSelectAll(int mode) {
        if(mode == 0) {
            for(int i = 0; i < localDataSet.size(); i++)
                chkMap.put(i, allListMap.get(i));
        }else
            chkMap.clear();

        notifyDataSetChanged();     //리스트 새로고침
    }
}
