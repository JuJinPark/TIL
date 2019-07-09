# json 파일을 읽어서 스트링으로 변환
* 프로그램 작성내역
그다음 json 배열이면 JSONArray 변환 JSONObject이면 JSONObject변한한다
그다음 CDL.toString(JSONArray) csv 파일형식으로 스트링으로 변환해줌
첫줄은 컬럼명만, 엮어져있고 데이터들은 컬럼이름 즉 key 값없이 value만 "," 뭍여져 있음

* 문제점 **JSONArray안에 JSONObject는 hashMap으로 구성되어있어 순서유지가 안됨**
* 해결방안
  * CDL.toString 대신 CDL.rowToString(arr) + CDL.toString(arr,JSONArray)
    * arr 에 `JSONArray arr = new JSONArray()` 해준다음 put 을이용해 원한는 출력순선대로 컬럼명을 string로 넣어준다.
  * org.json 라이브러리를 직접수정한다 그리고 나서 linkedHashMap으로 바꾼다.
    * 원본 라이브러리 자바 파일을 찾는다
    * 그리고 수정한후 `javac *.java` javac 를통해 재컴파일
    * 그리고 `jar cf json-custom.jar org`  jar cf 통해 다시 jar 파일로 만든다.
    * 프로젝트에서 새로만든 라이브러리로 dependency를 바꿔준다.
