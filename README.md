# boilerplate-gateway
gateway 기본 구조 작업


# 참조링크
```yaml

https://ykh6242.tistory.com/entry/Spring-Cloud-Gateway%EA%B0%80-netty-%EA%B8%B0%EB%B0%98-reactive-web-application%EC%9C%BC%EB%A1%9C-%EA%B5%AC%EB%8F%99%EB%90%98%EB%8A%94-%EC%9D%B4%EC%9C%A0

https://developer-youn.tistory.com/180


```
#github 검색 방법

spring cloud gateway pushed:>2023-01-01 stars:>2 language:java

```text
GitHub에서 "Spring Cloud Gateway"와 관련된 리포지토리를 검색하기 위해 사용할 수 있는 몇 가지 검색 쿼리 예시를 아래에 제공합니다. 이 예시들은 GitHub 검색창에 직접 입력할 수 있습니다:

기본 검색:
Copy code
spring cloud gateway
이 쿼리는 "Spring Cloud Gateway"라는 단어가 포함된 모든 리포지토리를 검색합니다.

언어 필터:
sql
Copy code
spring cloud gateway language:java
Java 언어로 작성된 "Spring Cloud Gateway" 관련 리포지토리를 검색합니다.

별점 필터:
Copy code
spring cloud gateway stars:>50
별점이 50개 이상인 "Spring Cloud Gateway" 관련 리포지토리를 검색합니다.

최근 업데이트:
Copy code
spring cloud gateway pushed:>2023-01-01
2023년 1월 1일 이후에 업데이트된 "Spring Cloud Gateway" 관련 리포지토리를 검색합니다.

코드, 설명, README 파일 검색:
Copy code
spring cloud gateway in:name,readme,description
이름, README 또는 설명에 "Spring Cloud Gateway"라는 단어가 포함된 리포지토리를 검색합니다.

정렬 옵션 추가:
bash
Copy code
spring cloud gateway stars:>10 sort:stars-desc
별점이 10개 이상인 "Spring Cloud Gateway" 관련 리포지토리 중에서 별점이 높은 순으로 정렬하여 검색합니다.
```


# docker 및 docker-compose 3개의서버 구성

```shell
docker-compose up --build
```


# plantuml sequance uml
```plantuml
@startuml
/' 이니시스 정기결제 - PC '/
/'
 
색상 정보
- 그룹사 - #d9d2e9
- psgservice - #d9ead3
- 이니시스 - #fce5cd
 
'/ 
participant 그룹사 #d9d2e9
participant layer_popup #d2e2e9
participant inicis_layout #fce5cd
participant gateway #d9ead3
participant psgservice #d9ead3
participant inicis #fce5cd
 
== 카드등록 ==
 
note left of 그룹사 #d9d2e9: 그룹사 레이어팝업 노출
그룹사 -> gateway: 주문서(카드등록) 인증요청\n Required Headers : accessToken\n psg-service api 필수정보(returnUrl포함)
activate gateway
 
gateway --> gateway: headers 생성 \n clientIp, userAgent \n accessToken 정보 검증
gateway -> psgservice: gateway 에서 생성된 headers 정보 및 \n group사에서 요청한 psg-service api 호출
activate psgservice
 
psgservice --> psgservice: 인증전문 생성(callback Url 포함)
psgservice --> gateway : 인증전문, certify.jsp 페이지 전달
deactivate psgservice
 
gateway --> layer_popup: 인증전문, certify.jsp 페이지 전달
deactivate gateway
activate layer_popup
note left of layer_popup #d2e2e9: certify.jsp 페이지 노출\n(빈화면)
 
layer_popup -> inicis: 인증요청\n인증페이지(INIStdPay.js) 호출
activate inicis
 
inicis --> inicis: 인증전문 확인
inicis --> inicis_layout: 인증전문 확인 및 카드입력 페이지 전달
deactivate inicis
activate inicis_layout
note left of inicis_layout #fce5cd: inicis 카드입력 페이지 노출
 
inicis_layout -> inicis_layout: 카드정보 입력
inicis_layout -> inicis: 카드정보 등록 요청
activate inicis
 
inicis --> inicis: 카드정보 검증
inicis --> inicis_layout: 카드정보 검증 결과 전달
deactivate inicis
inicis_layout --> layer_popup: 카드정보 검증 결과 전달
deactivate inicis_layout
layer_popup --> 그룹사: layer close
deactivate layer_popup
 
그룹사 -> gateway : 빌키 승인 요청(psgservice callback Url)
 
activate gateway
 
gateway -> psgservice: 빌키 승인 요청(psgservice callback Url)
deactivate inicis
activate psgservice
 
psgservice --> psgservice: 빌키(billKey)승인 전문생성
psgservice -> inicis: 빌키(billKey)승인 요청
activate inicis
 
inicis --> inicis: 빌키(billKey)승인전문 확인 및 빌키발급
inicis --> psgservice: 빌키 승인 결과(응답결과, billKey,마스킹카드번호) 전달\nresult.jsp 페이지 생성
deactivate inicis
 
psgservice --> gateway: 빌키 승인 결과(응답결과, billKey,마스킹카드번호)\nresult.jsp 페이지 전달
deactivate psgservice
 
gateway --> 그룹사: 빌키 승인 결과(응답결과, billKey,마스킹카드번호)\nresult.jsp 페이지 전달
deactivate gateway
note left of 그룹사 #d9d2e9: result.jsp 페이지 노출\n(빈화면)
 
그룹사 --> 그룹사: 그룹사 returnUrl 호출 - submit()
deactivate layer_popup
note left of 그룹사 #d9d2e9: 그룹사 returnUrl 페이지 노출
 
== 결제요청 ==
그룹사 -> gateway: 승인요청\n Required Headers : accessToken, device, siteCode \n psg-service api 필수정보
activate gateway
 
gateway --> gateway: headers 생성 \n clientIp, userAgent \n accessToken 정보 검증
gateway -> psgservice: gateway 에서 생성된 headers 정보 및 \n group사에서 요청한 psg-service api 호출
activate psgservice
 
psgservice --> psgservice: 승인 전문생성
psgservice -> inicis: 승인요청
activate inicis
 
inicis --> inicis: 승인전문 확인
inicis --> psgservice: 승인결과 전달
deactivate inicis
 
psgservice --> gateway: 승인결과 전달
deactivate psgservice
 
gateway --> 그룹사: 승인결과 전달
deactivate gateway
@enduml
```