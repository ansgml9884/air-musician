# AirMusician

<img src = "https://user-images.githubusercontent.com/68583697/103190778-c00ab480-4915-11eb-96fd-777a3cd755f0.png" width="300px">
플레이데이터 Pose-Estimation AI 개발자 교육 과정 final 프로젝트
AI 포즈 인식 기반 가상 악기 앱 개발 프로젝트

## 팀원소개

<table>
    <thead>
        <tr>
            <th>역할</th>
            <th>이름</th>
            <th>세부 사항</th>
            <th>세부 구현 내용</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td > 책임개발자 </td>
            <td ><a href = "https://github.com/ansgml9884" target = "blank" > 김문희 </a></td>
            <td >- 프로젝트 관리<br>- AI환경 셋팅 및 모델 조사<br>- AI와 App 연동</td>
            <td >• Http통신 구현<br>• 데이터셋 수집</td>
        </tr>
        <tr>
            <td rowspan="2"> 백엔드 </td>
            <td ><a href = "" target = "blank" > 이재형 </a></td>
            <td >- AI환경 셋팅 및 모델 조사<br>- AI와 App 연동</td>
            <td >• Http통신 구현<br>• 데이터셋 수집</td>
        </tr>
        <tr>
            <td rowspan="2"><a href = "https://github.com/parkjunoo" target = "blank" > 박준수 </a></td>
            <td >- 안드로이드  내부 DB구현</td>
            <td >• MediaStore를 활용한 내부 저장소 DB 구현</td>
        </tr>
        <tr>
            <td rowspan="2"> 프론트엔드 </td>
            <td rowspan="2" >- Android App 설계 및 개발</td>
            <td >• SoundPool 효과음 출력 구현<br>• 오디오 녹음 기능 구현</td>
        </tr>
        <tr>
            <td ><a href = "https://github.com/calmdownyoung" target = "blank" > 오나영 </a></td>
            <td >• 문서작성<br> • UI/UX 디자인<br>• App interface 구현</td>
        </tr>
    </tbody>
</table>

### 프로젝트 기간 
#### 2020-10-17 ~ 2020-12-23 
<br>
<img src = "https://user-images.githubusercontent.com/68583697/103226691-0ee53800-4970-11eb-8eb1-cb851b4c93bb.PNG" width="750px">

## 사용언어, 기술스택
![Generic badge](https://img.shields.io/badge/platform-Mobile-green.svg) ![Generic badge](https://img.shields.io/badge/OS-Android-brightgreen.svg)
![Generic badge](https://img.shields.io/badge/database-MediaStore-yellow.svg) ![Generic badge](https://img.shields.io/badge/model-MediaPipe,Mxnet-blue.svg) ![Generic badge](https://img.shields.io/badge/language-Java,Python-important.svg) 
<br>
![Generic badge](https://img.shields.io/badge/cloud-AWS(Sagemaker,Lambda,APIGateway)-red.svg)

<img src = "https://user-images.githubusercontent.com/68583697/103214616-3e864700-4954-11eb-9a7b-fe96bf52ccf8.PNG" width="750px">
<br>
<img src = "https://user-images.githubusercontent.com/68583697/103214636-4c3bcc80-4954-11eb-8a3e-e1982d359390.PNG" width="750px">
<br>

## 애플리케이션 아키텍처

<img src = "https://user-images.githubusercontent.com/68583697/103211168-ccf5cb00-494a-11eb-9ab2-2b87b9104943.PNG" width="750px">
<br>
<img src = "https://user-images.githubusercontent.com/68583697/103211262-0f1f0c80-494b-11eb-8b3f-cad8e0081fd6.PNG" width="750px">
<br>







This is an example of using MediaPipe AAR in Android Studio with Gradle.

The steps to build and use MediaPipe AAR is documented in MediaPipe's [android_archive_library.md](https://github.com/google/mediapipe/blob/master/mediapipe/docs/android_archive_library.md). The source code is copied from MediaPipe's [multi-hand tracking gpu demo](https://github.com/google/mediapipe/tree/master/mediapipe/examples/android/src/java/com/google/mediapipe/apps/multihandtrackinggpu).
