# JS sessionStorage
 * sessinStorage는 스프링의 request.getSession()과는 다른 독립적인 브라우저의 세션 공간 이다.
 * 또한 sessionStorage는 모든 데이터를 string 형식으로 저장하기 때문에 저장하기전에 JSON.stringify()를 해주어야 한다.
    > sessionStorage.setItem('deletedItems', JSON.stringify(array)).

 * sessionStorage에서 가지고 온데이터도 JSON 객체로 바꿔줘야지 객체또는 배열처럼  사용할수 있다.

    > JSON.parse(sessionStorage .getItem('rotation'));


참고 링크: <https://medium.com/@alexbrbr/polyfills-and-transpilers-one-code-for-every-browser-abaa85145c9c>
