# oauth 인증


* refresh token 만료기간이 없나?
- refresh token는 기간단위로 만료되지않느다. 보통은 (구글)

* backend 에서는 access token는 어디다 저장하고 쓰나?

* front 에서는 accesstoken어디에다 두고 헤더에 보내주나
    1. 웹 스토리지 (localStorage 혹은 sessionStorage)
        - 웹 요청을 할 때마다 HTTP 헤더 값에 넣어서 요청하는 방법이 있습니다.
        - 이 방법은, 구현하기 쉽고, 하나의 도메인에 제한되어있지 않다는 장점이 있지만, XSS 해킹 공격을 통하여 해커의 악성스크립트에 노출이 되는 경우 매우 쉽게 토큰이 탈취 될 수 있습니다 localStorage 에 접근하면 바로 토큰에 접근 할 수 있기 때문이죠.
    2. 토큰을 쿠키에 넣는 것 입니다.
        - 쿠키를 쓰지 않으려고 토큰을 쓰는 줄 알았는데, 갑자기 이렇게 설명을 하니 헷갈릴 수도 있습니다. 쿠키를 사용한다고해서 세션을 관리하는것은 아니고, 그저 쿠키를 정보 전송수단으로 사용 할 뿐입니다.
        - 서버측에서 응답을 하면서 쿠키를 설정 해 줄 때 httpOnly 값을 활성화를 해주면, 네트워크 통신 상에서만 해당 쿠키가 붙게 됩니다. 따라서, 브라우저상에서는, 자바스크립트로 토큰값에 접근하는것이 불가능해지죠.
        - 만약에 모바일 어플리케이션도 개발을 하게 된다면, 서버측에서 JWT 를 헤더 값으로도 받을 수 있게 하여 쿠키 혹은 헤더 둘 중 존재하는것을 토큰으로 인식하여 사용하도록 구현을 하면 됩니다.
        - 단점은, 쿠키가 한정된 도메인에서만 사용이 된다는 점 입니다. 이 부분은, 토큰이 필요해질 때 현재 쿠키에 있는 토큰을 사용하여 새 토큰을 문자열로 받아올 수 있게 하는 API를 구현하여 해결하면 됩니다.
        - 또 다른 단점은, XSS의 위험에서 완벽히 해방되는 대신, CSRF 공격의 위험성이 생긴다는 점 입니다.
        - CSRF의 경우엔 HTTP 요청 레퍼러 체크, 그리고 CSRF 토큰의 사용을 통하여 방지 할 수 있습니다.
     3. 스피링부트에서 쿠기 설정헤서 리턴하는 법
        ```
        @GetMapping("/change-username")
        public String setCookie(HttpServletResponse response) {
        // create a cookie
        Cookie cookie = new Cookie("username", "Jovan");
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days    
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // global cookie accessible every where
        //add cookie to response
        response.addCookie(cookie);
        return "이동페이지";

* access token 는 로그인 마다 새로 요청해서 받아오나? 그럼 유저당 따로 발급을 받는건가?

* redis vs spring session

* 프론트와 통신시 access token은 만료시 어떻게 설정 / 바로 그냥 재발급 아님 어떤식으로 관리

* github oauth 인증시 redirect url
-The redirect_uri parameter is optional. If left out, GitHub will redirect users to the callback URL configured in the OAuth Application settings. If provided, the redirect URL's host and port must exactly match the callback URL. The redirect URL's path must reference a subdirectory of the callback URL.
