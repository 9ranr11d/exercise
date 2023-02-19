# 프로젝트 : EXERCISE
Version.1.0.4
## 프로젝트 소개

### 제작 동기
흔히 헬스라고 불리는 운동을 하려면 무엇보다 **자세**가 중요하다.<br>
왜냐하면 대체로 운동하고자 하는 부위를 **고립**시켜서 운동하는 걸 목표로 하고,<br>
운동부위는 사람이라는 신체 특성상 오른쪽, 왼쪽으로 두 부분을 나뉘기에 **균형있게** 발달 시키는게 또 다른 목표이기 때문이다.

내가 알기로는 **근육의 발달**의 가장 중요한 개념은 **점진적 과부하**인데 점진적 과부하에 영향을 주는 요인은 크게 4가지라고 본다.<br>
> - 무게
> - 세트 수
> - 세트 당 횟수
> - 쉬는 시간

멀티플레이가 안되는 나로써는 저 4가지 요인과 자세까지 신경쓰면서 운동을 하려니 도저히 자세에만 신경 쓸 수 없었다.<br>
그래서 자세를 제외한 4가지 요인들을 최대한 **배제**시키기로 했다.

### 개발환경
- Android Studio (Java)
- SQLite

### SDK 버전
- min SDK version : 26
- target SDK version : 31

### 라이브러리
- Apache POI 5.2.2

### 외부 리소스
- Custom CalendarView
> https://github.com/prolificinteractive/material-calendarview

## 기능 소개

### 탭 : [StopwatchMenu.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/StopwatchMenu.java)
> - 스탑워치
> - 1초마다 소리

### 탭 : [TimerMenu.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerMenu.java)
> - 타이머 -> [TimerScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerScene.java)
> - 기록 -> [RecordScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/RecordScene.java)
> - 기록 예약 -> [RecordScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/RecordScene.java)
> - **횟수 임시저장** -> [TimerScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerScene.java)
> - 세트 수 조정
> - 쉬는 시간 조정
> - 푸쉬 알림 -> [TimerScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerScene.java)

### 탭 : [RecordMenu.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/RecordMenu.java)
> - 기록 추가/수정/삭제 -> [CalendarScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/CalendarScene.java) ->
[PopupActivity.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/PopupActivity.java)
> - 기록 엑셀로 내보내기 -> [DBManagePopup.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/DBManagePopup.java) ->
[DBListAdapter.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/DBListAdapter.java)
> - **날짜별로 운동부위 빈도 수 비교 후 색 지정**
> - 기록 검색 -> [DBManagePopup.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/DBManagePopup.java)

### 탭 : [SettingScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/SettingScene.java)
> - 테마 설정
> - 쉬는 시간 기본값 설정
> - 최대 세트 수 설정

## 특이사항 (*가독성을 위해 실제 코드와 다를 수 있습니다.*)
- DB에 바로 연결하지 않고, exercise라는 객체에 담고, 데이터를 처리
``` java
//Exercise.java
Exercise(int seq, String eDate, String eType, String eName, int setN, String eVolume, String eNumber) {
    this.seq = seq;             //id
    this.eDate = eDate;         //날짜
    this.eType = eType;         //운동 부위
    this.eName = eName;         //운동 이름
    this.setN = setN;           //세트 수
    this.eVolume = eVolume;     //무게
    this.eNumber = eNumber;     //횟수
}

//ExerciseDAO.java
public class ExerciseDAO {
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
}

//MainActivity.java
@Override
protected void onCreate(Bundle savedInstanceState) {
    //DB로 부터 데이터 받아옴
    exerciseDAO = new ExerciseDAO();
    //저장되있는 DB의 모든 데이터를 exercise객체에 담음
    DBHelper helper = new DBHelper(this, "record.db", null, 1);
    SQLiteDatabase db = helper.getReadableDatabase();
    String sql = "SELECT * FROM exercise;";
    Cursor lookupCursor = db.rawQuery(sql,null);
    while(lookupCursor.moveToNext()) {
    lastSeq = lookupCursor.getInt(0);
    exerciseDAO.insertObj(lastSeq,                      //마지막에 저장된 변수가 마지막 기록의 seq
                          lookupCursor.getString(1),
                          lookupCursor.getString(2),
                          lookupCursor.getString(3),
                          lookupCursor.getInt(4),
                          lookupCursor.getString(5),
                          lookupCursor.getString(6));
    }
    lookupCursor.close();
    db.close();
    helper.close();
}
```

