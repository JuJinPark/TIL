# 도커 이미지를 활용해서 back up email 띄우기
- 일단 도커 설치
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
- apache 설정 파일 수정하기 
    - Document Root php 소스에 public 폴더로 매칭 시키기
    - ServerName 설정하기
- local환경에 host 설정(포트는 잡지 않는다)-아파치 설정 파일 서버네임과 일치 시키기
    - 포트변경시
        ```
        In /etc/apache2/ports.conf, change the port as
            Listen 8079
        Then go to /etc/apache2/sites-enabled/000-default.conf

        And change the first line of apache 설정 파일

        <VirtualHost *: 8079>
        Now restart

        sudo service apache2 restart
        Apache will now listen on port 8079 
        ```
- apache 및 rpm 재시작
- 의존성 받아오기 composer install
- 서버이름으로 요청 해보기
