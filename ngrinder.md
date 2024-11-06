# nGrinder Login

## Download
https://github.com/naver/ngrinder/releases

## 선행조건
mac 또는 window 모두 Java11 버전으로 실행가능한상태로 설정필요

### JAVA
```shell
 mkdir -p ~/.jenv/versions
milk@milkui-Macmini ngrinder % jenv add /opt/homebrew/Cellar/openjdk@11/11.0.25/libexec/openjdk.jdk/Contents/Home
```

자바버전 정보가 17에서 11로 변경되지 않을경우   
아래와같은 상황 발생
```shell
milk@milkui-Macmini ngrinder % jenv global 11.0
milk@milkui-Macmini ngrinder % java --version
openjdk 17.0.13 2024-10-15 LTS
OpenJDK Runtime Environment Corretto-17.0.13.11.1 (build 17.0.13+11-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.13.11.1 (build 17.0.13+11-LTS, mixed mode, sharing)
```
jenv가 현재 셸에서 활성화되도록 PATH에 jenv 경로를 추가해야 합니다. ~/.zshrc 또는 ~/.bash_profile 파일에 다음 라인을 추가해보세요.
```shell
export PATH="$HOME/.jenv/bin:$PATH"
eval "$(jenv init -)"
```
변경된 파일 적용
```shell
source ~/.zshrc  # 또는 source ~/.bash_profile
```
jenv가 Java 버전을 인식하도록 rehash 명령어를 실행해 주세요.

```shell
jenv rehash
```

java 11버전으로 글로벌 변경
```shell
jenv global 11.0
```

java version 확인
```shell
java -version
```

## 
```shell
-rw-r--r--@  1 milk  staff  155997612 11  5 12:42 ngrinder-controller-3.5.9-p1.war
milk@milkui-Macmini ngrinder % chmod 755 /Users/milk/ngrinder
milk@milkui-Macmini ngrinder % java -Djava.io.tmpdir=/Users/milk/ngrinder/lib -jar ngrinder-controller-3.5.9-p1.war --port=8300
```