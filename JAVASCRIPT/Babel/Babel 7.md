# Babel 7 사용법
바벨은 ES2015+ 문법을 사용한 코드를 예전 ES5 자바스크립트 코드로 바꿔주는 도구
바벨은 **문법만** 바꿔지기 때문에 es2015새로운 **객체**(Promise,Map,Set 등등)과 **메서드**(_array.find_,_Object.assing_ 등은)사용불가 그래서 **babel-polyfill** 도 따로 필요합니다.

* 설치법
1. 적당한 폴더를 만든후 그폴더로 이동후에 cmd창에서 _npm init -y_ 을 실시한다. (*npm이 사전에 설치되어있어야함*)
2. 이후 cmd 창에서  _npm install --save-dev @babel/core @babel/cli @babel/preset-env_
3.  _npm install @babel/polyfill_ 또한 진행한다.
4. .babelrc 파일을 프로젝트 루트에 생성한다. (*babelrc는 파일확장자명이다.*)
5. .babelrc 파일에 @babel/preset-env룰 사용하겠다고 명시하고 여기서 option으로 특정 브라우저의 환경에 맞춰 트랜스파일링을 진행할수 있다.(ex. ie11, sourceType을 주면 트랜스파일링시 자동 추가되는 'use strict' 제거할수있다.)
  ```
  {
  "presets": [
    ["@babel/preset-env", {
      "targets": {
        "ie": 11,
      }
    }]
  ],
  "sourceType": "script"
}
  ```
6. package.json 파일에 scripts를 추가한다. _npm build_ 를 실시할시 수행되는 명령문을 build에 명시할수있다.
  ```
  {
  "name": "babel-test",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "@babel/cli": "^7.4.4",
    "@babel/core": "^7.4.5",
    "@babel/preset-env": "^7.4.5"
  },
  "dependencies": {
    "@babel/polyfill": "^7.4.4"
  },
  "scripts": {
    "build": "babel ./src -d ./lib"
  }

}
  ```
  * 완성된 package.json의 예이다 여기서 build부분 ./src 가 작업을 수행할 파일들이 위치한곳이고 -d 다음에는 결과물이 저장될 위치를 지정할수있다.(--out-dir 축양형).
  * 또한 원한다면 -d 앞에 -w 옵션을 추가해서 타깃폴더내 모든 파일의 변경을 감지하여 자동으로 트랜스파일한다.

7. _npm run build_ 를 프로젝트 폴더에서 실시하면 자동으로 작업이 실시된다.
8. polyfill 을 사용해야할시 가장 먼저 로딩되는 스크립트 제일위에 `import '@babel/polyfill'`
 * 만약 스크립트여러개가 한 jsp 나 html파일에서 로딩되는 상황이라면 프로젝트루트\node_modules\@babel\polyfill\dist 밑에있는 polyfiil.js 또는 polyfill.min.js둘중하나를 <script
		src="polyfill 파일 경로"></script> 이렇게 가져온다.

    참고 링크: <https://poiemaweb.com/es6-babel-webpack-1>
