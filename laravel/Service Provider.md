# Service Providers

## 1. 개요
  - 라라벨의 핵심 서비스들은 서비스 프로바이더에 의해 **bootstrapped** 됨.
  - **bootstrapping?** = **registering** things like:
    - service container binding
    - event listener
    - middleware
    - routes
~~~
class RouteServiceProvider extends ServiceProvider
{
    protected $namespace = 'App\Http\Controllers';

    public function boot()
    {
        Route::pattern('basic_info_no', '[0-9]+');
        Route::pattern('node_id', '[0-9]+');
        parent::boot();
    }

    public function map()
    {
        $this->mapApiRoutes();
        $this->mapWebRoutes();
    }

    protected function mapWebRoutes()
    {
        Route::middleware('web')
             ->namespace($this->namespace)
             ->group(base_path('routes/web.php'));
    }
    
    protected function mapApiRoutes()
    {
        Route::prefix('api')
             ->middleware('api')
             ->namespace($this->namespace)
             ->group(base_path('routes/api.php'));
    }
}
~~~

## 2. 기본 사용법
  - 모든 서비스 프로바이더는 Illuminate\Support\ServiceProvider 클래스를 상속해야 함
  - **config/app.php** 파일의 **providers** 배열에 서비스 프로바이더 클래스를 등록해야 함
    ~~~
    'providers' => [
        // Other Service Providers ...
        App\Providers\ComposerServiceProvider::class,
    ],
    ~~~
  - Artisan CLI 커맨드로 새로운 서비스 프로바이더 생성 가능
    ~~~
    php artisan make:provider RiakServiceProvider
    ~~~
  - 생성되는 기본 프로바이더와 설정
    ~~~
    <?php
    namespace App\Providers;
    
    use Illuminate\Support\ServiceProvider;
    
    class MyProvider extends ServiceProvider
    {
        /**
         * Register services.
         *
         * @return void
         */
        public function register() {}
    
        /**
         * Bootstrap services.
         *
         * @return void
         */
        public function boot() {}
    }
    ~~~

## 3. Register 메소드
  - 반드시 **서비스 컨테이너**에 대한 바인딩만 실시해야 함. 
    - 이벤트 리스너, 경로 등 다른 요소의 등록을 시도할 경우 아직 로드되지 않은 서비스 프로바이더가 제공하는 서비스를 사용하게 될 가능성이 있음.
~~~
<?php
namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use Riak\Connection;

class RiakServiceProvider extends ServiceProvider
{
    public function register()
    {
        $this->app->singleton(Connection::class, function ($app) {
            return new Connection(config('riak'));
        });
    }
}
~~~
  - 단순 바인딩이 다수 존재한다면 서비스 프로바이더의 binding 또는 singleton 프로퍼티를 활용 가능
~~~
<?php

namespace App\Providers;

use App\Contracts\DowntimeNotifier;
use App\Contracts\ServerProvider;
use App\Services\DigitalOceanServerProvider;
use App\Services\PingdomDowntimeNotifier;
use App\Services\ServerToolsProvider;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /*
    등록 형식:  key => 컨테이너에 의해 주입받을 대상(ex) 클래스, 인터페이스...)
              value => 주입할 실제 개체
     */
    public $bindings = [
        ServerProvider::class => DigitalOceanServerProvider::class,
    ];
    
    /* 싱글톤 컨테이너로 주입 */
    public $singletons = [
        DowntimeNotifier::class => PingdomDowntimeNotifier::class,
        ServerToolsProvider::class => ServerToolsProvider::class,
    ];
}
~~~

## 3. Boot 메소드
  - **모든 서비스 프로바이더가 등록된 후 호출** -> register 이후
  - 타 서비스 프로바이더에 의해서 등록된 모든 서비스들에 접근 가능
~~~
<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;

class ComposerServiceProvider extends ServiceProvider
{
    public function boot()
    {
        view()->composer('view', function () {
            //View Composer: view가 그려진 후 호출되는 콜백 클로저 또는 클래스 메소드
        });
    }
~~~
  - 서비스 컨테이너를 통해 주입된 의존성을 활용 가능
~~~
use Illuminate\Contracts\Routing\ResponseFactory;

/* ResponseFactory에 대해 주입된 의존성에 의해 type-hint가 적용되어 패러미터가 자동으로 생성 */
public function boot(ResponseFactory $response)
{
    $response->macro('caps', function ($value) {
        //
    });
}
~~~

## 4. Deferred Provider
   - **서비스 컨테이너만을 바인딩하는** 서비스 프로바이더라면 지연 로딩이 가능
     - 실제 바인딩이 필요할 때까지 로딩을 지연
     - 파일시스템에 의해 매번 로딩되지 않으므로 성능 향상을 기대할 수 있음
  - 지연 프로바이더의 경우 프로바이더의 이름과 해당 프로바이더에 의해 제공되는 서비스를 저장해 두고, 실제 서비스 중 하나가 resolve할 때 프로바이더를 로딩
  - 인터페이스인 **\Illuminate\Contracts\Support\DeferrableProvider**를 상속하여 **provides** 메소드를 구현해야 함
~~~
<?php

namespace App\Providers;

use Illuminate\Contracts\Support\DeferrableProvider;
use Illuminate\Support\ServiceProvider;
use Riak\Connection;

class RiakServiceProvider extends ServiceProvider implements DeferrableProvider
{
    public function register()
    {
        $this->app->singleton(Connection::class, function ($app) {
            return new Connection($app['config']['riak']);
        });
    }

    /*
    이 프로바이더가 제공하는 모든 서비스 컨테이너 바인딩을 배열로 리턴
     */
    public function provides()
    {
        return [Connection::class];
    }
}
~~~

* DeferrableProvider를 구현해도 정상적으로 지연 로딩이 발생하지 않는 현상?