# 프로젝트 : EXERCISE
Version.1.0.6
## 프로젝트 소개

### 앱 상 이미지

![앱 상 이미지 한번에](https://user-images.githubusercontent.com/116334843/226799547-105ade16-153a-4c91-8077-6b372b08487d.jpg)

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
> - 기록 보기 -> [TimerScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/TimerScene.java) ->
[DBManagePopup.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/DBManagePopup.java)

### 탭 : [RecordMenu.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/RecordMenu.java)
> - 기록 추가/수정/삭제 -> [CalendarScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/CalendarScene.java) ->
[PopupActivity.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/PopupActivity.java)
> - 기록 엑셀로 내보내기 -> [DBManagePopup.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/DBManagePopup.java)
> - **날짜별로 운동부위 빈도 수 비교 후 색 지정**
> - 기록 검색 -> [DBManagePopup.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/DBManagePopup.java)

### 탭 : [SettingScene.java](https://github.com/9ranr11d/exercise/blob/master/app/src/main/java/com/example/exercise/SettingScene.java)
> - 테마 설정
> - 쉬는 시간 기본값 설정
> - 최대 세트 수 설정

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
- 1.0.5
> 1. 'TimerScene'에서 'DBManagePopup'으로 가는 버튼 추가
- 1.0.6
> 1. 'DBManagePopup'에 기록을 누를 시 'PopupActivity'로 가게해 자세한 기록 확인 가능
- 1.2.0
> 1. 'DMManagePopup'에 엑셀로 내보내기 방식 변경
