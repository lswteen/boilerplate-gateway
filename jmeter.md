# Jmeter
참고링크 : 
https://curiousjinan.tistory.com/entry/mac-m1-jmeter-setup-and-testing

# JMeter Download
https://jmeter.apache.org/download_jmeter.cgi
Binaries : apache-jmeter-5.6.3.tgz

![스크린샷 2024-11-05 오후 4 41 43](https://github.com/user-attachments/assets/9ea04fed-bdf0-4cc0-adce-80071b9616f5)


## 설치 가능한 폴더 이동
```shell
cd /Users/milk/apache-jmeter-5.6.3/bin

m1 jmeter 실행
milk@milkui-Macmini bin % ./jmeter
```
![스크린샷 2024-11-05 오후 4 46 52](https://github.com/user-attachments/assets/8225b663-d229-4bb7-92d9-54fcda2ec0bc)

## 실행순서
### STEP1
Test Plan > Add > Thread(Users) > Thread Group
![스크린샷 2024-11-05 오후 5 45 42](https://github.com/user-attachments/assets/4868d864-f3b7-41bb-a25c-7a7ed7369025)

### STEP2
```text
thread 설정
```
![스크린샷 2024-11-05 오후 5 52 17](https://github.com/user-attachments/assets/30c2fd94-a2de-4f2b-bd59-231fa13695f8)

### STEP3 
```test
테스트 진행할 프로토콜 
```
![스크린샷 2024-11-05 오후 5 53 46](https://github.com/user-attachments/assets/69d3d473-6017-416a-87c4-bc3e859b0d90)

### STEP4
```text
Plan > Group > 선행조건 Header > Test API 등록 

Thread Group (스레드 그룹)
설명: 테스트할 사용자(스레드)와 부하 설정을 정의합니다. 사용자가 서버에 요청을 보내는 방식과 속도를 조정하는 요소입니다.

중요 설정 :
- Number of Threads (users): 가상의 사용자 수를 설정합니다.
- Ramp-Up Period: 설정된 사용자 수가 증가하는 데 걸리는 시간으로, 서서히 부하를 증가시킬 때 사용합니다.
- Loop Count: 각 스레드가 반복할 횟수입니다. 무한 반복 옵션을 통해 지정된 시간 동안 테스트할 수도 있습니다.
- Action to be taken after a Sampler error: 오류 발생 시 처리 방식을 선택합니다 (예: 계속 진행, 중단).
```

```text
Pre-Processor (선행 조건)

설명: 샘플러가 실행되기 전에 특정 작업을 수행하도록 하는 요소로, 테스트 데이터나 환경 설정 등을 준비할 때 유용합니다.

주요 유형 :
- User Parameters: 사용자별 데이터를 할당할 수 있습니다.
- BeanShell PreProcessor: 커스텀 스크립트를 통해 복잡한 논리나 데이터 처리 과정을 설정할 수 있습니다.
- HTTP URL Re-writing Modifier: 세션 ID와 같은 URL 데이터를 추가하는 데 유용합니다.
```

```text
Samplers (API 테스트)

설명: 실제 요청을 보내는 요소로, API 테스트에서는 HTTP Sampler가 주로 사용됩니다.
중요 설정:
- HTTP Request: REST API 요청을 구성하는 데 사용되며, 메서드(GET, POST 등), URL, 포트 등을 설정합니다.
- Body Data: POST/PUT 요청 시 JSON 또는 XML과 같은 데이터를 여기에 넣습니다.
- Content-Type: HTTP Header Manager에서 Content-Type을 application/json 등으로 설정하여 서버가 데이터를 올바르게 처리할 수 있도록 합니다.
```

```text
Samplers (API 테스트)

설명: 실제 요청을 보내는 요소로, API 테스트에서는 HTTP Sampler가 주로 사용됩니다.
중요 설정:
- HTTP Request: REST API 요청을 구성하는 데 사용되며, 메서드(GET, POST 등), URL, 포트 등을 설정합니다.
- Body Data: POST/PUT 요청 시 JSON 또는 XML과 같은 데이터를 여기에 넣습니다.
- Content-Type: HTTP Header Manager에서 Content-Type을 application/json 등으로 설정하여 서버가 데이터를 올바르게 처리할 수 있도록 합니다.
```
![스크린샷 2024-11-08 오후 4 04 20](https://github.com/user-attachments/assets/65c35090-aea6-4c1b-af6d-31d631efe8e6)


### STEP5 

```text
View Result Tree
Plan 에서 등록된 API 실행 이후 성공, 실패에 대한 Response 확인 가능

시나리오 테스트전 필수로 1회 실행후 스크립트 검증 
```
![스크린샷 2024-11-08 오후 4 20 20](https://github.com/user-attachments/assets/6f2dba1b-16cc-4c25-b291-610233750540)

```text
실패의 경우 Response를 확인 이후 
호출되는 인스턴스 검증 필요

실패원인 : Rest API Content-type : application/json 미설정
```
![스크린샷 2024-11-08 오후 4 36 19](https://github.com/user-attachments/assets/664045e8-6a22-4440-a59a-0e767692e72a)

```text
Group > Add > Config Element > HTTP Header Manager
```
![스크린샷 2024-11-08 오후 4 44 38](https://github.com/user-attachments/assets/67111922-a19e-43b1-9ab2-9e000cd4bd6f)

### STEP6