- 같은 날짜안의 기록들의 운동부위의 빈도 수를 분석해 일정표시 색을 달리함
``` xml
<!--strings.xml-->
<string-array name="exerciseType">
   <item>가슴</item>
   <item>등</item>
   <item>하체</item>
   <item>어깨</item>
   <item>복근</item>
   <item>이두</item>
   <item>삼두</item>
</string-array>
<!--빨, 주, 노, 초, 파, 남, 보-->
<integer-array name="exerTypeColor">
  <item>0xFFDB4455</item>
  <item>0xFFFF7F00</item>
  <item>0xFFFDFD96</item>
  <item>0xFF008000</item>
  <item>0xFF4AA8D8</item>
  <item>0xFF0E0F37</item>
  <item>0xFF8B00FF</item>
</integer-array>
```

``` java
//distDate set에 중복 없게 날짜를 저장, dateType에는 날짜랑 부위를 짝지어서 저장
HashSet<String> distDate = new HashSet<>();
ArrayList<String> dateType = new ArrayList<>();
for(Exercise exer : MainActivity.exerciseDAO.eList) {
    distDate.add(exer.geteDate());                                //모든 기록의 날짜를 중복없이 나열
    dateType.add(exer.geteDate() + ":" + exer.geteType());        //모든 기록을 ('날짜':'운동부위')의 쌍으로 나열
}
//날짜별로 부위의 빈도수를 구하여 높은 부위별로 색상을 다르게 함
for(String date : distDate) {                                   //부위별 빈도수를 typeList에 저장          
    ArrayList<Integer> typeList = new ArrayList<>();
    for(int i = 0; i < typeAry.length; i++) {                     //typeAry : exerciseType(strings.xml 속 운동부위 String-array)
    typeList.add(Collections.frequency(dateType, date + ":" + typeAry[i]));   //('날짜':'운동부위')로 저장한 리스트속에서 
}                                                                             //('해당 날짜':'해당 운동부위')와 일치하는 데이터의 수

int colorNum = typeList.indexOf(Collections.max(typeList));                   //typeList의 최빈수의 위치를 저장

String[] markingDate = date.split("-");

int markingYear = Integer.parseInt(markingDate[0]);
int markingMonth = Integer.parseInt(markingDate[1]);
int markingDay = Integer.parseInt(markingDate[2]);
//Custom CalendarView의 날짜 밑에 점 찍는 함수(를 색상도 지정하게 수정)
mCalendarView.addDecorator(new EventDecorator(colorAry[colorNum],
                                              Collections.singleton(CalendarDay.from(markingYear, markingMonth, markingDay)),
                                              1));
}
```

- DB의 기본키(seq)를 auto increment가 아닌 자체적으로 처리를 위해 SharedPreferences로 별도로 변수를 저장했고, 그로 인해 예기치 못한 종료로 seq 미저장 될 때의 SQLiteConstraintException 예외처리
``` java
private void tInsertSeq(String selectDate, String selectType, String selectName, String selectVolume, String selectNumber) {
    DBHelper helper = new DBHelper(getActivity(), "record.db", null, 1);
    try {
        if (helper.dataInsert(MainActivity.seq, selectDate, selectType, selectName, setNum, selectVolume, selectNumber)) {
            MainActivity.exerciseDAO.insertObj(MainActivity.seq,
                                               selectDate, selectType,
                                               selectName,
                                               setNum,
                                               selectVolume,
                                               selectNumber);
            MainActivity.seq++;

            makeToast(selectName + " (으)로 기록되었습니다.");

            setNum = 0;
            setSetNum(setNum);
        }
    }catch (SQLiteConstraintException uniqueE) {
        MainActivity.seq = MainActivity.lastSeq + 1;    //마지막 기록의 seq를 가져와서 +1
        //다른 Exception이 있는 지 확인을 위한 재귀함수
        tInsertSeq(selectDate, selectType, selectName, selectVolume, selectNumber);
    }catch (Exception e) {
        e.printStackTrace();
        makeToast("예기치 못한 오류입니다");
    }
    helper.close();
}
```

