# STRING CONCAT 및 문자열 합치기
* 항상 STRING BUILDER 가 "+"나 "CONCAT"보다 더 나은 성능을 보인다. 하지만 큰성능 차이는 없다
* 문자열에 대한 "+" 연산자인 경우 COMPLIER가 STRINGBUILDER로 바꾸어서 진행한다. 그래서 성능에는 큰 차이가 없다.
* 그러나 LOOP 문안에서는 컴파일러가 STRINGBUILDER 로 바꾸는 부분이 비효율적으로 진행되니 명시적으로 사용하는게 좋다
~~~
String s= "" ;
for(int i= 0 ; i < 10 ; i++ )
s+= "a" ;
~~~
* 여기서는 각 포문마다 새로운 STRINGBUILDER가 생성되고 기존 S의값이 APPEND되고 다시 A가 APPEND되고 그값을 TOSTRING을 통해 STRING변환후 S에 대입한다. 총10번 STRINGBUILDER 객체와 STRING 객체가 생긴다.
* 계기 : 웹서버
* 이유 : 웹서버 프로젝트중 헤더 문자열로 생성해주는 메서드를 만들던중 STRINGBUILDER VS STRING으로 고민하던중
