# 도커 이미지를 활용해서 back up email 띄우기
- 일단 도커 설치
- toolbox를 사용하면 로컬로 붙는게 아니라 도커 머신을 통해서 접속 된는거 같다 그래서 모든 portainer를 포함에 컨테이너 요청은 도커 머신 아아피로 한다
- docker on mac 은 로컬호스트로 붙었던거 같다.
- portainer 설치
- 이미지 받아오기 docker pull suhoag/hiworks-api-dev
- Entry Point 설정 	/docker-entrypoint.sh
- 포트 설정 80 , 80
- volume 설정  
```
container
/mail/01/gabiaholdings.com
 host
/c/Users/gm1702757/docker/dev/gabiaholdings.com
  
container
/var/www
host
/c/Users/gm1702757/git
  
container
/etc/apache2/certificate
host
/c/Users/gm1702757/docker/dev/certificate	

container
/etc/apache2/sites-enabled
host
/c/Users/gm1702757/docker/dev/sites-enabled	

```
- shared-api.conf 파일 예시
  ```
  <VirtualHost *:80>
      DocumentRoot "/var/www/shared-mail-api.hiworks.com/public"
      ServerName "pyj.shared-mail-api.hiworks.com"
  
      DirectoryIndex index.php
  
      <IfModule mod_rewrite.c>
          <IfModule mod_negotiation.c>
              Options -MultiViews -Indexes
          </IfModule>
  
          RewriteEngine On
  
          # Handle Authorization Header
          RewriteCond %{HTTP:Authorization} .
          RewriteRule .* - [E=HTTP_AUTHORIZATION:%{HTTP:Authorization}]
  
          # Redirect Trailing Slashes If Not A Folder...
          RewriteCond %{DOCUMENT_ROOT}%{REQUEST_FILENAME} !-d
          RewriteCond %{REQUEST_URI} (.+)/$
          RewriteRule ^ %1 [L,R=301]
  
          # Handle Front Controller...
          RewriteCond %{DOCUMENT_ROOT}%{REQUEST_FILENAME} !-d
          RewriteCond %{DOCUMENT_ROOT}%{REQUEST_FILENAME} !-f
          RewriteRule ^ /index.php [L]
      </IfModule>
  
      <FilesMatch \.php$>
          SetHandler "proxy:unix:/var/run/php/php7.2-fpm.sock|fcgi://localhost"
      </FilesMatch>
  
      ErrorLog "/var/log/apache2/sample.com.error_log"
      CustomLog "/var/log/apache2/sample.com.access_log" common
  </VirtualHost>
  ```
- apache 설정 파일 수정하기 
    - Document Root php 소스에 public 폴더로 매칭 시키기
    - ServerName 설정하기
- local환경에 host 설정(포트는 잡지 않는다)-아파치 설정 파일 서버네임과 일치 시키기
    - 포트변경시
        ```
        In /etc/apache2/ports.conf, change the port as
            Listen 8079
        Then go to /etc/apache2/sites-enabled/000-default.conf

        And change the first line of apache 설정 파일(shared-api.conf)

        <VirtualHost *: 8079>
        Now restart

        sudo service apache2 restart
        Apache will now listen on port 8079 
        ```
- apache 및 rpm 재시작
- 의존성 받아오기 composer install
- 서버이름으로 요청 해보기

- php storm에서 도커 기반 테스트 디버깅 구축법
 - php 리모트 부터 설절
 - test frame work 에서 interpreter 설정
    - php unit version 못잡을시 /opt/project/vendor/autoload.php 입력
 - debug는 크롬에서 세션 붙이기 첫번쨰라인에 break 옵셥 설정 후 테스하면 자동으로 서버를 잡는다. 이떄 혹시나 해서 전체 매핑도 같이 설정해주기   

- 맥에서 설치시 유념사항
    - localhost로 붙어야 한다(docker on window도 동일하수 있음)
    - 디버깅 설정은
    ```
    Php.ini 
    xdebug.remote_enable=true
    xdebug.remote_connect_back=0
    xdebug.idekey=PHPSTORM
    xdebug.remote_host=host.docker.internal
    ```
  
    - php storm 에서 디버깅 서버 잘안잡일떄는 강제로 서버 네임 넣고 껏다켜보고 다시 지우고 해보