### 스프링 컨드롤러 생성시기
Spring Framework 또한 Controller, Service, Biz, DAO 등의 객체를 Bean을 통해 미리 만들어 놓고 하나의 인스턴스만으로 사용한다.



### 스프링 효율적인 컨트롤러 및 서비스 설계
그리고 서비스에서 비즈니스로직을 작성하면 뷰에 종속되지않으므로 스펙의 변경 예를 들면 웹 -> 모바일앱으로 변경된다면 해당 비즈니스로직을 그대로 가지고 갈 수 있습니다.
단위 테스트시에도 서비스레이어에 비즈니스로직을 작성하면 테스트도 유용합니다.
단순한 비지니스일 경우는 controller 1개 -> Service 1개 -> DAO1개 이겠지만 비지니스가 복잡다하고 하면
다수의 Service가 호출이 되어야 한다. 그렇다면 여러개의 Service는 어디서 호출이 되어야 할것인가?

우선 controller는 아닐것 같다. "Thin controller, Fat Model"이라는 얘기도 있는 것처럼 다수의 service가
호출된다는 것은 결국 그것이 비지니스 로직이라는 얘기이고 controller에 비지니스로직을 담는 것은
적당하지 않을것 같다.
그렇다면 service에서 다른 service를 호출하는 것이 맞는 방향인것 같고 지금도 그렇게 사용하고 있는 있다.

여기까진 맞는 것 같은데..

그럼 Service와 DAO가 1:1로 연결이 되어야 할까?

DAO는 주로 테이블 단위이거나 데이터 블럭의 단위로 주로 형성이 되기에 Service와 1:1은 할 필요가 없을것 같은데 맞는 것인지 잘 모르겠다.
그리고 1:1로 생성이 된다라면 controller에서 호출되지 않는, 다른 Service에서만 불러지는 가비지 Service가 생성이 될듯한데..

어짜피 Service에서 1개의 DAO을 호출해야 한다는 제한이 없기에, 이런 가비지 Service 대신에
그냥 직접 DAO를 부르면 되는게 아닌가?

그리고 Service에서 3개의 DAO를 호출한다고 하면 그 Service를 호출할까? 아님 직접 3개의 DAO를
처리하는게 맞을까? 똑같은 비지니스라면 당연히 중복성을 낮추기 위해서 Service를 호출하는게 좋을것 같은데..
대규모 프로젝트라면 이런 중복되는 것이 얼마나 많을까..
어짜피 많다면 다른 사람들의 코드까지 모두 검토하면서 고민하지 말고 그냥 DAO를 호출하는게 속편하지 않을까?

## 스프링 마이바티스 연동
```
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.my.test" />
</bean>
```
이걸 context.xml에서 해주면 저기에 dao 인터페이스만 만들고 maper에 id 랑 동일한 메서드 만 만들어주면
서비스에서 dao.메서드로 불를수있다. 하지만 메퍼의 네임스페이스가 일단 dao 위치 + 이름으로 정의가 되어있어여한다.
하지만 인터넷에서는 다른 방법이 많이 기재되어있다 무엇이 다른가? 가령 dao 인터페이스를 만들고 여기서 sql session을 주입받아서 메퍼를 부른다 무슨 차이일까???어떤게 더 나은 방식일까?

## root-context vs servlet-context
* servlet-context는 서블릿 설정에대한 파일
  controller 매핑,view 처리 등

* root context
  비지니스 혹은 목적을 위한 service layer와 해당 서비스레이어에서 조회및 처리에 필요한 dataase와 연결된느 bean들에 대한 설정
  다수의 sevelet context가 root context의 bean의 정보를 참조하는 구조
  항상 단방향 severlet-context에서 root-context에 있는 빈 참조구조
  공통빈 설정

* 두 context에 중복되서 등록 되지않게 주의필요  에러는 발상하지 않으나 낭비

## 스프링 classpath:경로

* 결국엔 build후 target/classes 아래 또는 WEB-INF/classes  그경로가 된다. 이건 properties>deployment assembly에서 확인이 가능하다.
* 보통은 src/main 또는 src가 잡혀있다.

## 톰캣 재시작후 세션이 살아있을떄
세션은
이 문제에 대한 해결책은 WAS 세팅을 통한 것이다.context.xml 의 <Manager> 설정을 해주면 된다.
```
<Manager pathname=""> <saveOnRestart>false</saveOnRestart> </Manager>
```
더간단한 방법
```
<Manager pathname=""/>
```
