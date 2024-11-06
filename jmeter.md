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
test 진행할 REST API 등록  
등록전 정상적으로 실행가능한지 확인필요
![스크린샷 2024-11-05 오후 6 06 19](https://github.com/user-attachments/assets/141085e6-5341-46ff-94f8-b2f7be5e172f)