- 세트 당 횟수를 임시저장 후 기록시 저장된 데이터 자동 입력
``` java
//TimerMenu.java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_timer_menu, container, false);
    timerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent getTScenIntent = result.getData();
        if(result.getResultCode() == Activity.RESULT_OK) {          //Activity.RESULT_OK : 쉬는 시간 끝
            setNum = getTScenIntent.getIntExtra("set_n", 0);        //TimerScen으로 부터 세트 수 받아옴
            setSetNum(setNum);
            //세트별 횟수 임시저장값
            String tGRPS = getTScenIntent.getStringExtra("reps_per_set");
            if(!tGRPS.equals("")) {
                int tRPS = Integer.parseInt(getTScenIntent.getStringExtra("reps_per_set"));
                //'세트 수':'임시저장값','세트 수':'임시저장값',...의 형태
                repsPerSet.append(setNum).append(":").append(tRPS).append(",");
            }
    }
}

@Override
public void onClick(View view) {
    switch (view.getId()) {
        case R.id.dbSaveBtn:    //기록
            if(setNum != 0) {   //세트수가 없으면 기록 되지 않음
            int[] iRPS = new int[setNum];
            //저장된 임시저장값이 있을 때만 실행
            if(!repsPerSet.toString().isEmpty()) {
                String[] tRPS = repsPerSet.toString().split(",");
                //세트별 횟수 임시저장값을 배열의 맞는 위치에 대입
                for (String str : tRPS) {
                    String[] tStr = str.split(":");
                    int iStr = Integer.parseInt(tStr[0]);
                    //임시저장된 값의 크기와 세트 수가 맞지 않을 때를 방지
                    if(iStr <= setNum) {
                        int tInt = iStr - 1;
                        iRPS[tInt] = Integer.parseInt(tStr[1]);
                    }
                }
            }
    Intent recordIntent = new Intent(getActivity(), RecordScene.class);
    recordIntent.putExtra("set_n", setNum);
    recordIntent.putExtra("eName", routine);
    recordIntent.putExtra("reps_per_set", iRPS);        //ArrayList 통째로 던지기
    
    timerLauncher.launch(recordIntent);
    }
}

//RecordScene.java
private int[] repsPerSet;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record_scene);
    
    Intent getRecordIntent = getIntent();                           //세트 수를 받아옴
    setNum = getRecordIntent.getIntExtra("set_n", 0);
    repsPerSet = new int[setNum];
    repsPerSet = getRecordIntent.getIntArrayExtra("reps_per_set");  //ArrayList 통째로 받기
    
    numberEdit = new EditText[setNum];
    for(int i = 0; i < setNum; i++) {
        numberEdit[i] = new EditText(getApplication());
        //임시저장값이 없으면 생략
        if(repsPerSet[i] != 0)
            numberEdit[i].setText(String.valueOf(repsPerSet[i]));   //위치에 해당하는 임시저장값 저장
    }                                                               //(있으면 있는데로 없으면 없는데로)
}
```

- 일반 Activity를 Dialog마냥 다른 Activity 위에 일정크기로 띄우고 그 위에 한번 더 또 다른 Activity를 띄울 수 있게 처리<br>
(예시)
``` xml
<!--AndroidManifest.xml-->
<activity
    android:name=".B_Activity"
    android:exported="false"
    android:theme="@style/Theme.~.Dialog" />
```
``` java
//A_Activity
Intent aIntent = new Intent(getActivity(), B_Activity.class);               //가지고 갈 Intent를 만듬
aIntent.putExtra("임의의 키이름", 임의의 값);

bLauncher.launch(recordIntent);                                    //Intent를 가지고 B Activity로 이동

//B_Activity 
Intent getAIntent = getIntent();
변수 = getRecordIntent.getIntExtra("가져올 키이름", 기본값);       //가지고 온 Intent를 가져옴

Intent bIntent = new Intent();                                    //다시 가져갈 Intent 만듬
setResult(RESULT_OK, bIntent);                                    //RESULT_OK란 신호와 Intent를 들고 A Activity로 이동 
finish();

//A Activity
//onCreate 안에서 Launcher를 만들어야 띄운 Activity 위에 한번 더 Activity를 띄울 수 있음
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_timer_menu, container, false);
    bLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent getTScenIntent = result.getData();
        if(result.getResultCode() == Activity.RESULT_OK) {
            RESULT_OK란 신호 일때 할 로직
        }
    }
}
```

