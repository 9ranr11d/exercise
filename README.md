# 프로젝트 : EXERCISE
exercise 0.1.2
## 프로젝트 소개

### 제작 동기
흔히 헬스라고 불리는 운동을 하려면 무엇보다 **자세**가 중요하다.<br>
왜냐하면 대체로 운동하고자 하는 부위를 **고립**시켜서 운동하는 걸 목표로 하고,<br>
운동부위는 사람이라는 신체 특성상 오른쪽, 왼쪽으로 두 부분을 나뉘기에 **균형있게** 발달 시키는게 또 다른 목표이기 때문이다.

내가 알기로는 **근육의 발달**의 가장 중요한 개념은 **점진적 과부하**인데 점진적 과부하에 영향을 주는 요인은 크게 4가지라고 본다.<br>
- 무게
- 세트 수
- 세트 당 횟수
- 쉬는 시간

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

#### 기능
- 스탑워치
- 1초마다 소리

### 탭 : [TimerMenu.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerMenu.java)

#### 기능
- 타이머 -> [TimerScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerScene.java)
- 기록 -> [RecordScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/RecordScene.java)
- 기록 예약 -> [RecordScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/RecordScene.java)
- **횟수 임시저장** -> [TimerScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerScene.java)
- 세트 수 조정
- 쉬는 시간 조정
- 푸쉬 알림 -> [TimerScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerScene.java)

### 탭 : [RecordMenu.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/RecordMenu.java)
- 기록 추가/수정/삭제 -> [CalendarScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/CalendarScene.java) ->
[PopupActivity.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/PopupActivity.java)
- 기록 엑셀로 내보내기 -> [DBManagePopup.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/DBManagePopup.java)
- **날짜별로 운동부위 빈도 수 비교 후 색 지정**

### 탭 : [SettingScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/SettingScene.java)
- 테마 설정
- 쉬는 시간 기본값 설정
- 최대 세트 수 설정

## 어려웠던점
- DB에 바로 연결하지 않고, exercise라는 객체에 담고 exercise로 데이터를 처리 했던 부분

``` java
//Exercise.java 생성자
Exercise(int seq, String eDate, String eType, String eName, int setN, String eVolume, String eNumber) {
        this.seq = seq;
        this.eDate = eDate;
        this.eType = eType;
        this.eName = eName;
        this.setN = setN;
        this.eVolume = eVolume;
        this.eNumber = eNumber;
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
  exerciseDAO.insertObj(
    lastSeq,
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

``` java
//distDate set에 중복 없게 날짜를 저장, dateType에는 날짜랑 부위를 짝지어서 저장
HashSet<String> distDate = new HashSet<>();
ArrayList<String> dateType = new ArrayList<>();
for(Exercise exer : MainActivity.exerciseDAO.eList) {
  distDate.add(exer.geteDate());
  dateType.add(exer.geteDate() + ":" + exer.geteType());
}
//날짜별로 부위의 빈도수를 구하여 높은 부위별로 색상을 다르게 함
for(String date : distDate) {
  ArrayList<Integer> typeList = new ArrayList<>();
  for(int i = 0; i < typeAry.length; i++) {
  typeList.add(Collections.frequency(dateType, date + ":" + typeAry[i]));     //부위별 빈도수를 typeList에 저장
}
Log.d(TAG, date + " type frequency = " + typeList);
int colorNum = typeList.indexOf(Collections.max(typeList));                   //typeList의 최빈수의 위치를 저장

Log.d(TAG, date + " color = " + Integer.toHexString(colorAry[colorNum]));

String[] markingDate = date.split("-");

int markingYear = Integer.parseInt(markingDate[0]);
int markingMonth = Integer.parseInt(markingDate[1]);
int markingDay = Integer.parseInt(markingDate[2]);

mCalendarView.addDecorator(new EventDecorator(colorAry[colorNum], Collections.singleton(CalendarDay.from(markingYear, markingMonth, markingDay)), 1));
}
```

``` xml
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

## 배운점
- 개념만 알고 있던 Listener의 활용법
- 객체 생성과 객체 관리(DAO)의 활용법
- DB연결과 보안성 관련 문제 해결법
- Class의 재활용 방법
- 함수의 재활용 방법
- Activity 간의 이동 및 데이터 이동 방법
- static, final 변수 선언의 중요성
- Log를 사용해 오류의 위치 추정 하는 법
- 예외처리 필요성
- 버전 관리의 필요성
- 테스트의 필요성
- 디자인의 중요성
- 다양한 View의 종류
- 테마의 상속 개념
- Thread의 개념
- Popup과 Dialog의 차이
- Notification 개념
- 생명주기 개념
- Fragment 개념
- List, Map을 이용한 데이터 관리
- 동적뷰의 개념
