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


## 스프링
- Bindgresult로 바인딩시 에러 처리 가능 (MODEL attriubts) request body는 테스트 필요
- session attributes 사용해서 model.addattribute를 에 넣어주면 자동으로 세션에 등록되고 다른받는 곳에서 @modelAttirbute 같은 이름으로 받으면 세션에 있는걸 받아온다. 같은 controller 에서만 사용가능 
- @Validated 로 선택전 validation 가능 구룹을 지정해서 특정 그룹 validation만 특정상황에 적용(같은 dto 만들떄 업데이트할때 사용시)