- RecyclerView 전체 선택과, 체크 여부 기록을 위한 Map
``` java
//DBListAdapter.java
private ArrayList<Exercise> localDataSet;
private HashMap<Integer, String> checkedMap = new HashMap<>();  //해당 행의 체크 여부
private HashMap<Integer, String> allListMap = new HashMap<>();  //모든 행의 기록
//목록에 삽입할 데이터리스트
public DBListAdapter(ArrayList<Exercise> dataSet) {
    localDataSet = dataSet;
    for(int i = 0; i < localDataSet.size(); i++) {
        String[] localData = localDataSet.get(i).toString().split(":");
        allListMap.put(i, localData[0]);    //('순서', 'seq')
    }
}
    
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    //checkedMap에 저장되어있는 체크유무를 판별(처음에는 체크돼있는게 없으니 모두 false)
    //스크롤을 내리고 올리면 뷰를 재사용하는 메커니즘상 기존과는 다른게 체크됨
    if(checkedMap.containsKey(position)) {  //해당되는 키가 있으면 true
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
                checkedMap.put(tempPosition, tempText[0]);  //('위치', 'seq')
            }else {
                checkedMap.remove(tempPosition);            //해당 위치의 Map 데이터 삭제
            }
        }
    });
}
    
//전체선택
public void setSelectAll(int mode) {
    if(mode == 0) {
        for(int i = 0; i < localDataSet.size(); i++) {
            checkedMap.put(i, allListMap.get(i));       //('순서', 'seq')
        }
    }else {
        checkedMap.clear();
    }
    notifyDataSetChanged();     //새로고침
}
```
- 모든 기록 중에서 원하는 검색어(이름)을 통해 기록 검색 가능
```java
//DBManagePopup.java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dbmanage_popup);
    //searchView
    nameSearch = findViewById(R.id.manageNameSearch);

    nameSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {                    //searchView의 텍스트 변화감지
            makeRecycler(MainActivity.exerciseDAO.searchNameObj(s));    //현재 searchView의 텍스트로
            return false;                                               //같은 이름의 리스트를 표시
        }
    });
    makeRecycler(MainActivity.exerciseDAO.exerciseList);                //처음에는 모든 리스트 표시
}
//리사이클뷰 생성
private void makeRecycler(ArrayList<Exercise> list) {
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
    recyclerView.setLayoutManager(linearLayoutManager);
    listAdapter = new DBListAdapter(list);
    recyclerView.setAdapter(listAdapter);
}
//exerciseDAO.java
//이름과 일치하는 객체목록 내보내기
public ArrayList<Exercise> searchNameObj(String str) {
    ArrayList<Exercise> resultList = new ArrayList<>();
    for(Exercise exercise : exerciseList) {
        if(exercise.getName().toLowerCase().contains(str))      //포함되는 단어 모두
        resultList.add(exercise);
    }

    return resultList;
}
```
## 최신내용
- 1.0.0
> 1. 'DBManagePopup'에 검색기능 추가
> 2. 'TimerScene'에 'DBManagePopup'을 볼 수 있는 버튼 추가
- 1.0.1
> 1. 'TimerScene'이 종료 될 시 'DBManagePopup' 닫도록 수정
- 1.0.2
> 1. 'TimerScene'이 종료 될 시 'DBmanagePopup'이 *열려 있을 시* 닫도록 수정
> 2. 앱을 종료 시 'MainActivity'에 모든 notification 알람 끄도록 수정
- 1.0.3
> 1. 'DBManagePopup' 검색기능을 이용해 검색 후 삭제 시 삭제 되지 않던 버그 수정
> 2. 'DBManagePopup' 검색기능 사용 중 화면의 다른 곳 클릭 시 키보드가 내려가게 수정
- 1.0.4
> 1. 'PopupActivity' 이름, 무게, 횟수를 입력 후 다른 곳 클릭 시 키보드가 내려가게 수정
