# 서비스 컨테이너
## 사용이유
    - 의존성 주입을 위해
        - 의존성 주입(Dependency Injection, DI)은 프로그래밍 에서 구성요소간의 의존   관계가 소스코드  내부가 아닌 외부의 설정파일 등을 통해 정의되게 하는          디자인 패턴 중의 하나이다.
        - 한 마디로 뭔가 필요한게 있으면 내가 가서 찾아오던지 직접 만드는 대신 무엇이   필요하다고 선언만 하면 외부에서 알아서 제공해주겠다는 것입니다.
    - 장점
        - 관심분리 (시스템 생성 로직 과 시스템 사용 로직)
            - 코드 변경 쉬움 
            - 의존성이 줄어듬 
        - 테스트 간편            

 - 사용방법
    - setter, constructor       
## bindings
    - service provider를 통해 service container 에 등록 또는 바인딩 한다.
    - binding 종류   
        - simple binding
            - 기본적인 바인딩으로 싱글톤이 아닌 의존성 주입 호출떄마다 새로운 인스턴스 반환
            ```
            $this->app->bind('HelpSpot\API', function ($app) {
             return new HelpSpot\API($app->make('HttpClient'));
            });

            ```
        - singleton binding
            - 의존성 주입 호출마다 하나의 인스턴스 반환
            ```
            $this->app->singleton('HelpSpot\API', function ($app) {
             return new HelpSpot\API($app->make('HttpClient'));
            });
            ```
        - instance binding
            - 이미 존재하는 인스턴스를 바인딩한다.
            - 다른점은 new 생성자를 상용하지않고 특정 인스턴스를 바인딩??
            ```
            $api = new HelpSpot\API(new HttpClient);
            $this->app->instance('HelpSpot\API', $api);
            ```
        - binding primitives
            - 클래스가아닌 정수형과 같은 기본 타입 값을 바인딩해 의존서으로          ```
            $this->app->when('App\Http\Controllers\UserController')
             ->needs('$variableName')
             ->give($value);
            ```         
        - binding interfaces
            - 구현 객체를 인터페이스형으로 바인등
            - 특정 인터페이스를 의존성 주입시 구현객체를 주입
            ```
            $this->app->bind(
             'App\Contracts\EventPusher',
             'App\Services\RedisEventPusher'
              );
            ```
        - contextual binding
            - 특정 인터페이스에 두가지 구현체가 있을시 각각 원하는 곳에 원하는 구현객체를 주입
            ```
            $this->app->when(PhotoController::class)
             ->needs(Filesystem::class)
             ->give(function () {
              return Storage::disk('local');
                });

            $this->app->when([VideoController::class, UploadController::class])
             ->needs(Filesystem::class)
             ->give(function () {
             return Storage::disk('s3');
                });
            ```

    - tagging
        - 여러가지 바인딩된 객체를 테그를 통해 한이름으로 호출할 수있다.
        ```
        $this->app->extend('validator', function ($validator, $app) {
            $validator->setPresenceVerifier($app[RedisPresenceVerifier::class]);
            return $validator;
        });

        $this->app->bind('ReportAggregator', function ($app) {
         return new ReportAggregator($app->tagged('reports'));
        });

        });
        ```
    - extending bindings
        - 바인딩된 객체를 extend를 통해 서비스를 변경하거나 수정할수 있게 한다.
        - 기존 서비스를 대체한다
        ```
             $this->app->extend('validator', function ($validator, $app) {
            $validator->setPresenceVerifier($app[RedisPresenceVerifier::class]);
            return $validator;
        });
        ```
## 바인딩된 객체 받아오기
    - make 메서드 사용
     ```
     $api = $this->app->make('HelpSpot\API');
     ```
    - $app변수에 접근 불가시에는 resolve 라는 헬퍼 함수 사용
    ``` 
     $api = resolve('HelpSpot\API');
    ```
   - makeWith이라는 메서드로 객체를 생성에 필요한 인자를 넘겨서 생성후 받아오기 가능
    ```
     $api = $this->app->makeWith('HelpSpot\API', ['id' => 1]);
    ```   
   - setter 또는 생성자로 자동으로 받아오기
    ```
        public function __construct(UserRepository $users)
    {
        $this->users = $users;
    }
    ``` 
## 컨테이너 이벤트
    - resolving을 통해 객체주입시 발생하는 이벤트리스너를 등록할수있다.
    ```
    $this->app->resolving(function ($object, $app) {
    // Called when container resolves object of any type...
    });

    $this->app->resolving(HelpSpot\API::class, function ($api, $app) {
    // Called when container resolves objects of type "HelpSpot\API"...
    });
    ```
    - 객체가 사용자에게 전달되기전에 수행 된다.

## ContainerInterface
    - 서비스 컨테이너는 ContainerInterface를 구현함으로 해당 형태로 서비스 컨테이너 인스턴스를 받아올수 있다.         

