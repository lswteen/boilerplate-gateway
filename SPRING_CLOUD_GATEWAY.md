# Spring Cloud Gateway 4.1.3 
## (2024.05.11 작업 한글번역 GPT 4.0 java springboot bot 제공)

원문링크 : https://docs.spring.io/spring-cloud-gateway/reference/index.html

# Spring Cloud Gateway

이 프로젝트는 Spring 6, Spring Boot 3 및 Project Reactor를 포함한 Spring 생태계를 기반으로 구축된 API 게이트웨이를 제공합니다. Spring Cloud Gateway는 API로 라우팅하고 보안, 모니터링/메트릭 및 회복탄력성(resiliency)과 같은 횡단 관심사(cross cutting concerns)를 제공하는 간단하면서도 효과적인 방법을 제공하는 것을 목표로 합니다.
Spring Cloud Gateway에는 두 가지 유형이 있습니다: Server와 Proxy Exchange. 각 유형은 WebFlux 및 MVC 호환성을 제공합니다.

- **Server 버전**은 독립 실행형 또는 Spring Boot 애플리케이션에 임베디드될 수 있는 전체 기능의 API 게이트웨이입니다.
- **Proxy Exchange 버전**은 주석 기반 WebFlux 또는 MVC 애플리케이션에서 독점적으로 사용되며, 웹 핸들러 메서드의 매개변수로 특별한 ProxyExchange 객체를 사용할 수 있습니다.

# Spring Cloud Gateway Reactive Server

## Spring Cloud Gateway 포함 방법

Spring Cloud Gateway를 프로젝트에 포함하려면 `org.springframework.cloud` 그룹 ID와 `spring-cloud-starter-gateway` 아티팩트 ID를 가진 스타터를 사용하십시오. 현재 Spring Cloud Release Train으로 빌드 시스템을 설정하는 방법에 대한 자세한 내용은 Spring Cloud Project 페이지를 참조하십시오.

스타터를 포함하지만 게이트웨이를 활성화하고 싶지 않은 경우, `spring.cloud.gateway.enabled=false`로 설정하십시오.

Spring Cloud Gateway는 Spring Boot, Spring WebFlux 및 Project Reactor를 기반으로 구축되었습니다. 그 결과, 많은 익숙한 동기식 라이브러리(예: Spring Data 및 Spring Security) 및 패턴이 Spring Cloud Gateway 사용 시 적용되지 않을 수 있습니다. 이러한 프로젝트에 익숙하지 않다면 Spring Cloud Gateway 작업을 시작하기 전에 이들의 문서를 읽어 새로운 개념을 익히는 것을 권장합니다.

Spring Cloud Gateway는 Spring Boot 및 Spring Webflux에서 제공하는 Netty 런타임을 필요로 합니다. 전통적인 서블릿 컨테이너 또는 WAR로 빌드할 때는 작동하지 않습니다.

## 용어집

- **Route**: 게이트웨이의 기본 구성 요소입니다. ID, 대상 URI, 프레디케이트(predictes) 모음, 필터(filters) 모음으로 정의됩니다. 집합 프레디케이트가 true이면 라우트가 일치합니다.

- **Predicate**: Java 8의 Function Predicate입니다. 입력 유형은 Spring Framework의 `ServerWebExchange`입니다. 이를 통해 헤더나 매개변수와 같은 HTTP 요청의 모든 항목과 일치시킬 수 있습니다.

- **Filter**: 특정 팩토리로 구성된 `GatewayFilter` 인스턴스입니다. 여기서 하류 요청을 보내기 전이나 후에 요청 및 응답을 수정할 수 있습니다.

## 작동 원리

다음 다이어그램은 Spring Cloud Gateway의 작동 방식을 높은 수준에서 개괄적으로 보여줍니다
![스크린샷 2024-05-11 오전 10.52.37.png](..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-11%20%EC%98%A4%EC%A0%84%2010.52.37.png)

클라이언트는 Spring Cloud Gateway에 요청을 보냅니다. Gateway Handler Mapping이 요청이 라우트와 일치하는지 결정하면, 요청은 Gateway Web Handler로 전송됩니다. 이 핸들러는 요청을 해당 요청에 특화된 필터 체인을 통해 실행합니다. 필터가 점선으로 나누어져 있는 이유는 프록시 요청이 전송되기 전과 후에 필터가 각각 로직을 실행할 수 있기 때문입니다. 모든 "pre" 필터 로직이 실행된 후 프록시 요청이 이루어집니다. 프록시 요청이 이루어진 후 "post" 필터 로직이 실행됩니다.

포트가 없는 라우트에 정의된 URI는 HTTP와 HTTPS URI에 대해 각각 기본 포트 값인 80과 443을 갖습니다.
라우트 URI에 정의된 경로는 무시됩니다.


## Route Predicate 팩토리 및 Gateway Filter 팩토리 구성

프레디케이트와 필터를 구성하는 방법에는 두 가지가 있습니다: 단축 구성과 완전 확장된 인수입니다. 아래의 대부분의 예시는 단축 구성 방법을 사용합니다.

각 섹션의 첫 문장이나 두 번째 문장에 이름과 인수 이름이 코드로 나열됩니다. 인수는 일반적으로 단축 구성에 필요한 순서로 나열됩니다.

### 단축 구성

단축 구성은 필터 이름 뒤에 등호(=)를 쓰고, 그 뒤에 쉼표(,)로 구분된 인수 값을 쓰는 방식으로 인식됩니다.

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: https://example.org
        predicates:
        - Cookie=mycookie,mycookievalue
```

이전 샘플에서는 두 개의 인수, 즉 쿠키 이름 `mycookie`와 일치시킬 값 `mycookievalue`를 사용하여 `Cookie Route Predicate Factory`를 정의합니다.

### 완전 확장된 인수

완전 확장된 인수는 이름/값 쌍을 가진 표준 YAML 구성처럼 보입니다. 일반적으로 `name` 키와 `args` 키가 있습니다. `args` 키는 프레디케이트 또는 필터를 구성하기 위한 키-값 쌍의 맵입니다.

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: https://example.org
        predicates:
        - name: Cookie
          args:
            name: mycookie
            regexp: mycookievalue
```
다음은 위에서 설명한 `Cookie` 프레디케이트의 단축 구성에 대한 전체 구성입니다.


## Route Predicate 팩토리

Spring Cloud Gateway는 Spring WebFlux `HandlerMapping` 인프라의 일부로 라우트를 매칭합니다. Spring Cloud Gateway에는 많은 내장된 라우트 프레디케이트 팩토리가 포함되어 있습니다. 이러한 프레디케이트는 모두 HTTP 요청의 다양한 속성에 매칭됩니다. 여러 라우트 프레디케이트 팩토리를 논리적 AND 문으로 결합할 수 있습니다.

### After Route Predicate Factory

`After` 라우트 프레디케이트 팩토리는 하나의 매개변수, 즉 datetime(Java의 `ZonedDateTime`)을 받습니다. 이 프레디케이트는 지정된 datetime 이후에 발생하는 요청과 일치합니다. 다음 예시는 `After` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: https://example.org
        predicates:
        - After=2017-01-20T17:42:47.789-07:00[America/Denver]
```
이 라우트는 2017년 1월 20일 17:42(마운틴 타임, 덴버) 이후에 이루어진 모든 요청과 일치합니다.

### Before Route Predicate Factory

`Before` 라우트 프레디케이트 팩토리는 하나의 매개변수, 즉 datetime(Java의 `ZonedDateTime`)을 받습니다. 이 프레디케이트는 지정된 datetime 이전에 발생하는 요청과 일치합니다. 다음 예시는 `Before` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: before_route
        uri: https://example.org
        predicates:
        - Before=2017-01-20T17:42:47.789-07:00[America/Denver]
```
이 라우트는 2017년 1월 20일 17:42(마운틴 타임, 덴버) 이전에 이루어진 모든 요청과 일치합니다.


### Between Route Predicate Factory

`Between` 라우트 프레디케이트 팩토리는 두 개의 매개변수, `datetime1`과 `datetime2`를 받습니다. 이들은 Java의 `ZonedDateTime` 객체입니다. 이 프레디케이트는 `datetime1` 이후이면서 `datetime2` 이전에 발생하는 요청과 일치합니다. `datetime2` 매개변수는 반드시 `datetime1` 이후여야 합니다. 다음 예시는 `Between` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: between_route
          uri: https://example.org
          predicates:
            - Between=2017-01-20T17:42:47.789-07:00[America/Denver], 2017-01-21T17:42:47.789-07:00[America/Denver]
```
이 라우트는 2017년 1월 20일 17:42(마운틴 타임, 덴버) 이후이면서 2017년 1월 21일 17:42(마운틴 타임, 덴버) 이전에 이루어진 모든 요청과 일치합니다. 이는 유지보수 시간대에 유용할 수 있습니다.

### Cookie Route Predicate Factory

`Cookie` 라우트 프레디케이트 팩토리는 두 개의 매개변수, 쿠키 이름과 정규 표현식(Java의 정규 표현식)을 받습니다. 이 프레디케이트는 주어진 이름을 가진 쿠키 중 값이 정규 표현식과 일치하는 쿠키와 일치합니다. 다음 예시는 `Cookie` 라우트 프레디케이트 팩토리를 구성하는 방법을 보여줍니다:

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: cookie_route
          uri: https://example.org
          predicates:
            - Cookie=chocolate, ch.p
```

이 라우트는 이름이 chocolate인 쿠키 중 값이 ch.p 정규 표현식과 일치하는 쿠키를 가진 요청과 일치합니다.

### Header Route Predicate Factory

`Header` 라우트 프레디케이트 팩토리는 두 개의 매개변수, 헤더 이름과 정규 표현식(Java의 정규 표현식)을 받습니다. 이 프레디케이트는 주어진 이름을 가진 헤더 중 값이 정규 표현식과 일치하는 헤더와 일치합니다. 다음 예시는 `Header` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: header_route
          uri: https://example.org
          predicates:
            - Header=X-Request-Id, \d+
```

이 라우트는 X-Request-Id라는 이름의 헤더가 있고 그 값이 \d+ 정규 표현식(즉, 하나 이상의 숫자)과 일치하는 경우에 요청과 일치합니다.


### Host Route Predicate Factory

`Host` 라우트 프레디케이트 팩토리는 하나의 매개변수, 즉 호스트 이름 패턴 목록을 받습니다. 패턴은 `.`을 구분자로 사용하는 Ant 스타일 패턴입니다. 이 프레디케이트는 `Host` 헤더가 패턴과 일치하는 경우와 일치합니다. 다음 예시는 `Host` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: host_route
          uri: https://example.org
          predicates:
            - Host=**.somehost.org,**.anotherhost.org
```
URI 템플릿 변수(예: {sub}.myhost.org)도 지원됩니다.  
이 라우트는 Host 헤더의 값이 www.somehost.org 또는 beta.somehost.org 또는 www.anotherhost.org인 경우와 일치합니다.  
이 프레디케이트는 URI 템플릿 변수(예: 위 예시에서 정의된 sub)를 이름과 값의 맵으로 추출하여 ServerWebExchange.getAttributes() 에 
ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE로 정의된 키와 함께 저장합니다.  
그런 다음 이러한 값은 GatewayFilter 팩토리에서 사용할 수 있습니다.

### Method Route Predicate Factory

`Method` 라우트 프레디케이트 팩토리는 HTTP 메서드와 일치하는 하나 이상의 매개변수를 받는 `methods` 인수를 사용합니다. 다음 예시는 `Method` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: method_route
          uri: https://example.org
          predicates:
            - Method=GET,POST
```
이 라우트는 요청 메서드가 GET 또는 POST인 경우와 일치합니다.

### Path Route Predicate Factory

`Path` 라우트 프레디케이트 팩토리는 Spring `PathMatcher` 패턴 목록과 선택적 플래그 `matchTrailingSlash`(기본값은 `true`)를 받습니다.   
다음 예시는 `Path` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: path_route
          uri: https://example.org
          predicates:
            - Path=/red/{segment},/blue/{segment}
```
이 라우트는 요청 경로가 /red/1, /red/1/, /red/blue, /blue/green인 경우와 일치합니다.  
matchTrailingSlash가 false로 설정된 경우, 요청 경로 /red/1/은 일치하지 않습니다.  
이 프레디케이트는 URI 템플릿 변수(예: 위 예시에서 정의된 segment)를 이름과 값의 맵으로 추출하여   
ServerWebExchange.getAttributes()에 ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE   
로 정의된 키와 함께 저장합니다. 그런 다음 이러한 값은 GatewayFilter 팩토리에서 사용할 수 있습니다.  
이 변수를 쉽게 액세스할 수 있도록 하는 유틸리티 메서드(get)가 제공됩니다. 다음 예시는 get 메서드를 사용하는 방법을 보여줍니다:  

```java
Map<String, String> uriVariables = ServerWebExchangeUtils.getUriTemplateVariables(exchange);
String segment = uriVariables.get("segment");
```
### Query Route Predicate Factory
`Query` 라우트 프레디케이트 팩토리는 필수 매개변수인 `param`과 선택적 매개변수 `regexp`(Java의 정규 표현식)를 받습니다.   
다음 예시는 `Query` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: query_route
          uri: https://example.org
          predicates:
            - Query=green
```
위의 라우트는 요청에 green 쿼리 매개변수가 포함된 경우와 일치합니다.

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: query_route
          uri: https://example.org
          predicates:
            - Query=red, gree.
```
위의 라우트는 요청에 red 쿼리 매개변수가 포함되어 있고 그 값이 gree.   
정규 표현식과 일치하는 경우와 일치하므로 green 및 greet와 일치합니다.


### RemoteAddr Route Predicate Factory

`RemoteAddr` 라우트 프레디케이트 팩토리는 CIDR 표기법(IPv4 또는 IPv6) 문자열의 소스 목록(최소 크기 1)을 받습니다.   
예를 들어 `192.168.0.1/16`에서는 `192.168.0.1`이 IP 주소이고 `16`이 서브넷 마스크입니다. 다음 예시는 `RemoteAddr` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: remoteaddr_route
          uri: https://example.org
          predicates:
            - RemoteAddr=192.168.1.1/24
```
이 라우트는 요청의 원격 주소가 예를 들어 192.168.1.10인 경우와 일치합니다.



## Modifying the Way Remote Addresses Are Resolved (원격 주소 확인 방식 수정) 

기본적으로 `RemoteAddr` 라우트 프레디케이트 팩토리는 들어오는 요청의 원격 주소를 사용합니다.   
Spring Cloud Gateway가 프록시 레이어 뒤에 위치한 경우 실제 클라이언트 IP 주소와 일치하지 않을 수 있습니다.

사용자 지정 `RemoteAddressResolver`를 설정하여 원격 주소 확인 방식을 맞춤 설정할 수 있습니다.   
Spring Cloud Gateway는 `X-Forwarded-For` 헤더를 기반으로 하는 비기본 원격 주소 확인자인 `XForwardedRemoteAddressResolver`를 제공합니다.  

`XForwardedRemoteAddressResolver`에는 서로 다른 보안 접근 방식을 취하는 두 가지 정적 생성 메서드가 있습니다:  

- `XForwardedRemoteAddressResolver::trustAll`은 `X-Forwarded-For` 헤더에서 찾은 첫 번째 IP 주소를 항상 사용하는 `RemoteAddressResolver`를 반환합니다. 이 접근 방식은 스푸핑에 취약합니다. 악의적인 클라이언트가 `X-Forwarded-For`의 초기 값을 설정하면 리졸버에서 이를 수락할 수 있습니다.

- `XForwardedRemoteAddressResolver::maxTrustedIndex`는 Spring Cloud Gateway 앞에서 실행되는 신뢰할 수 있는 인프라의 수에 해당하는 인덱스를 받습니다.  
- 예를 들어, Spring Cloud Gateway가 HAProxy를 통해서만 액세스할 수 있는 경우 값은 `1`이어야 합니다.   
- Spring Cloud Gateway에 액세스하기 전에 신뢰할 수 있는 인프라의 홉이 두 개 필요한 경우 값은 `2`이어야 합니다.

다음 헤더 값을 고려해 보십시오:

```yaml
X-Forwarded-For: 0.0.0.1, 0.0.0.2, 0.0.0.3
```

다음 `maxTrustedIndex` 값은 다음 원격 주소를 제공합니다:

- `1`: `0.0.0.3`
- `2`: `0.0.0.2`
- `3`: `0.0.0.1`
- `4` 또는 `Integer.MAX_VALUE`: `0.0.0.1`

다음 예시는 Java로 동일한 구성을 달성하는 방법을 보여줍니다:

GatewayConfig.java
```java

RemoteAddressResolver resolver = XForwardedRemoteAddressResolver
    .maxTrustedIndex(1);

...

.route("direct-route",
    r -> r.remoteAddr("10.1.1.1", "10.10.1.1/24")
        .uri("https://downstream1")
)
.route("proxied-route",
    r -> r.remoteAddr(resolver, "10.10.1.1", "10.10.1.1/24")
        .uri("https://downstream2")
)
```

### Weight Route Predicate Factory

`Weight` 라우트 프레디케이트 팩토리는 두 개의 매개변수, `group`과 `weight`(정수)를 받습니다.   
가중치는 그룹별로 계산됩니다. 다음 예시는 `Weight` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: weight_high
          uri: https://weighthigh.org
          predicates:
            - Weight=group1, 8
        - id: weight_low
          uri: https://weightlow.org
          predicates:
            - Weight=group1, 2
```

### XForwarded Remote Addr Route Predicate Factory

`XForwarded Remote Addr` 라우트 프레디케이트 팩토리는 CIDR 표기법(IPv4 또는 IPv6) 문자열의 소스 목록(최소 크기 1)을 받습니다.  
예를 들어 `192.168.0.1/16`에서는 `192.168.0.1`이 IP 주소이고 `16`이 서브넷 마스크입니다.

이 라우트 프레디케이트는 `X-Forwarded-For` HTTP 헤더를 기반으로 요청을 필터링할 수 있게 해줍니다.

이는 로드 밸런서나 웹 애플리케이션 방화벽과 같은 리버스 프록시에서 사용될 수 있으며, 요청이 해당 리버스 프록시에서 사용하는   
신뢰할 수 있는 IP 주소 목록에서 온 경우에만 허용됩니다.

다음 예시는 `XForwardedRemoteAddr` 라우트 프레디케이트를 구성하는 방법을 보여줍니다:

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: xforwarded_remoteaddr_route
          uri: https://example.org
          predicates:
            - XForwardedRemoteAddr=192.168.1.1/24
```
이 라우트는 X-Forwarded-For 헤더에 192.168.1.10이 포함된 경우와 일치합니다.


---

## GatewayFilter 팩토리

라우트 필터는 들어오는 HTTP 요청 또는 나가는 HTTP 응답을 어떤 방식으로든 수정할 수 있게 해줍니다.   
라우트 필터는 특정 라우트에 범위가 지정됩니다. Spring Cloud Gateway에는 많은 내장된 `GatewayFilter` 팩토리가 포함되어 있습니다.

다음 필터를 사용하는 방법에 대한 보다 자세한 예시는 단위 테스트를 참조하십시오.

---

### AddRequestHeader GatewayFilter Factory

`AddRequestHeader` GatewayFilter 팩토리는 `name`과 `value` 매개변수를 받습니다.   
다음 예시는 `AddRequestHeader` GatewayFilter를 구성하는 방법을 보여줍니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: https://example.org
          filters:
            - AddRequestHeader=X-Request-red, blue
```
이 구성은 모든 일치하는 요청에 대해 X-Request-red: blue 헤더를 다운스트림 요청의 헤더에 추가합니다.

AddRequestHeader는 경로 또는 호스트와 일치하는 데 사용된 URI 변수를 인식합니다. URI 변수는 값에서 사용할 수 있으며 런타임에 확장됩니다.  
다음 예시는 변수를 사용하는 AddRequestHeader GatewayFilter를 구성하는 방법을 보여줍니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: add_request_header_route
          uri: https://example.org
          predicates:
            - Path=/red/{segment}
          filters:
            - AddRequestHeader=X-Request-Red, Blue-{segment}

```

---
### AddRequestHeadersIfNotPresent GatewayFilter Factory

`AddRequestHeadersIfNotPresent` GatewayFilter 팩토리는 이름과 값 쌍이 콜론으로 구분된 컬렉션을 받습니다.   
다음 예시는 `AddRequestHeadersIfNotPresent` GatewayFilter를 구성하는 방법을 보여줍니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: add_request_headers_route
          uri: https://example.org
          filters:
            - AddRequestHeadersIfNotPresent=X-Request-Color-1:blue,X-Request-Color-2:green
```
이 구성은 모든 일치하는 요청에 대해 X-Request-Color-1: blue 및 X-Request-Color-2: green 헤더를 다운스트림 요청의 헤더에 추가합니다.  
AddRequestHeader와 유사하지만, 헤더가 이미 존재하지 않는 경우에만 추가합니다. 그렇지 않으면 클라이언트 요청의 원래 값이 전송됩니다.

또한, 다중 값 헤더를 설정하려면 헤더 이름을 여러 번 사용하십시오.   
예를 들어 AddRequestHeadersIfNotPresent=X-Request-Color-1:blue,X-Request-Color-1:green와 같이 설정합니다.

AddRequestHeadersIfNotPresent는 경로 또는 호스트와 일치하는 데 사용된 URI 변수를 지원합니다.   
URI 변수는 값에서 사용할 수 있으며 런타임에 확장됩니다. 다음 예시는 변수를 사용하는 AddRequestHeadersIfNotPresent GatewayFilter를 구성하는 방법을 보여줍니다:

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: add_request_header_route
        uri: https://example.org
        predicates:
        - Path=/red/{segment}
        filters:
        - AddRequestHeadersIfNotPresent=X-Request-Red:Blue-{segment}
```

---

### AddRequestParameter GatewayFilter Factory

`AddRequestParameter` GatewayFilter 팩토리는 `name`과 `value` 매개변수를 받습니다.
다음 예시는 `AddRequestParameter` GatewayFilter를 구성하는 방법을 보여줍니다:

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_parameter_route
          uri: https://example.org
          filters:
            - AddRequestParameter=red, blue
```
이 구성은 모든 일치하는 요청에 대해 다운스트림 요청의 쿼리 문자열에 red=blue를 추가합니다.

AddRequestParameter는 경로 또는 호스트와 일치하는 데 사용된 URI 변수를 인식합니다. URI 변수는 값에서 사용할 수 있으며 런타임에 확장됩니다. 
다음 예시는 변수를 사용하는 AddRequestParameter GatewayFilter를 구성하는 방법을 보여줍니다:  

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_request_parameter_route
          uri: https://example.org
          predicates:
            - Host={segment}.myhost.org
          filters:
            - AddRequestParameter=foo, bar-{segment}

```

---
### AddResponseHeader GatewayFilter Factory

`AddResponseHeader` GatewayFilter 팩토리는 `name`과 `value` 매개변수를 받습니다.   
다음 예시는 `AddResponseHeader` GatewayFilter를 구성하는 방법을 보여줍니다:

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add_response_header_route
          uri: https://example.org
          filters:
            - AddResponseHeader=X-Response-Red, Blue
```
이 구성은 모든 일치하는 요청에 대해 다운스트림 응답의 헤더에 X-Response-Red: Blue 헤더를 추가합니다.

AddResponseHeader는 경로 또는 호스트와 일치하는 데 사용된 URI 변수를 인식합니다.  
URI 변수는 값에서 사용할 수 있으며 런타임에 확장됩니다.  
다음 예시는 변수를 사용하는 AddResponseHeader GatewayFilter를 구성하는 방법을 보여줍니다:  

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: add_response_header_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - AddResponseHeader=foo, bar-{segment}
```

---

### CircuitBreaker GatewayFilter Factory

`Spring Cloud CircuitBreaker` GatewayFilter 팩토리는   
Spring Cloud CircuitBreaker API를 사용하여 게이트웨이 라우트를 서킷 브레이커로 감쌉니다.   
Spring Cloud CircuitBreaker는 Spring Cloud Gateway와 함께 사용할 수 있는 여러 라이브러리를 지원합니다.   
Spring Cloud는 기본적으로 Resilience4J를 지원합니다.

Spring Cloud CircuitBreaker 필터를 활성화하려면 클래스패스에  
`spring-cloud-starter-circuitbreaker-reactor-resilience4j`를 추가해야 합니다.  
다음 예시는 Spring Cloud CircuitBreaker GatewayFilter를 구성하는 방법을 보여줍니다:  

application.yml
```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: circuitbreaker_route
          uri: https://example.org
          filters:
            - CircuitBreaker=myCircuitBreaker
```

서킷 브레이커를 구성하려면 사용하는 기본 서킷 브레이커 구현의 구성을 참조하십시오.  
https://cloud.spring.io/spring-cloud-circuitbreaker/reference/html/spring-cloud-circuitbreaker.html


`Spring Cloud CircuitBreaker` 필터는 선택적 `fallbackUri` 매개변수를 받을 수 있습니다.   
현재는 `forward:` 스킴 URI만 지원됩니다. 폴백이 호출되면 요청은 URI와 일치하는 컨트롤러로 전달됩니다.   
다음 예시는 이러한 폴백을 구성하는 방법을 보여줍니다:

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: circuitbreaker_route
          uri: lb://backing-service:8088
          predicates:
            - Path=/consumingServiceEndpoint
          filters:
            - name: CircuitBreaker
              args:
                name: myCircuitBreaker
                fallbackUri: forward:/inCaseOfFailureUseThis
            - RewritePath=/consumingServiceEndpoint, /backingServiceEndpoint
```

Application.java
```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("circuitbreaker_route", r -> r.path("/consumingServiceEndpoint")
            .filters(f -> f.circuitBreaker(c -> c.name("myCircuitBreaker").fallbackUri("forward:/inCaseOfFailureUseThis"))
                .rewritePath("/consumingServiceEndpoint", "/backingServiceEndpoint")).uri("lb://backing-service:8088")
        .build();
}
```
이 예제에서는 서킷 브레이커 폴백이 호출되면 `/inCaseofFailureUseThis` URI로 포워딩됩니다.   
이 예제는 또한 대상 URI에 `lb` 접두사가 정의된 Spring Cloud LoadBalancer 로드 밸런싱(선택 사항)을 보여줍니다.  

`CircuitBreaker`는 `fallbackUri`에서 URI 변수를 지원합니다.   
통해 원래 호스트 또는 URL 경로의 일부를 `PathPattern` 표현식을 사용하여 포워딩하는 등의 복잡한 라우팅 옵션이 가능합니다.  

아래 예시에서는 `consumingServiceEndpoint/users/1` 호출이 `inCaseOfFailureUseThis/users/1`로 리디렉션됩니다.

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: circuitbreaker_route
          uri: lb://backing-service:8088
          predicates:
            - Path=/consumingServiceEndpoint/{*segments}
          filters:
            - name: CircuitBreaker
              args:
                name: myCircuitBreaker
                fallbackUri: forward:/inCaseOfFailureUseThis/{segments}
```
주요 시나리오는 fallbackUri를 사용하여 게이트웨이 애플리케이션 내의 내부 컨트롤러 또는 핸들러를 정의하는 것입니다.   
그러나 다음과 같이 요청을 외부 애플리케이션의 컨트롤러나 핸들러로 다시 라우팅할 수도 있습니다:  

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: ingredients
        uri: lb://ingredients
        predicates:
        - Path=//ingredients/**
        filters:
        - name: CircuitBreaker
          args:
            name: fetchIngredients
            fallbackUri: forward:/fallback
      - id: ingredients-fallback
        uri: http://localhost:9994
        predicates:
        - Path=/fallback
```
이 예제에서는 게이트웨이 애플리케이션에 폴백 엔드포인트 또는 핸들러가 없습니다.  
그러나 `localhost:9994`에 등록된 다른 애플리케이션에 폴백 엔드포인트가 있습니다.  

요청이 폴백으로 포워딩될 경우, Spring Cloud CircuitBreaker Gateway 필터는 원인을 제공한 `Throwable`을 제공합니다.   
이는 `ServerWebExchange`에 `ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR`   
속성으로 추가되어 게이트웨이 애플리케이션 내에서 폴백을 처리할 때 사용할 수 있습니다.  

외부 컨트롤러/핸들러 시나리오의 경우, 예외 세부 정보가 포함된 헤더를 추가할 수 있습니다.   
이에 대한 자세한 내용은 `FallbackHeaders` GatewayFilter Factory 섹션에서 확인할 수 있습니다.  

### Tripping The Circuit Breaker On Status Codes
오번역..GPT3.5 -> GPT 4.0 다시 번역해야됨.

일부 경우에는 랩핑된 라우트에서 반환된 상태 코드에 따라 회로 차단기를 트리핑하고 싶을 수 있습니다.   
회로 차단기 구성 객체는 회로 차단기를 트리핑할 상태 코드의 목록을 가져옵니다.   
회로 차단기를 트리핑하려는 상태 코드를 설정할 때는 상태 코드 값의 정수 또는 HttpStatus 열거형의 문자열 표현을 사용할 수 있습니다.  

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: circuitbreaker_route
        uri: lb://backing-service:8088
        predicates:
        - Path=/consumingServiceEndpoint
        filters:
        - name: CircuitBreaker
          args:
            name: myCircuitBreaker
            fallbackUri: forward:/inCaseOfFailureUseThis
            statusCodes:
              - 500
              - "NOT_FOUND"
```

Application.java
```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("circuitbreaker_route", r -> r.path("/consumingServiceEndpoint")
            .filters(f -> f.circuitBreaker(c -> c.name("myCircuitBreaker").fallbackUri("forward:/inCaseOfFailureUseThis").addStatusCode("INTERNAL_SERVER_ERROR"))
                .rewritePath("/consumingServiceEndpoint", "/backingServiceEndpoint")).uri("lb://backing-service:8088")
        .build();
}
```

---
여기부터 다시 번역 준비 1시 35분부터   
https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/cacherequestbody-factory.html

### CacheRequestBody GatewayFilter Factory
요청 본문을 읽어야 하는 상황이 발생할 수 있습니다. 요청은 한 번만 읽을 수 있기 때문에, 요청 본문을 캐시해야 합니다.   
CacheRequestBody 필터를 사용하여, 요청 본문을 다운스트림으로 보내기 전에 캐시하고 exchange 속성에서 본문을 가져올 수 있습니다.  

다음은 요청 본문을 캐시하는 GatewayFilter를 보여주는 예제입니다:
```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("cache_request_body_route", r -> r.path("/downstream/**")
            .filters(f -> f.prefixPath("/httpbin")
                .cacheRequestBody(String.class).uri(uri))
        .build();
}

```

application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: cache_request_body_route
        uri: lb://downstream
        predicates:
        - Path=/downstream/**
        filters:
        - name: CacheRequestBody
          args:
            bodyClass: java.lang.String
```

CacheRequestBody는 요청 본문을 추출하여, 위 예제에서 정의한 java.lang.String 같은 본문 클래스로 변환합니다.  
그런 다음 ServerWebExchange.getAttributes()에서 사용할 수 있는 속성에   
ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR 키로 배치합니다.

이 필터는 HTTP(HTTPS 포함) 요청에서만 작동합니다.

---

### DedupeResponseHeader GatewayFilter Factory

DedupeResponseHeader GatewayFilter Factory는 name 파라미터와 선택적인 strategy 파라미터를 받습니다.   
name은 공백으로 구분된 헤더 이름 목록을 포함할 수 있습니다.   
다음 예제는 DedupeResponseHeader GatewayFilter를 구성하는 방법을 보여줍니다:  

application.yml 설정 예제:  
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: dedupe_response_header_route
        uri: https://example.org
        filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin

```
이 설정은 게이트웨이 CORS 로직과 다운스트림 로직이 모두 Access-Control-Allow-Credentials  
및 Access-Control-Allow-Origin 응답 헤더를 추가하는 경우 중복 값을 제거합니다.  

DedupeResponseHeader 필터는 선택적인 strategy 파라미터도 받습니다.  
허용되는 값은 RETAIN_FIRST (기본값), RETAIN_LAST, 및 RETAIN_UNIQUE 입니다.  

예를 들어 RETAIN_LAST 전략을 사용하는 경우 설정 예시는 다음과 같습니다:

---
### FallbackHeaders GatewayFilter Factory
FallbackHeaders 팩토리는 외부 애플리케이션의   
fallbackUri로 전달된 요청의 헤더에 Spring Cloud CircuitBreaker      
실행 예외 세부 정보를 추가할 수 있도록 해줍니다. 다음과 같은 시나리오에서 사용할 수 있습니다:  

application.yml 설정 예제:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: ingredients
        uri: lb://ingredients
        predicates:
        - Path=//ingredients/**
        filters:
        - name: CircuitBreaker
          args:
            name: fetchIngredients
            fallbackUri: forward:/fallback
      - id: ingredients-fallback
        uri: http://localhost:9994
        predicates:
        - Path=/fallback
        filters:
        - name: FallbackHeaders
          args:
            executionExceptionTypeHeaderName: Test-Header
```
이 예제에서는 서킷 브레이커 실행 중 예외가 발생한 후,   
요청이 localhost:9994에서 실행 중인 애플리케이션의 폴백 엔드포인트 또는 핸들러로 전달됩니다.   
FallbackHeaders 필터는 예외 타입, 메시지,   
그리고 (가능한 경우) 루트 원인 예외 타입 및 메시지를 포함하는 헤더를 해당 요청에 추가합니다.  

헤더 이름은 다음 인수의 값을 설정하여 구성에서 덮어쓸 수 있습니다(기본값 표시):

executionExceptionTypeHeaderName ("Execution-Exception-Type")  
executionExceptionMessageHeaderName ("Execution-Exception-Message")  
rootCauseExceptionTypeHeaderName ("Root-Cause-Exception-Type")  
rootCauseExceptionMessageHeaderName ("Root-Cause-Exception-Message")  

서킷 브레이커와 게이트웨이에 대한 추가 정보는 Spring Cloud CircuitBreaker Factory 섹션에서 확인할 수 있습니다.

---
### JsonToGrpc GatewayFilter Factory
JSONToGRPC GatewayFilter Factory는 JSON 페이로드를 gRPC 요청으로 변환합니다.  

이 필터는 다음 인수를 사용합니다:  

protoDescriptor: 프로토 디스크립터 파일.  
protoFile: 프로토 정의 파일.  
service: 요청을 처리하는 서비스의 단축 이름.  
method: 요청을 처리하는 서비스의 메서드 이름.  
다음 명령을 사용하여 protoc 및 --descriptor_set_out 플래그를 지정하여 이 파일을 생성할 수 있습니다:  
```shell
protoc --proto_path=src/main/resources/proto/ \
--descriptor_set_out=src/main/resources/proto/hello.pb  \
src/main/resources/proto/hello.proto
```
application.yml 설정 예제:
```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
            .route("json-grpc", r -> r.path("/json/hello").filters(f -> {
                String protoDescriptor = "file:src/main/proto/hello.pb";
                String protoFile = "file:src/main/proto/hello.proto";
                String service = "HelloService";
                String method = "hello";
                return f.jsonToGRPC(protoDescriptor, protoFile, service, method);
            }).uri(uri))
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: json-grpc
          uri: https://localhost:6565/testhello
          predicates:
            - Path=/json/**
          filters:
            - name: JsonToGrpc
              args:
                protoDescriptor: file:proto/hello.pb
                protoFile: file:proto/hello.proto
                service: HelloService
                method: hello
```

요청이 /json/hello를 통해 게이트웨이에 전달되면, 이 요청은 hello.proto에   
정의된 내용을 사용하여 변환되어 HelloService/hello로 전송되고,  
응답은 다시 JSON으로 변환됩니다.  

기본적으로, 기본 TrustManagerFactory를 사용하여 NettyChannel을 생성합니다.   
그러나 GrpcSslConfigurer 타입의 빈을 생성하여 이 TrustManager를 사용자 정의할 수 있습니다:  

```java
@Configuration
public class GRPCLocalConfiguration {
    @Bean
    public GRPCSSLContext sslContext() {
        TrustManager trustManager = trustAllCerts();
        return new GRPCSSLContext(trustManager);
    }
}

```

---

### LocalResponseCache GatewayFilter Factory

이 필터는 응답 본문과 헤더를 캐싱하여 다음 규칙을 따릅니다:

본문이 없는 GET 요청만 캐시할 수 있습니다.
HTTP 200 (OK), HTTP 206 (Partial Content),   
HTTP 301 (Moved Permanently) 상태 코드 중 하나에 해당하는 응답만 캐시합니다.  
Cache-Control 헤더에 no-store가 요청에 포함되어 있거나,   
응답에 no-store 또는 private가 포함되어 있으면 응답 데이터는 캐시되지 않습니다.  
응답이 이미 캐시된 상태에서 Cache-Control 헤더에 no-cache 값이  
포함된 새로운 요청이 수행되면, 본문이 없는 304 (Not Modified) 응답을 반환합니다.  
이 필터는 경로별로 로컬 응답 캐시를 구성하며, spring.cloud.gateway.filter.local-response-cache.enabled 속성이  
활성화된 경우에만 사용할 수 있습니다. 전역적으로 구성된 로컬 응답 캐시도 기능으로 제공됩니다.

이 필터는 첫 번째 매개변수로 캐시 항목 만료 시간을 (초: s, 분: m, 시간: h 단위로) 재정의하며, 두 번째 매개변수로 이 경로의 캐시 최대 크기 (KB, MB 또는 GB 단위)를 설정하여 항목을 제거할 수 있습니다.

다음은 로컬 응답 캐시 GatewayFilter를 추가하는 방법을 보여줍니다:
```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("rewrite_response_upper", r -> r.host("*.rewriteresponseupper.org")
            .filters(f -> f.prefixPath("/httpbin")
        		.localResponseCache(Duration.ofMinutes(30), "500MB")
            ).uri(uri))
        .build();
}
```
or this

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: resource
        uri: http://localhost:9000
        predicates:
        - Path=/resource
        filters:
        - LocalResponseCache=30m,500MB
```
이 필터는 또한 HTTP Cache-Control 헤더의 max-age 값을 자동으로 계산합니다.   
원래 응답에 max-age가 있는 경우에만 이 값을 timeToLive 구성 매개변수에 설정된 초 단위 값으로 다시 씁니다.   
연속 호출에서는 응답이 만료될 때까지 남은 초 수로 이 값을 재계산합니다.  

이 기능을 활성화하려면 com.github.ben-manes.caffeine:caffeine 및  
spring-boot-starter-cache를 프로젝트 종속성으로 추가하십시오.  
프로젝트에서 커스텀 CacheManager 빈을 생성하는 경우,  
이 빈에 @Primary를 표시하거나 @Qualifier를 사용하여 주입해야 합니다.  

---
### MapRequestHeader GatewayFilter Factory
MapRequestHeader GatewayFilter factory는 fromHeader와 toHeader 매개변수를 사용합니다.  
이 필터는 새로 명명된 헤더(toHeader)를 생성하며, 값은 들어오는 HTTP 요청의 기존 명명된 헤더(fromHeader)에서 추출됩니다.   
입력 헤더가 존재하지 않으면 필터는 아무 영향도 주지 않습니다. 새로 명명된 헤더가 이미 존재하는 경우, 해당 헤더의 값은 새로운 값으로 보강됩니다.
다음 예제는 MapRequestHeader를 구성하는 방법을 보여줍니다:  

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: map_request_header_route
        uri: https://example.org
        filters:
        - MapRequestHeader=Blue, X-Request-Red

```
이 구성은 들어오는 HTTP 요청의 Blue 헤더에서 업데이트된 값을 사용하여,  
다운스트림 요청에 X-Request-Red:<values> 헤더를 추가합니다.

---
### ModifyRequestBody GatewayFilter Factory
ModifyRequestBody 필터를 사용하여 게이트웨이가 다운스트림으로   
요청을 보내기 전에 요청 본문을 수정할 수 있습니다.

이 필터는 Java DSL로만 구성할 수 있습니다. 다음 예제는 요청 본문을 수정하는 GatewayFilter를 설정하는 방법을 보여줍니다:

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("rewrite_request_obj", r -> r.host("*.rewriterequestobj.org")
            .filters(f -> f.prefixPath("/httpbin")
                .modifyRequestBody(String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE,
                    (exchange, s) -> Mono.just(new Hello(s.toUpperCase())))).uri(uri))
        .build();
}

static class Hello {
    String message;

    public Hello() { }

    public Hello(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

요청에 본문이 없는 경우, RewriteFilter는 null을 전달받습니다.   
누락된 본문을 요청에 할당하려면 Mono.empty()를 반환해야 합니다.



---
### ModifyResponseBody GatewayFilter Factory
ModifyResponseBody 필터를 사용하여 게이트웨이가  
클라이언트에게 응답을 보내기 전에 응답 본문을 수정할 수 있습니다.  

이 필터는 Java DSL로만 구성할 수 있습니다.   
다음 예제는 응답 본문을 수정하는 GatewayFilter를 설정하는 방법을 보여줍니다:  

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("rewrite_response_upper", r -> r.host("*.rewriteresponseupper.org")
            .filters(f -> f.prefixPath("/httpbin")
        		.modifyResponseBody(String.class, String.class,
        		    (exchange, s) -> Mono.just(s.toUpperCase()))).uri(uri))
        .build();
}
```
응답에 본문이 없는 경우, RewriteFilter는 null을 전달받습니다.   
누락된 본문을 응답에 할당하려면 Mono.empty()를 반환해야 합니다.

---

### PrefixPath GatewayFilter Factory
PrefixPath GatewayFilter factory는 단일 prefix 매개변수를 받습니다.
다음 예제는 PrefixPath GatewayFilter를 구성하는 방법을 보여줍니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: prefixpath_route
        uri: https://example.org
        filters:
        - PrefixPath=/mypath
```

이 구성은 모든 매칭 요청의 경로에 /mypath를 접두사로 추가합니다.
따라서 /hello로의 요청은 /mypath/hello로 전달됩니다.

---

### PreserveHostHeader GatewayFilter Factory
PreserveHostHeader GatewayFilter factory는 매개변수가 없습니다.   
이 필터는 라우팅 필터가 원래의 호스트 헤더를 보낼지, HTTP 클라이언트에 의해 결정된 호스트 헤더를 보낼지 결정하기 위해 요청 속성을 설정합니다.  
다음은 PreserveHostHeader GatewayFilter를 구성하는 예제입니다:  

application.yml

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: preserve_host_route
        uri: https://example.org
        filters:
        - PreserveHostHeader
```

---
### RedirectTo GatewayFilter Factory
RedirectTo GatewayFilter factory는 status, url, 그리고 선택적으로 includeRequestParams  
세 가지 매개변수를 받습니다. status 매개변수는 301과 같은 300 시리즈의 리다이렉트 HTTP 코드여야 합니다.   
url 매개변수는 유효한 URL이어야 하며, 이는 Location 헤더의 값이 됩니다.   
includeRequestParams 매개변수는 요청 쿼리 매개변수를 URL에 포함할지를 나타내며,   
설정되지 않으면 기본적으로 false로 처리됩니다. 상대적 리다이렉트를 위해 경로 정의의 uri로 no://op를 사용해야 합니다.  
다음은 RedirectTo GatewayFilter를 구성하는 예제입니다:  

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: prefixpath_route
        uri: https://example.org
        filters:
        - RedirectTo=302, https://acme.org
```
이 구성은 상태 코드 302와 Location: https://acme.org 헤더를 사용하여 리다이렉트를 수행합니다.

다음 예제는 includeRequestParams를 true로 설정한 RedirectTo GatewayFilter를 구성하는 방법을 보여줍니다:
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: prefixpath_route
        uri: https://example.org
        filters:
        - RedirectTo=302, https://acme.org, true
```
게이트웨이에 ?skip=10 쿼리와 함께 요청이 들어오면, 게이트웨이는   
상태 코드 302와 Location: https://acme.org?skip=10 헤더를 사용하여 리다이렉트를 수행합니다.  
---

### RemoveJsonAttributesResponseBody GatewayFilter Factory

RemoveJsonAttributesResponseBody GatewayFilter factory는 검색할 속성 이름의 컬렉션과,  
선택적으로 마지막 매개변수로 루트 수준에서 속성을 제거할지(false, 기본값)   
또는 재귀적으로 제거할지(true)를 지정하는 부울 값을 받습니다.   
이 필터는 JSON 본문 내용에서 속성을 삭제하는 변환을 쉽게 적용할 수 있습니다.  

다음은 RemoveJsonAttributesResponseBody GatewayFilter를 구성하는 예제입니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: removejsonattributes_route
        uri: https://example.org
        filters:
        - RemoveJsonAttributesResponseBody=id,color
```
이 구성은 루트 수준에서 JSON 콘텐츠 본문에서 "id"와 "color" 속성을 제거합니다.

다음 예제는 선택적 마지막 매개변수를 사용하는 
RemoveJsonAttributesResponseBody GatewayFilter를 구성하는 방법을 보여줍니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: removejsonattributes_recursively_route
        uri: https://example.org
        predicates:
        - Path=/red/{segment}
        filters:
        - RemoveJsonAttributesResponseBody=id,color,true
```
이 구성은 JSON 콘텐츠 본문에서 모든 수준에서 "id"와 "color" 속성을 제거합니다.

---
### RemoveRequestHeader GatewayFilter Factory
RemoveRequestHeader GatewayFilter factory는 제거할 헤더의 이름을 나타내는 name 매개변수를 받습니다.   
다음은 RemoveRequestHeader GatewayFilter를 구성하는 방법을 보여줍니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: removerequestheader_route
        uri: https://example.org
        filters:
        - RemoveRequestHeader=X-Request-Foo
```

이 구성은 다운스트림으로 보내기 전에 X-Request-Foo 헤더를 제거합니다:

---
### RemoveRequestParameter GatewayFilter Factory

RemoveRequestParameter GatewayFilter factory는 제거할 쿼리 매개변수의 이름을 나타내는 name 매개변수를 받습니다.  
다음은 RemoveRequestParameter GatewayFilter를 구성하는 예제입니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: removerequestparameter_route
        uri: https://example.org
        filters:
        - RemoveRequestParameter=red
```
이 설정은 red 매개변수를 다운스트림으로 보내기 전에 제거합니다.

---

### RemoveResponseHeader GatewayFilter Factory

RemoveResponseHeader GatewayFilter factory는 제거할 헤더의 이름을 나타내는 name 매개변수를 받습니다.  
다음은 RemoveResponseHeader GatewayFilter를 구성하는 방법을 보여줍니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: removeresponseheader_route
        uri: https://example.org
        filters:
        - RemoveResponseHeader=X-Response-Foo
```
이 설정은 응답에서 X-Response-Foo 헤더를 게이트웨이 클라이언트로 반환하기 전에 제거합니다.  

민감한 헤더를 제거하려면, 이를 수행할 경로마다 이 필터를 구성해야 합니다.  
추가로, spring.cloud.gateway.default-filters를 사용하여 한 번 구성하면 모든 경로에 적용할 수 있습니다.  


---

### RequestHeaderSize GatewayFilter Factory
RequestHeaderSize GatewayFilter factory는 maxSize와 errorHeaderName 매개변수를 받습니다.  
maxSize 매개변수는 요청 헤더에 허용되는 최대 데이터 크기(키와 값을 포함)이며,  
errorHeaderName 매개변수는 기본값으로 "errorMessage"인 오류 메시지를 포함하는 응답 헤더의 이름을 설정합니다.   
다음은 RequestHeaderSize GatewayFilter를 구성하는 예제입니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: requestheadersize_route
        uri: https://example.org
        filters:
        - RequestHeaderSize=1000B
```

이 설정은 요청 헤더의 크기가 1000 바이트를 초과할 경우 상태 코드 431과   
함께 myErrorHeader라는 이름의 응답 헤더에 오류 메시지를 포함합니다.

---
### RequestRateLimiter GatewayFilter Factory

RequestRateLimiter GatewayFilter factory는 RateLimiter 구현을 사용하여 현재 요청이 진행될 수 있는지 결정합니다.   
허용되지 않으면 기본적으로 HTTP 429 - Too Many Requests 상태가 반환됩니다.  

이 필터는 선택적 keyResolver 매개변수와 rate limiter에 특정한 매개변수를 받습니다.   
keyResolver는 KeyResolver 인터페이스를 구현하는 빈이며, SpEL을 사용하여 이름으로 참조합니다.  
#{@myKeyResolver}는 myKeyResolver라는 이름의 빈을 참조하는 SpEL 표현식입니다.  

다음은 KeyResolver 인터페이스입니다:
KeyResolver.java
```yaml

public interface KeyResolver {
	Mono<String> resolve(ServerWebExchange exchange);
}

```
KeyResolver 인터페이스는 요청 제한을 위한 키를 도출하는 플러그 가능한 전략을 제공합니다.  
기본 구현은 PrincipalNameKeyResolver로, ServerWebExchange에서 Principal을 가져와 Principal.getName()을 호출합니다.  

기본적으로 KeyResolver가 키를 찾지 못하면 요청이 거부됩니다.   
이 동작은 spring.cloud.gateway.filter.request-rate-limiter.deny-empty-key(true 또는 false)   
및 spring.cloud.gateway.filter.request-rate-limiter.empty-key-status-code 속성을 설정하여 조정할 수 있습니다.  

RequestRateLimiter는 "shortcut" 표기법으로 구성할 수 없습니다. 다음 예제는 유효하지 않습니다:  

```properties
# INVALID SHORTCUT CONFIGURATION
spring.cloud.gateway.routes[0].filters[0]=RequestRateLimiter=2, 2, #{@userkeyresolver}

```

```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: limit
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            key-resolver: "#{@userkeyresolver}"

```
Redis RateLimiter
Redis 구현은 Stripe의 작업을 기반으로 하며, spring-boot-starter-data-redis-reactive Spring Boot starter를 사용해야 합니다.

사용된 알고리즘은 토큰 버킷 알고리즘입니다.

redis-rate-limiter.replenishRate: 초당 허용되는 요청 수를 정의합니다. 이는 토큰 버킷이 채워지는 속도입니다.
redis-rate-limiter.burstCapacity: 사용자가 1초 동안 허용되는 최대 요청 수입니다. 이는 토큰 버킷이 보유할 수 있는 토큰 수입니다. 이 값을 0으로 설정하면 모든 요청이 차단됩니다.
redis-rate-limiter.requestedTokens: 요청당 소모되는 토큰 수를 정의합니다. 기본값은 1입니다.
안정된 속도는 replenishRate와 burstCapacity에 동일한 값을 설정하여 달성됩니다. 일시적인 버스트는 burstCapacity를 replenishRate보다 높게 설정하여 허용할 수 있습니다. 이 경우, 연속 버스트는 드롭된 요청(HTTP 429 - Too Many Requests)을 초래하므로 버스트 간에 일부 시간이 필요합니다.

다음은 redis-rate-limiter를 구성하는 예제입니다:

```yaml
.application.yml

spring:
  cloud:
    gateway:
      routes:
      - id: requestratelimiter_route
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 10
            redis-rate-limiter.burstCapacity: 20
            redis-rate-limiter.requestedTokens: 1

```
Java에서 KeyResolver 구성 예제:

```yaml
Config.java
@Bean
KeyResolver userKeyResolver() {
    return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
}

```
이 정의는 사용자당 10개의 요청 제한을 설정합니다. 20개의 버스트가 허용되지만, 다음 초에는 10개의 요청만 가능합니다.  
KeyResolver는 사용자 요청 매개변수를 가져오는 간단한 구현입니다.   
참고: 이는 프로덕션에서는 권장되지 않습니다.  

또한, RateLimiter 인터페이스를 구현하는 빈으로 rate limiter를 정의할 수 있습니다.   
구성에서, SpEL을 사용하여 이름으로 빈을 참조할 수 있습니다. #{@myRateLimiter}는 myRateLimiter라는   
이름의 빈을 참조하는 SpEL 표현식입니다. 다음은 이전에 정의된 KeyResolver를 사용하는 rate limiter를 정의하는 예제입니다:  

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: requestratelimiter_route
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            rate-limiter: "#{@myRateLimiter}"
            key-resolver: "#{@userKeyResolver}"
```

---

### RewriteLocationResponseHeader GatewayFilter Factory
RewriteLocationResponseHeader GatewayFilter factory는 Location   
응답 헤더의 값을 수정하여 백엔드 관련 세부 정보를 제거합니다.   
이 필터는 Mode, locationHeaderName, hostValue, 및 protocols 매개변수를 받습니다.   
다음은 RewriteLocationResponseHeader GatewayFilter를 구성하는 예제입니다:  

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: rewritelocationresponseheader_route
        uri: http://example.org
        filters:
        - RewriteLocationResponseHeader=AS_IN_REQUEST, Location, ,
```
예를 들어, POST api.example.com/some/object/name 요청의 경우,  
Location 응답 헤더 값 object-service.prod.example.net/v2/some/object/id가   
api.example.com/some/object/id로 다시 작성됩니다.  

Mode 매개변수 값 설명:
NEVER_STRIP: 원래 요청 경로에 버전이 포함되어 있지 않더라도 버전이 제거되지 않습니다.  
AS_IN_REQUEST (기본값): 원래 요청 경로에 버전이 포함되어 있지 않을 때만 버전이 제거됩니다.  
ALWAYS_STRIP: 원래 요청 경로에 버전이 포함되어 있더라도 버전이 항상 제거됩니다.  

추가 매개변수 설명:
hostValue 매개변수: 제공된 경우, 응답 Location 헤더의 host:port 부분을 대체하는 데 사용됩니다.   
제공되지 않으면 요청의 Host 헤더 값이 사용됩니다.  
protocols 매개변수: 프로토콜 이름과 일치하는 유효한 정규 표현식 문자열이어야 합니다.   
일치하지 않으면 필터는 아무 작업도 수행하지 않습니다. 기본값은 https?|ftps?입니다.  


---

### RewritePath GatewayFilter Factory
RewritePath GatewayFilter factory는 path regexp 매개변수와 replacement 매개변수를 받습니다. 
이는 Java 정규 표현식을 사용하여 요청 경로를 유연하게 다시 작성할 수 있습니다. 
다음은 RewritePath GatewayFilter를 구성하는 예제입니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: rewritepath_route
        uri: https://example.org
        predicates:
        - Path=/red/**
        filters:
        - RewritePath=/red/?(?<segment>.*), /$\{segment}
```
이 구성은 /red/blue 요청 경로를 다운스트림 요청 전에 /blue로 설정합니다.
YAML 명세로 인해 $를 $\로 교체해야 한다는 점을 주의해야 합니다.

---
### RewriteRequestParameter GatewayFilter Factory

RewriteRequestParameter GatewayFilter factory는 name 매개변수와 replacement 매개변수를 받습니다.   
이는 주어진 이름의 요청 매개변수 값을 다시 작성합니다.   
동일한 이름의 여러 요청 매개변수가 설정된 경우, 단일 값으로 대체됩니다.  
요청 매개변수가 없으면 변경이 이루어지지 않습니다. 다음은 RewriteRequestParameter GatewayFilter를 구성하는 예제입니다:  

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: rewriterequestparameter_route
        uri: https://example.org
        predicates:
        - Path=/products
        filters:
        - RewriteRequestParameter=campaign,fall2023
```
이 구성은 /products?campaign=old 요청에 대해 campaign=fall2023으로 요청 매개변수를 설정합니다.

---
### RewriteResponseHeader GatewayFilter Factory
RewriteResponseHeader GatewayFilter factory는 name, regexp, 및 replacement 매개변수를 받습니다. 
이는 Java 정규 표현식을 사용하여 응답 헤더 값을 유연하게 다시 작성합니다.
다음은 RewriteResponseHeader GatewayFilter를 구성하는 예제입니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: rewriteresponseheader_route
        uri: https://example.org
        filters:
        - RewriteResponseHeader=X-Response-Red, , password=[^&]+, password=***
```

구성 요소 설명:  
name: X-Response-Red  
변경할 응답 헤더의 이름입니다.

regexp: password=[^&]+  
password 매개변수와 그 값을 캡처하는 정규 표현식입니다.

replacement: password=***  
password 값을 ***로 대체합니다.

예시 설명:  
입력 헤더 값: /42?user=ford&password=omg!what&flag=true  
변경 후 헤더 값: /42?user=ford&password=***&flag=true  

---
### SaveSession GatewayFilter Factory
SaveSession GatewayFilter factory는 다운스트림으로 호출을 전달하기 전에 WebSession::save 작업을 강제로 수행합니다.
이는 Spring Session과 같은 지연 데이터 저장소를 사용하는 경우에 특히 유용하며, 전달된 호출 전에 세션 상태가 저장되었는지 확인해야 할 때 사용됩니다.
다음은 SaveSession GatewayFilter를 구성하는 예제입니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: save_session
        uri: https://example.org
        predicates:
        - Path=/foo/**
        filters:
        - SaveSession
```
이 구성은 /foo/** 경로에 대해 SaveSession 필터를 추가하여 호출을 전달하기 전에 세션 상태를 저장합니다.  

구성 요소 설명:  
경로: /foo/**  
이 경로에 매칭되는 요청에 대해 필터가 적용됩니다.  

필터: SaveSession   
호출을 전달하기 전에 세션을 저장합니다.  

예시 설명:  
목적: Spring Security와 Spring Session을 통합하여 보안 세부 정보를 원격 프로세스로 전달하려면 필수입니다.  


---

### SecureHeaders GatewayFilter Factory
SecureHeaders GatewayFilter factory는 블로그 게시물에서 권장하는 대로 응답에 여러 헤더를 추가합니다.   
기본값으로 추가되는 헤더는 다음과 같습니다:

X-Xss-Protection: 1; mode=block  
Strict-Transport-Security: max-age=631138519  
X-Frame-Options: DENY  
X-Content-Type-Options: nosniff  
Referrer-Policy: no-referrer  
Content-Security-Policy: default-src 'self' https:; font-src 'self' https: data:; img-src 'self' https: data:; object-src 'none'; script-src https:; style-src 'self' https: 'unsafe-inline'  
X-Download-Options: noopen  
X-Permitted-Cross-Domain-Policies: none  

기본값을 변경하려면 spring.cloud.gateway.filter.secure-headers   
네임스페이스에 적절한 속성을 설정합니다. 사용 가능한 속성은 다음과 같습니다:  

xss-protection-header  
strict-transport-security  
frame-options  
content-type-options  
referrer-policy  
content-security-policy  
download-options  
permitted-cross-domain-policies  

기본값을 비활성화하려면 spring.cloud.gateway.filter.secure-headers.disable  
속성에 쉼표로 구분된 값을 설정합니다. 예시는 다음과 같습니다:  

```yaml
spring.cloud.gateway.filter.secure-headers.disable=x-frame-options,strict-transport-security
```

아래는 SecureHeaders GatewayFilter를 구성하는 예제입니다:

---
### SetPath GatewayFilter Factory
SetPath GatewayFilter factory는 경로 템플릿 매개변수를 받습니다. 
이는 요청 경로를 템플릿 세그먼트를 사용하여 간단하게 조작할 수 있는 방법을 제공합니다. 
Spring Framework의 URI 템플릿을 사용하며, 여러 일치 세그먼트가 허용됩니다. 다음은 SetPath GatewayFilter를 구성하는 예제입니다:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: setpath_route
        uri: https://example.org
        predicates:
        - Path=/red/{segment}
        filters:
        - SetPath=/{segment}
```

이 구성은 /red/blue 요청 경로를 다운스트림 요청 전에 /blue로 설정합니다.

---
### SetRequestHeader GatewayFilter Factory
SetRequestHeader GatewayFilter factory는 name과 value 매개변수를 받습니다.   
이 필터는 주어진 이름의 모든 헤더를 교체하며, URI 변수를 인식하고 실행 시 확장합니다.   
다음은 SetRequestHeader GatewayFilter를 구성하는 예제입니다:  

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: setrequestheader_route
        uri: https://example.org
        filters:
        - SetRequestHeader=X-Request-Red, Blue
```
이 구성은 요청 헤더 X-Request-Red를 Blue로 설정합니다. 따라서 다운스트림 서비스는 X-Request-Red: Blue 헤더를 받습니다.

이 구성은 호스트가 {segment}.myhost.org인 경우, 요청 헤더 foo를 bar-{segment}로 설정합니다.
예를 들어, segment가 example이면, 헤더는 foo: bar-example로 설정됩니다.

구성 요소 설명:
name: X-Request-Red
설정할 헤더의 이름입니다.
value: Blue 또는 bar-{segment}
설정할 헤더의 값입니다. URI 변수는 {}로 묶어 사용하며, 실행 시 확장됩니다.
예시 설명:
기본 구성:

입력 헤더: X-Request-Red: 1234
변경 후 헤더: X-Request-Red: Blue
URI 변수 사용:

입력 호스트: example.myhost.org
변경 후 헤더: foo: bar-example

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: setrequestheader_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - SetRequestHeader=foo, bar-{segment}
```

---

### SetResponseHeader GatewayFilter Factory

SetResponseHeader GatewayFilter factory는 name과 value 매개변수를 받습니다. 
이 필터는 주어진 이름의 모든 헤더를 교체하며, URI 변수를 인식하고 실행 시 확장합니다.
다음은 SetResponseHeader GatewayFilter를 구성하는 예제입니다:

기본 SetResponseHeader 구성:
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: setresponseheader_route
        uri: https://example.org
        filters:
        - SetResponseHeader=X-Response-Red, Blue
```

이 구성은 호스트가 {segment}.myhost.org인 경우, 응답 헤더 foo를 bar-{segment}로 설정합니다. 
예를 들어, segment가 example이면, 헤더는 foo: bar-example로 설정됩니다.

구성 요소 설명:
name: X-Response-Red
설정할 헤더의 이름입니다.
value: Blue 또는 bar-{segment}
설정할 헤더의 값입니다. URI 변수는 {}로 묶어 사용하며, 실행 시 확장됩니다.
예시 설명:
기본 구성:

입력 헤더: X-Response-Red: 1234
변경 후 헤더: X-Response-Red: Blue
URI 변수 사용:

입력 호스트: example.myhost.org
변경 후 헤더: foo: bar-example

URI 변수를 사용하는 SetResponseHeader 구성:
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: setresponseheader_route
        uri: https://example.org
        predicates:
        - Host: {segment}.myhost.org
        filters:
        - SetResponseHeader=foo, bar-{segment}
```

---
### SetStatus GatewayFilter Factory

SetStatus GatewayFilter factory는 단일 매개변수 status를 받습니다.   
이는 유효한 Spring HttpStatus여야 하며, 정수 값 404 또는 열거형의 문자열 표현인 NOT_FOUND일 수 있습니다.  
다음은 SetStatus GatewayFilter를 구성하는 예제입니다:  

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: setstatusstring_route
        uri: https://example.org
        filters:
        - SetStatus=UNAUTHORIZED
      - id: setstatusint_route
        uri: https://example.org
        filters:
        - SetStatus=401
```

위의 두 예시는 응답의 HTTP 상태를 401로 설정합니다.

구성 요소 설명:
status: UNAUTHORIZED 또는 401
응답 상태를 나타내는 값입니다. 문자열로 HttpStatus 열거형 또는 정수 값으로 지정할 수 있습니다.

예시 설명:
문자열 표현: UNAUTHORIZED는 HTTP 상태 401에 해당합니다.
정수 표현: 401은 HTTP 상태 UNAUTHORIZED의 정수 표현입니다.
추가 구성: 원래 HTTP 상태 코드 반환
원래 프록시 요청의 HTTP 상태 코드를 응답 헤더에 포함하도록 SetStatus GatewayFilter를 구성할 수 있습니다. 다음 속성을 사용하여 구성합니다:

application.yml
```yaml

spring:
  cloud:
    gateway:
      set-status:
        original-status-header-name: original-http-status

```

---
### StripPrefix GatewayFilter Factory
StripPrefix GatewayFilter factory는 parts라는 매개변수를 받습니다.   
이 매개변수는 다운스트림으로 요청을 보내기 전에 경로에서 제거할 부분의 수를 나타냅니다.  
다음은 StripPrefix GatewayFilter를 구성하는 예제입니다:  

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: nameRoot
        uri: https://nameservice
        predicates:
        - Path=/name/**
        filters:
        - StripPrefix=2
```

이 구성은 /name/blue/red로 게이트웨이를 통해 요청이 들어오면, 
nameservice로의 요청은 nameservice/red처럼 보이게 됩니다.

구성 요소 설명:  
parts: 2
제거할 경로 부분의 수를 나타냅니다.

예시 설명:  
입력 경로: /name/blue/red
변경 후 경로: nameservice/red

---
### Retry GatewayFilter Factory
Retry GatewayFilter factory는 다음과 같은 매개변수를 지원합니다:

retries: 재시도할 횟수.
statuses: 재시도할 HTTP 상태 코드(org.springframework.http.HttpStatus 사용).
methods: 재시도할 HTTP 메소드(org.springframework.http.HttpMethod 사용).
series: 재시도할 상태 코드의 시리즈(org.springframework.http.HttpStatus.Series 사용).
exceptions: 재시도할 예외 목록.
backoff: 재시도에 대한 지수 백오프 설정. 재시도는 firstBackoff * (factor ^ n) 간격 후에 수행되며,  
여기서 n은 반복 횟수입니다. maxBackoff가 설정된 경우 최대 백오프는 maxBackoff로 제한됩니다. 
basedOnPreviousValue가 true이면, 백오프는 prevBackoff * factor로 계산됩니다.  


기본 설정:
retries: 3회
series: 5XX 시리즈
methods: GET 메소드
exceptions: IOException 및 TimeoutException
backoff: 비활성화

구성 예제
기본 구성 예제:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: retry_test
        uri: http://localhost:8080/flakey
        predicates:
        - Host=*.retry.com
        filters:
        - name: Retry
          args:
            retries: 3
            statuses: BAD_GATEWAY
            methods: GET,POST
            backoff:
              firstBackoff: 10ms
              maxBackoff: 50ms
              factor: 2
              basedOnPreviousValue: false
```
이 구성은 BAD_GATEWAY 상태 코드에 대해 GET 및 POST 메소드의 요청을 최대 3회 재시도하며,
초기 백오프는 10ms, 최대 백오프는 50ms, 백오프 인수는 2입니다.

단순화된 "shortcut" 구성:
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: retry_route
        uri: https://example.org
        filters:
        - name: Retry
          args:
            retries: 3
            statuses: INTERNAL_SERVER_ERROR
            methods: GET
            backoff:
              firstBackoff: 10ms
              maxBackoff: 50ms
              factor: 2
              basedOnPreviousValue: false

      - id: retryshortcut_route
        uri: https://example.org
        filters:
        - Retry=3,INTERNAL_SERVER_ERROR,GET,10ms,50ms,2,false
```

두 구성은 동일한 동작을 정의합니다. INTERNAL_SERVER_ERROR 상태 코드에 대해 GET 메소드의 요청을 최대 3회 재시도합니다.

주의사항:
**forward:**로 접두사가 붙은 URL과 함께 재시도 필터를 사용할 때는 타겟 엔드포인트가 오류가 발생할 경우   
클라이언트에게 응답을 보내지 않도록 신중하게 작성해야 합니다.  
예를 들어, 타겟 컨트롤러 메소드가 오류 상태 코드로 ResponseEntity를 반환하지 않도록 하고,   
대신 예외를 던지거나 Mono.error(ex)를 통해 오류를 신호로 보내야 합니다.  

본문이 있는 HTTP 메소드와 함께 재시도 필터를 사용할 때는 본문이 캐시되므로 게이트웨이의 메모리 제약이 발생할 수 있습니다.   
본문은 ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR에 정의된 요청 속성에 캐시되며,   
객체의 타입은 org.springframework.core.io.buffer.DataBuffer입니다.  

구성 요약:  
기본 Retry 설정:  
재시도 횟수: 3회  
재시도할 상태 코드: INTERNAL_SERVER_ERROR  
재시도할 메소드: GET  
초기 백오프: 10ms  
최대 백오프: 50ms  
백오프 인수: 2  
이전 값 기반 여부: false  
이 구성은 안정적인 서비스 운영을 위해 재시도 로직을 설정할 때 유용합니다.  

---

### RequestSize GatewayFilter Factory
maxSize는 DataSize 유형이므로 'KB' 또는 'MB'와 같은 선택적 DataUnit 접미사와 함께 숫자로 정의할 수 있습니다. 
기본값은 바이트를 나타내는 'B'입니다.
이 필터는 요청의 허용 크기 한도를 바이트 단위로 정의합니다.

다음은 RequestSize GatewayFilter를 구성하는 예제입니다:

예시: 기본 구성
```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: request_size_route
        uri: http://localhost:8080/upload
        predicates:
        - Path=/upload
        filters:
        - name: RequestSize
          args:
            maxSize: 5000000

```

이 구성은 /upload 경로로의 요청이 5,000,000 바이트(약 5MB)를 초과할 경우 요청을 제한하고, 상태 코드 413 Payload Too Large와 추가 헤더 errorMessage를 설정합니다.

구성 요소 설명:
maxSize: 5000000
허용되는 요청 크기 한도를 정의합니다. 여기서는 5,000,000 바이트입니다.
예시 설명:
입력 경로: /upload
변경 후 상태 코드: 413 Payload Too Large
추가 헤더: errorMessage
예: Request size is larger than permissible limit. Request size is 6.0 MB where permissible limit is 5.0 MB

확장 예제:
기본 RequestSize 구성:

기본 허용 크기 한도를 5MB로 설정:

```yaml

spring:
  cloud:
    gateway:
      routes:
      - id: request_size_route
        uri: http://localhost:8080/upload
        predicates:
        - Path=/upload
        filters:
        - name: RequestSize
          args:
            maxSize: 5000000

```
KB 단위 허용 크기 설정:

허용 크기를 10KB로 설정하는 예제:
```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: request_size_kb_route
        uri: http://localhost:8080/upload
        predicates:
        - Path=/upload
        filters:
        - name: RequestSize
          args:
            maxSize: 10KB

```

MB 단위 허용 크기 설정:

허용 크기를 2MB로 설정하는 예제:
```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: request_size_mb_route
        uri: http://localhost:8080/upload
        predicates:
        - Path=/upload
        filters:
        - name: RequestSize
          args:
            maxSize: 2MB

```

이 구성을 통해 RequestSize 필터는 요청 크기가 허용된 한도를 초과할 경우 상태 코드 413을 설정하고,   
요청 크기 초과에 대한 명확한 오류 메시지를 포함하여 응답을 제공합니다.   
이를 통해 요청 크기에 대한 제어를 효율적으로 관리할 수 있습니다.

---

### SetRequestHostHeader GatewayFilter Factory

SetRequestHostHeader GatewayFilter factory는 host 매개변수를 사용하여 
기존의 호스트 헤더를 지정된 값으로 교체할 수 있습니다. 
다음은 SetRequestHostHeader GatewayFilter를 구성하는 예제입니다:
```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: set_request_host_header_route
        uri: http://localhost:8080/headers
        predicates:
        - Path=/headers
        filters:
        - name: SetRequestHostHeader
          args:
            host: example.org

```

이 구성은 요청의 호스트 헤더 값을 example.org로 교체합니다.

구성 요소 설명:
host: example.org
새로 설정할 호스트 헤더의 값입니다.
예시 설명:
입력 경로: /headers
변경 후 호스트 헤더: example.org

예제 코드 요약:
기본 SetRequestHostHeader 구성:
기존 호스트 헤더를 example.org로 교체:
```yaml
application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: set_request_host_header_route
        uri: http://localhost:8080/headers
        predicates:
        - Path=/headers
        filters:
        - name: SetRequestHostHeader
          args:
            host: example.org

```
확장 예제:
다른 호스트로 설정:

기존 호스트 헤더를 myhost.com으로 교체

```yaml

spring:
  cloud:
    gateway:
      routes:
      - id: set_request_host_myhost_route
        uri: http://localhost:8080/headers
        predicates:
        - Path=/headers
        filters:
        - name: SetRequestHostHeader
          args:
            host: myhost.com

```

요약:
SetRequestHostHeader 필터는 요청의 호스트 헤더를 지정된 값으로 설정하여,   
특정 상황에서 필요한 호스트 헤더를 효율적으로 관리할 수 있습니다.   
이를 통해 호스트 기반 라우팅을 쉽게 구성할 수 있습니다.  

---
### TokenRelay GatewayFilter Factory
TokenRelay GatewayFilter는 Spring Cloud Gateway에서 OAuth2 액세스 토큰을 다운스트림 서비스로 전달할 수 있게 해줍니다.
이 필터는 클라이언트로부터 전달된 토큰을 사용하여 리소스 요청을 전달합니다. 
이때 clientRegistrationId 매개변수를 선택적으로 받을 수 있습니다.

예제 구성
Java 설정 (without clientRegistrationId):
```java
App.java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
            .route("resource", r -> r.path("/resource")
                    .filters(f -> f.tokenRelay("myregistrationid"))
                    .uri("http://localhost:9000"))
            .build();
}

```
YAML 설정 with clientRegistrationId:

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: resource
        uri: http://localhost:9000
        predicates:
        - Path=/resource
        filters:
        - TokenRelay=myregistrationid

```
위 예제는 clientRegistrationId를 지정하여 해당 클라이언트 등록 정보를 사용해 OAuth2 액세스 토큰을 가져오고 전달합니다.


Java 설정 without clientRegistrationId:
```java
App.java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
            .route("resource", r -> r.path("/resource")
                    .filters(f -> f.tokenRelay())
                    .uri("http://localhost:9000"))
            .build();
}

```
YAML 설정 without clientRegistrationId:
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: resource
        uri: http://localhost:9000
        predicates:
        - Path=/resource
        filters:
        - TokenRelay=

```
이 예제에서는 clientRegistrationId를 생략하여 현재 인증된 사용자의 액세스 토큰을 사용하여 요청을 전달합니다.

필수 종속성
Spring Cloud Gateway에서 OAuth2 액세스 토큰을 전달하려면 다음 종속성을 추가해야 합니다:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

```
동작 원리
필터는 현재 인증된 사용자로부터 OAuth2 액세스 토큰을 추출합니다.  
clientRegistrationId가 제공된 경우, 해당 클라이언트 등록 정보를 사용하여 토큰을 가져옵니다.  
clientRegistrationId가 제공되지 않은 경우, 로그인 중에 얻은 현재 사용자의 자체 액세스 토큰을 사용합니다.  
추출된 액세스 토큰은 다운스트림 요청의 요청 헤더에 추가됩니다.  

필수 설정  
TokenRelayGatewayFilterFactory 빈은 올바른 spring.security.oauth2.client.* 속성이 설정된 경우에만 생성됩니다.  
이는 ReactiveClientRegistrationRepository 빈의 생성을 트리거합니다.  

기본 ReactiveOAuth2AuthorizedClientService 구현은 인메모리 데이터 저장소를 사용합니다.   
보다 견고한 솔루션이 필요한 경우, 사용자 정의 ReactiveOAuth2AuthorizedClientService 구현을 제공해야 합니다.  

---
### Default Filters
spring.cloud.gateway.default-filters 속성을 사용하면 모든 경로에 적용할 기본 필터를 정의할 수 있습니다.   
이는 각 경로에 필터를 반복적으로 추가할 필요 없이 일괄적으로 필터를 적용할 수 있는 편리한 방법입니다.   
다음은 default-filters를 사용하여 기본 필터를 정의하는 예제입니다:  

```yaml
application.yml
spring:
  cloud:
    gateway:
      default-filters:
      - AddResponseHeader=X-Response-Default-Red, Default-Blue
      - PrefixPath=/httpbin
      routes:
      - id: example_route
        uri: https://example.org
        predicates:
        - Path=/example

```
구성 요소 설명:
AddResponseHeader: X-Response-Default-Red, Default-Blue
모든 응답에 X-Response-Default-Red 헤더를 추가하고 그 값을 Default-Blue로 설정합니다.

PrefixPath: /httpbin
모든 요청 경로에 /httpbin을 접두사로 추가합니다.

예시 설명:
AddResponseHeader:
모든 응답 헤더에 X-Response-Default-Red: Default-Blue를 추가합니다.

PrefixPath:
모든 요청 경로에 /httpbin을 접두사로 추가합니다.
예를 들어, /example 요청 경로는 /httpbin/example로 변경됩니다.

확장 예제:
추가 기본 필터 설정:

RequestHeaderSize 필터 추가:
```yaml
application.yml
spring:
  cloud:
    gateway:
      default-filters:
      - AddResponseHeader=X-Response-Default-Red, Default-Blue
      - PrefixPath=/httpbin
      - RequestHeaderSize=10KB
      routes:
      - id: example_route
        uri: https://example.org
        predicates:
        - Path=/example

```

기능 설명:
모든 요청의 헤더 크기를 10KB로 제한합니다.
입력 경로: /example
변경 후 경로: /httpbin/example
추가된 헤더: X-Response-Default-Red: Default-Blue

Retry 필터 추가:
Retry 필터 적용:
```yaml
spring:
  cloud:
    gateway:
      default-filters:
      - AddResponseHeader=X-Response-Default-Red, Default-Blue
      - PrefixPath=/httpbin
      - Retry=3,INTERNAL_SERVER_ERROR,GET,10ms,50ms,2,false
      routes:
      - id: retry_route
        uri: https://retry.example.org
        predicates:
        - Path=/retry/**

```
기능 설명:  
HTTP 상태 코드 INTERNAL_SERVER_ERROR에 대해 GET 요청을 3회 재시도합니다.  
입력 경로: /retry/**  
변경 후 경로: /httpbin/retry/**  
추가된 헤더: X-Response-Default-Red: Default-Blue  

이 구성을 통해, 기본 필터는 모든 경로에 적용되며, 각 경로에 대해 반복적으로 필터를 추가할 필요 없이 일관된 필터링을 제공합니다.  
이를 통해 코드의 간결함과 유지 보수성을 높일 수 있습니다.  

---
# Global Filters
GlobalFilter와 GatewayFilter의 통합 필터 체인 순서
GlobalFilter 인터페이스는 GatewayFilter와 동일한 시그니처를 가집니다. 이 필터들은 모든 라우트에 조건부로 적용되는 특별한 필터입니다.

이 인터페이스와 그 사용법은 향후 마일스톤 릴리스에서 변경될 수 있습니다.

통합 GlobalFilter 및 GatewayFilter 순서
요청이 라우트와 일치하면 필터링 웹 핸들러는 모든 GlobalFilter 인스턴스와 모든 라우트 특정 GatewayFilter 인스턴스를 필터 체인에 추가합니다. 이 통합 필터 체인은 org.springframework.core.Ordered 인터페이스에 따라 정렬되며, getOrder() 메서드를 구현하여 설정할 수 있습니다.

Spring Cloud Gateway는 필터 로직 실행을 위해 “pre” 및 “post” 단계로 구분합니다. 우선 순위가 가장 높은 필터는 “pre” 단계에서 첫 번째, “post” 단계에서 마지막이 됩니다.

다음은 필터 체인을 구성하는 예입니다:
```java
// ExampleConfiguration.java
@Bean
public GlobalFilter customFilter() {
    return new CustomGlobalFilter();
}

public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("custom global filter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

```
### Gateway Metrics Filter
게이트웨이 메트릭을 활성화하려면 spring-boot-starter-actuator를 프로젝트 종속성으로 추가합니다.   
기본적으로 spring.cloud.gateway.metrics.enabled 속성이 false로 설정되지 않는 한, 게이트웨이 메트릭 필터가 실행됩니다.   
이 필터는 다음 태그를 가진 spring.cloud.gateway.requests라는 타이머 메트릭을 추가합니다:  

```text
routeId: 라우트 ID  
routeUri: API가 라우팅되는 URI  
outcome: HttpStatus.Series로 분류된 결과  
status: 클라이언트에 반환된 요청의 HTTP 상태  
httpStatusCode: 클라이언트에 반환된 요청의 HTTP 상태  
httpMethod: 요청에 사용된 HTTP 메서드  
추가로, spring.cloud.gateway.metrics.tags.path.enabled 속성(기본값: false)을 통해   
path 태그가 있는 추가 메트릭을 활성화할 수 있습니다:  
```


path: 요청 경로
이 메트릭은 /actuator/metrics/spring.cloud.gateway.requests에서 스크랩할 수 있으며,   
Prometheus와 쉽게 통합하여 Grafana 대시보드를 생성할 수 있습니다.  

Prometheus 엔드포인트를 활성화하려면 micrometer-registry-prometheus를 프로젝트 종속성으로 추가합니다.

### Local Response Cache Filter
```text
LocalResponseCache는 관련 속성이 활성화된 경우 실행됩니다:

spring.cloud.gateway.global-filter.local-response-cache.enabled: 모든 라우트에 대한 전역 캐시 활성화  
spring.cloud.gateway.filter.local-response-cache.enabled: 라우트 수준에서 사용할 필터 활성화  
이 기능은 다음 기준을 충족하는 모든 응답에 대해 Caffeine을 사용한 로컬 캐시를 활성화합니다:  

요청이 바디가 없는 GET 요청일 때  
응답이 다음 상태 코드 중 하나일 때: HTTP 200 (OK), HTTP 206 (Partial Content), 또는 HTTP 301 (Moved Permanently)  
HTTP Cache-Control 헤더가 캐싱을 허용할 때 (즉, 요청에 no-store가 없고, 응답에 no-store 또는 private가 없는 경우)  
두 가지 구성 매개변수를 허용합니다:  

spring.cloud.gateway.filter.local-response-cache.size:   
이 라우트에 대한 캐시 항목을 제거할 최대 크기 설정 (KB, MB 및 GB 단위)  

spring.cloud.gateway.filter.local-response-cache.time-to-live:     
캐시 항목의 만료 시간을 설정 (초 단위 s, 분 단위 m, 시간 단위 h)    
이 매개변수가 구성되지 않았지만 전역 필터가 활성화된 경우, 기본적으로 캐시된 응답에 대해 5분의 만료 시간이 설정됩니다.    

이 필터는 HTTP Cache-Control 헤더의 max-age 값을 자동으로 계산하는 기능도 구현합니다.  
원래 응답에 max-age가 존재하면, timeToLive 구성 매개변수에 설정된 초 수로 값이 재작성됩니다.  
이후 호출에서는 응답 만료까지 남은 초 수로 이 값이 재계산됩니다.  

spring.cloud.gateway.global-filter.local-response-cache.enabled를 false로   
설정하면 모든 라우트에 대해 로컬 응답 캐시가 비활성화되며, LocalResponseCache 필터는   
이 기능을 라우트 수준에서 사용할 수 있게 합니다.  

이 기능을 활성화하려면 com.github.ben-manes.caffeine:caffeine과   
spring-boot-starter-cache를 프로젝트 종속성으로 추가합니다.  
프로젝트가 사용자 정의 CacheManager 빈을 생성하는 경우  
@Primary로 표시하거나 @Qualifier를 사용하여 주입해야 합니다.  
```


### Forward Routing Filter
```text
ForwardRoutingFilter는 ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR 교환 속성에서 URI를 찾습니다. 
URL에 forward 스킴(예: forward:///localendpoint)이 있으면 Spring DispatcherHandler를 사용하여 요청을 처리합니다. 
요청 URL의 경로 부분은 전달 URL의 경로로 대체됩니다. 
수정되지 않은 원래 URL은 ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR 속성의 목록에 추가됩니다.
```

### Netty Routing Filter
```text
Netty 라우팅 필터는 ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR 
교환 속성에 있는 URL에 http 또는 https 스킴이 있는 경우 실행됩니다. 
Netty HttpClient를 사용하여 다운스트림 프록시 요청을 수행합니다. 
응답은 나중에 필터에서 사용하기 위해 ServerWebExchangeUtils.CLIENT_RESPONSE_ATTR 교환 속성에 저장됩니다. 
(Netty가 필요하지 않은 동일한 기능을 수행하는 실험적 WebClientHttpRoutingFilter도 있습니다.)
```


### Netty Write Response Filter
```text
NettyWriteResponseFilter는 ServerWebExchangeUtils.CLIENT_RESPONSE_ATTR 
교환 속성에 Netty HttpClientResponse가 있는 경우 실행됩니다.
다른 모든 필터가 완료된 후 실행되며 프록시 응답을 게이트웨이 클라이언트 응답으로 다시 씁니다. 
(Netty가 필요하지 않은 동일한 기능을 수행하는 실험적 WebClientWriteResponseFilter도 있습니다.)
```


### ReactiveLoadBalancerClientFilter
```text
ReactiveLoadBalancerClientFilter는 ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR 
교환 속성에 있는 URI를 찾습니다. URL에 lb 스킴(예: lb://myservice)이 있으면 
Spring Cloud ReactorLoadBalancer를 사용하여 이름(myservice 예제에서)을 실제 호스트와 포트로 확인하고
동일한 속성의 URI를 대체합니다. 
수정되지 않은 원래 URL은 ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR 속성의 목록에 추가됩니다. 
필터는 ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR 속성에서 lb와 동일한지 여부도 확인합니다. 
그렇다면 동일한 규칙이 적용됩니다. 다음은 ReactiveLoadBalancerClientFilter를 구성하는 예입니다:
```


```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: myRoute
        uri: lb://service
        predicates:
        - Path=/service/**

```
```text
기본적으로 ReactorLoadBalancer가 서비스 인스턴스를 찾을 수 없는 경우 503이 반환됩니다. 
spring.cloud.gateway.loadbalancer.use404=true로 설정하여 게이트웨이가 404를 반환하도록 구성할 수 있습니다.

ReactiveLoadBalancerClientFilter에서 반환된 ServiceInstance의 isSecure 값은 게이트웨이에 대한 요청에서 지정된 스킴을 재정의합니다. 
예를 들어, 요청이 HTTPS를 통해 게이트웨이에 들어오지만 ServiceInstance가 안전하지 않은 것으로 
표시된 경우 다운스트림 요청은 HTTP를 통해 이루어집니다. 반대의 상황도 적용될 수 있습니다. 
그러나 라우트에 대해 GATEWAY_SCHEME_PREFIX_ATTR이 지정된 경우 접두사가 제거되고 
라우트 URL에서 나온 결과 스킴이 ServiceInstance 구성을 재정의합니다.

게이트웨이는 모든 로드밸런서 기능을 지원합니다. Spring Cloud Commons 문서에서 자세히 읽을 수 있습니다.

RouteToRequestUrl Filter
ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR 교환 속성에 Route 객체가 있는 경우
 RouteToRequestUrlFilter가 실행됩니다. 요청 URI를 기반으로 하지만 Route 객체의 URI로 업데이트된 새 URI를 생성합니다. 
 새 URI는 ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR 교환 속성에 저장됩니다.

URI에 lb:ws://serviceid와 같은 스킴 접두사가 있는 경우 lb 스킴이 URI에서 제거되고
이후 필터 체인에서 사용하기 위해 ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR에 저장됩니다.

```


### The Websocket Routing Filter
ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR 교환 속성에 있는 URL에
ws 또는 wss 스킴이 있으면 websocket 라우팅 필터가 실행됩니다.
Spring WebSocket 인프라를 사용하여 websocket 요청을 다운스트림으로 전달합니다.

웹소켓을 로드밸런싱하려면 URI에 lb 접두사를 추가합니다. 예: lb:ws://serviceid

SockJS를 일반 HTTP를 통한 대체로 사용하는 경우 일반 HTTP 라우트와 웹소켓 Route를 모두 구성해야 합니다.

다음은 websocket 라우팅 필터를 구성하는 예입니다:
```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
      # SockJS route
      - id: websocket_sockjs_route
        uri: http://localhost:3001
        predicates:
        - Path=/websocket/info/**
      # Normal Websocket route
      - id: websocket_route
        uri: ws://localhost:3001
        predicates:
        - Path=/websocket/**

```
```text
요청이 라우트로 표시하기
게이트웨이가 ServerWebExchange를 라우팅한 후, gatewayAlreadyRouted를 
교환 속성에 추가하여 해당 교환을 “라우트된” 것으로 표시합니다. 
요청이 라우트로 표시되면 다른 라우팅 필터는 요청을 다시 라우팅하지 않아 필터를 건너뛸 수 있습니다. 
교환을 라우트된 것으로 표시하거나 이미 라우트되었는지 확인하는 데 사용할 수 있는 편리한 메서드가 있습니다.

ServerWebExchangeUtils.isAlreadyRouted는 ServerWebExchange 객체를 받아서 “라우트됨”으로 표시되었는지 확인합니다.
ServerWebExchangeUtils.setAlreadyRouted는 ServerWebExchange 객체를 받아서 이를 “라우트됨”으로 표시합니다.
```
---
# HttpHeadersFilters

```text
HttpHeadersFilters 적용
HttpHeadersFilters는 NettyRoutingFilter와 같이 다운스트림으로 요청을 보내기 전에 적용됩니다.

Forwarded Headers Filter
Forwarded Headers Filter는 다운스트림 서비스에 보낼 Forwarded 헤더를 생성합니다. 
현재 요청의 Host 헤더, 스킴 및 포트를 기존 Forwarded 헤더에 추가합니다.

RemoveHopByHop Headers Filter
RemoveHopByHop Headers Filter는 전달된 요청에서 헤더를 제거합니다. 기본적으로 제거되는 헤더 목록은 IETF에서 가져옵니다.

기본적으로 제거되는 헤더는 다음과 같습니다:
```
- Connection
- Keep-Alive
- Proxy-Authenticate
- Proxy-Authorization
- TE
- Trailer
- Transfer-Encoding
- Upgrade


이 목록을 변경하려면 spring.cloud.gateway.filter.remove-hop-by-hop.headers 속성을 제거할 헤더 이름 목록으로 설정합니다.



### XForwarded Headers Filter
XForwarded Headers Filter는 다양한 X-Forwarded-* 헤더를 생성하여 다운스트림 서비스에 보냅니다.  
현재 요청의 Host 헤더, 스킴, 포트 및 경로를 사용하여 다양한 헤더를 생성합니다.

개별 헤더의 생성은 다음의 boolean 속성으로 제어할 수 있습니다 (기본값은 true입니다):

- spring.cloud.gateway.x-forwarded.for-enabled
- spring.cloud.gateway.x-forwarded.host-enabled
- spring.cloud.gateway.x-forwarded.port-enabled
- spring.cloud.gateway.x-forwarded.proto-enabled
- spring.cloud.gateway.x-forwarded.prefix-enabled

여러 헤더를 추가하는 것은 다음의 boolean 속성으로 제어할 수 있습니다 (기본값은 true입니다):

- spring.cloud.gateway.x-forwarded.for-append
- spring.cloud.gateway.x-forwarded.host-append
- spring.cloud.gateway.x-forwarded.port-append
- spring.cloud.gateway.x-forwarded.proto-append
- spring.cloud.gateway.x-forwarded.prefix-append
---
# TLS and SSL

## HTTPS 요청 수신 및 백엔드 라우팅 설정
게이트웨이는 일반적인 Spring 서버 구성을 따라 HTTPS 요청을 수신할 수 있습니다.   
다음 예제는 이를 구현하는 방법을 보여줍니다:

```yaml
# application.yml
server:
  ssl:
    enabled: true
    key-alias: scg
    key-store-password: scg1234
    key-store: classpath:scg-keystore.p12
    key-store-type: PKCS12

```

게이트웨이 라우트를 HTTP 및 HTTPS 백엔드 모두로 라우팅할 수 있습니다. HTTPS 백엔드로 라우팅하는 경우,   
다음 구성으로 게이트웨이가 모든 다운스트림 인증서를 신뢰하도록 설정할 수 있습니다: 

```yaml
# application.yml
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          useInsecureTrustManager: true

```
비신뢰 트러스트 매니저를 사용하는 것은 프로덕션 환경에 적합하지 않습니다.   
프로덕션 배포를 위해서는 다음 구성으로 게이트웨이에 신뢰할 수 있는 인증서 집합을 설정할 수 있습니다:
```yaml
# application.yml
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          trustedX509Certificates:
          - cert1.pem
          - cert2.pem

```
Spring Cloud Gateway가 신뢰할 수 있는 인증서로 프로비저닝되지 않은 경우,  
기본 트러스트 스토어가 사용됩니다 (이는 javax.net.ssl.trustStore 시스템 속성을 설정하여 재정의할 수 있습니다).  

## TLS 핸드셰이크  
게이트웨이는 백엔드로 라우팅하는 데 사용하는 클라이언트 풀을 유지합니다. HTTPS를 통해 통신할 때,   
클라이언트는 TLS 핸드셰이크를 시작합니다. 이 핸드셰이크에는 여러 타임아웃이 관련됩니다.   
이러한 타임아웃은 다음과 같이 구성할 수 있습니다 (기본값 표시):
```yaml
# application.yml
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          handshake-timeout-millis: 10000
          close-notify-flush-timeout-millis: 3000
          close-notify-read-timeout-millis: 0

```
---
# Configuration

Spring Cloud Gateway의 구성은 여러 RouteDefinitionLocator 인스턴스에 의해 구동됩니다. 
다음은 RouteDefinitionLocator 인터페이스의 정의를 보여줍니다:

```java
// RouteDefinitionLocator.java
public interface RouteDefinitionLocator {
	Flux<RouteDefinition> getRouteDefinitions();
}

```

기본적으로 PropertiesRouteDefinitionLocator는 Spring Boot의 
@ConfigurationProperties 메커니즘을 사용하여 프로퍼티를 로드합니다.

앞서 설명한 구성 예제들은 모두 위치 인수를 사용하는 간편 표기법을 사용합니다. 
다음 두 예제는 동일한 기능을 수행합니다:

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: setstatus_route
        uri: https://example.org
        filters:
        - name: SetStatus
          args:
            status: 401
      - id: setstatusshortcut_route
        uri: https://example.org
        filters:
        - SetStatus=401

```

일부 게이트웨이 사용 사례에서는 프로퍼티가 충분하지만,   
일부 프로덕션 사용 사례에서는 데이터베이스와 같은 외부 소스에서 구성을 로드하는 것이 유리할 수 있습니다.  
향후 마일스톤 버전에서는 Redis, MongoDB, Cassandra와 같은 Spring Data Repositories를 기반으로  
한 RouteDefinitionLocator 구현이 포함될 예정입니다.  

## RouteDefinition 메트릭
RouteDefinition 메트릭을 활성화하려면 spring-boot-starter-actuator를 프로젝트 종속성으로 추가합니다.   
그러면 기본적으로 spring.cloud.gateway.metrics.enabled 속성이 true로 설정되어 있는 한 메트릭이 사용 가능합니다.   
spring.cloud.gateway.routes.count라는 게이지 메트릭이 추가되며,   
이 값은 RouteDefinitions의 수입니다.   
이 메트릭은 /actuator/metrics/spring.cloud.gateway.routes.count에서 사용할 수 있습니다.  

---
# Route Metadata Configuration
각 라우트에 대해 추가 매개변수를 구성하려면 메타데이터를 사용할 수 있습니다. 
다음은 메타데이터를 사용하는 예시입니다:

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: route_with_metadata
        uri: https://example.org
        metadata:
          optionName: "OptionValue"
          compositeObject:
            name: "value"
          iAmNumber: 1

```
## 메타데이터 속성 가져오기
exchange에서 모든 메타데이터 속성을 가져오려면 다음과 같이 사용할 수 있습니다:

```java
Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
// 모든 메타데이터 속성 가져오기
route.getMetadata();
// 단일 메타데이터 속성 가져오기
route.getMetadata(someKey);
```

---
# Http timeouts configuration

HTTP 타임아웃(응답 및 연결)은 모든 라우트에 대해 구성할 수 있으며, 각 특정 라우트에 대해 재정의할 수 있습니다.

글로벌 타임아웃 설정
글로벌 HTTP 타임아웃 구성 방법:

connect-timeout은 밀리초(ms) 단위로 지정해야 합니다.
response-timeout은 java.time.Duration 형식으로 지정해야 합니다.
글로벌 HTTP 타임아웃 예시:

```yaml
# global http timeouts example
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000
        response-timeout: 5s

```

라우트별 타임아웃 설정
라우트별 타임아웃 구성 방법:

connect-timeout은 밀리초(ms) 단위로 지정해야 합니다.
response-timeout은 밀리초(ms) 단위로 지정해야 합니다.
구성 파일을 통한 라우트별 HTTP 타임아웃 설정 예시:

```yaml
# per-route http timeouts configuration via configuration
spring:
  cloud:
    gateway:
      routes:
      - id: per_route_timeouts
        uri: https://example.org
        predicates:
          - name: Path
            args:
              pattern: /delay/{timeout}
        metadata:
          response-timeout: 200
          connect-timeout: 200

```

Java DSL을 사용한 라우트별 타임아웃 설정 예시:

```java
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.CONNECT_TIMEOUT_ATTR;
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR;

@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder routeBuilder){
   return routeBuilder.routes()
         .route("test1", r -> {
            return r.host("*.somehost.org").and().path("/somepath")
                  .filters(f -> f.addRequestHeader("header1", "header-value-1"))
                  .uri("http://someuri")
                  .metadata(RESPONSE_TIMEOUT_ATTR, 200)
                  .metadata(CONNECT_TIMEOUT_ATTR, 200);
         })
         .build();
}

```
글로벌 response-timeout 값을 비활성화하려면, 라우트별 response-timeout을 음수 값으로 설정합니다.
```yaml
# per-route response-timeout disabling global response-timeout
spring:
  cloud:
    gateway:
      routes:
      - id: per_route_timeouts
        uri: https://example.org
        predicates:
          - name: Path
            args:
              pattern: /delay/{timeout}
        metadata:
          response-timeout: -1

```

---
# Fluent Java Routes API

Java를 통한 간단한 구성: RouteLocatorBuilder 사용  
RouteLocatorBuilder 빈은 유창한 API를 포함하여 Java에서 간단하게 구성할 수 있습니다.  
다음은 작동 방식을 보여주는 예입니다:

```java
// GatewaySampleApplication.java
// GatewayFilters와 RoutePredicates에서의 정적 임포트
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ThrottleGatewayFilterFactory throttle) {
    return builder.routes()
            .route(r -> r.host("**.abc.org").and().path("/image/png")
                .filters(f ->
                        f.addResponseHeader("X-TestHeader", "foobar"))
                .uri("http://httpbin.org:80")
            )
            .route(r -> r.path("/image/webp")
                .filters(f ->
                        f.addResponseHeader("X-AnotherHeader", "baz"))
                .uri("http://httpbin.org:80")
                .metadata("key", "value")
            )
            .route(r -> r.order(-1)
                .host("**.throttle.org").and().path("/get")
                .filters(f -> f.filter(throttle.apply(1,
                        1,
                        10,
                        TimeUnit.SECONDS)))
                .uri("http://httpbin.org:80")
                .metadata("key", "value")
            )
            .build();
}

```
## 주요 기능 및 구성 예시 설명
첫 번째 라우트:
```text
호스트가 **.abc.org이고 경로가 /image/png인 요청을 처리합니다.
응답 헤더 X-TestHeader에 foobar를 추가합니다.
URI는 http://httpbin.org:80로 설정됩니다.
두 번째 라우트:

경로가 /image/webp인 요청을 처리합니다.
응답 헤더 X-AnotherHeader에 baz를 추가합니다.
URI는 http://httpbin.org:80로 설정되며, 메타데이터 key에 value를 추가합니다.
세 번째 라우트:

우선순위가 -1이며, 호스트가 **.throttle.org이고 경로가 /get인 요청을 처리합니다.
Throttle 필터를 적용하여 1초에 1개의 요청을 허용하며, 최대 10초까지 큐잉합니다.
URI는 http://httpbin.org:80로 설정되며, 메타데이터 key에 value를 추가합니다.
추가 구성 기능
이 스타일은 더 많은 사용자 정의 프레디케이트(assertion)를 허용합니다. 
RouteDefinitionLocator 빈에 의해 정의된 프레디케이트는 논리적 and로 결합됩니다. 
유창한 Java API를 사용하면 Predicate 클래스에서 and(), or(), negate() 연산자를 사용할 수 있습니다.
```

---

# DiscoveryClient Route Definition Locator
(DiscoveryClient와의 연동을 통한 게이트웨이 라우트 생성)
```text

게이트웨이는 DiscoveryClient 호환 서비스 레지스트리와 연동하여 라우트를 생성할 수 있습니다.

기본적으로 생성된 라우트는 lb://service-name 프로토콜을 사용하며(여기서 service-name은 DiscoveryClient::getServices가 반환하는 문자열) 
이는 로드밸런싱됩니다. 따라서 클래스패스에 org.springframework.cloud:spring-cloud-starter-loadbalancer 종속성을 포함해야 합니다.

이를 활성화하려면 spring.cloud.gateway.discovery.locator.enabled=true로 설정하고, 
DiscoveryClient 구현체(예: Netflix Eureka, Consul, Zookeeper 또는 Kubernetes)가 클래스패스에 있고 활성화되어 있는지 확인하십시오.

DiscoveryClient 라우트를 위한 프레디케이트 및 필터 구성
기본적으로 게이트웨이는 DiscoveryClient로 생성된 라우트에 대해 하나의 프레디케이트와 필터를 정의합니다.

기본 프레디케이트: /serviceId/** 패턴을 가진 경로 프레디케이트 (serviceId는 DiscoveryClient의 서비스 ID입니다).
기본 필터: 정규 표현식 /serviceId/?(?<remaining>.*)와 대체 /\${remaining}를 가진 경로 재작성 필터입니다. 
이는 요청이 다운스트림으로 전송되기 전에 경로에서 서비스 ID를 제거합니다.
DiscoveryClient 라우트에서 사용되는 프레디케이트나 필터를 사용자 정의하려면 
spring.cloud.gateway.discovery.locator.predicates[x]와 
spring.cloud.gateway.discovery.locator.filters[y]를 설정합니다. 
이때 기본 프레디케이트와 필터를 유지하려면 위에서 설명한 기본 프레디케이트와 필터를 포함해야 합니다.

```
설정 예시
application.properties 예시:
```properties
spring.cloud.gateway.discovery.locator.predicates[0].name=Path
spring.cloud.gateway.discovery.locator.predicates[0].args[pattern]="'/'+serviceId+'/**'"
spring.cloud.gateway.discovery.locator.predicates[1].name=Host
spring.cloud.gateway.discovery.locator.predicates[1].args[pattern]="'**.foo.com'"
spring.cloud.gateway.discovery.locator.filters[0].name=CircuitBreaker
spring.cloud.gateway.discovery.locator.filters[0].args[name]=serviceId
spring.cloud.gateway.discovery.locator.filters[1].name=RewritePath
spring.cloud.gateway.discovery.locator.filters[1].args[regexp]="'/' + serviceId + '/?(?<remaining>.*)'"
spring.cloud.gateway.discovery.locator.filters[1].args[replacement]="'/$\{remaining}'"

```

```yaml
# Example 2. application.yml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          predicates:
          - name: Host
            args:
              pattern: "'**.foo.com'"
          - name: Path
            args:
              pattern: "'/'+serviceId+'/**'"
          filters:
          - name: CircuitBreaker
            args:
              name: serviceId
          - name: RewritePath
            args:
              regexp: "'/' + serviceId + '/?(?<remaining>.*)'"
              replacement: "'/${remaining}'"

```

---
# Reactor Netty Access Logs
(Reactor Netty 액세스 로그 활성화 및 Logback 구성)
```text
Reactor Netty 액세스 로그를 활성화하려면 -Dreactor.netty.http.server.accessLogEnabled=true로 설정합니다. 
이는 Java 시스템 속성으로 설정해야 하며, Spring Boot 속성이 아닙니다.

액세스 로그 파일을 위한 Logback 설정
액세스 로그 파일을 별도로 설정하려면 로그백(Logback) 구성을 사용할 수 있습니다. 다음은 Logback 설정 예시입니다:
```

logback.xml 예시:
```xml
<configuration>
    <appender name="accessLog" class="ch.qos.logback.core.FileAppender">
        <file>access_log.log</file>
        <encoder>
            <pattern>%msg%n</pattern>
        </encoder>
    </appender>

    <appender name="async" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="accessLog" />
    </appender>

    <logger name="reactor.netty.http.server.AccessLog" level="INFO" additivity="false">
        <appender-ref ref="async"/>
    </logger>
</configuration>
```
구성 요약
Java 시스템 속성 설정:
```shell
-Dreactor.netty.http.server.accessLogEnabled=true
```

Logback 구성 파일 (logback.xml):

- accessLog 파일에 로그를 기록하는 FileAppender를 정의합니다.
- 비동기 로깅을 위해 AsyncAppender를 설정합니다.
- reactor.netty.http.server.AccessLog 로거를 INFO 레벨로 설정하고, 비동기 appender를 참조하도록 합니다.

참고 사항
- 패턴 설정: 로그 메시지의 형식을 %msg%n로 지정하여, 액세스 로그 메시지를 출력합니다.
- 파일 경로: <file>access_log.log</file>에서 access_log.log 파일의 경로를 설정할 수 있습니다. 필요에 따라 절대 경로를 지정할 수 있습니다.
- 레벨: INFO 수준으로 설정하여 적절한 로깅을 보장합니다.

이 구성을 통해 Reactor Netty의 액세스 로그를 활성화하고 별도의 파일로 기록할 수 있습니다.

---
# CORS Configuration (CORS 동작 제어를 위한 게이트웨이 구성)
Spring Cloud Gateway는 글로벌 또는 라우트별로 CORS(Cross-Origin Resource Sharing) 동작을 제어할 수 있습니다.   
두 가지 모두 동일한 설정 기능을 제공합니다.

## 글로벌 CORS 설정
글로벌 CORS 구성은 URL 패턴과 Spring Framework CorsConfiguration의 매핑입니다.  
다음 예제는 CORS를 구성하는 방법을 보여줍니다:
```yaml
# application.yml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "https://docs.spring.io"
            allowedMethods:
            - GET

```
위 예제에서는 docs.spring.io에서 시작된 모든 GET 요청 경로에 대해 CORS 요청이 허용됩니다.

프리플라이트 요청 지원을 위한 설정:

게이트웨이 라우트 프레디케이트로 처리되지 않는 요청에 동일한 CORS 구성을 제공하려면, 
spring.cloud.gateway.globalcors.add-to-simple-url-handler-mapping 속성을 true로 설정합니다. 
이는 HTTP 메서드가 OPTIONS인 경우 라우트 프레디케이트가 true로 평가되지 않아도 CORS 프리플라이트 요청을 지원하려는 경우에 유용합니다.
```yaml
# application.yml (추가 설정)
spring:
  cloud:
    gateway:
      globalcors:
        add-to-simple-url-handler-mapping: true

```
##라우트 CORS 설정
라우트 CORS 구성은 cors 키로 메타데이터로 CORS를 라우트에 직접 적용할 수 있게 합니다.   
글로벌 구성과 마찬가지로 속성은 Spring Framework CorsConfiguration에 속합니다.  

라우트에 Path 프레디케이트가 없으면, '/'가 적용됩니다.**  

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
      - id: cors_route
        uri: https://example.org
        predicates:
        - Path=/service/**
        metadata:
          cors:
            allowedOrigins: '*'
            allowedMethods:
              - GET
              - POST
            allowedHeaders: '*'
            maxAge: 30

```
구성 요약
글로벌 CORS 설정:

- 모든 요청 경로에 대해 docs.spring.io에서 시작된 GET 요청을 허용.
- add-to-simple-url-handler-mapping을 true로 설정하여 프리플라이트 요청을 지원.

라우트별 CORS 설정:

- /service/** 경로에 대해 모든 오리진에서 시작된 GET 및 POST 요청을 허용.
- 모든 헤더를 허용하며, maxAge는 30초로 설정.

주요 속성 설명
- allowedOrigins: 허용할 오리진(URL) 목록.
- allowedMethods: 허용할 HTTP 메서드 목록.
- allowedHeaders: 허용할 HTTP 헤더 목록.
- maxAge: CORS 요청의 응답을 캐시할 시간(초 단위).


이 구성을 통해 글로벌 또는 특정 라우트에 대한 CORS 동작을 쉽게 제어할 수 있습니다.

---
# Actuator API (Spring Cloud Gateway Actuator 엔드포인트 설정 및 사용)

/gateway 액추에이터 엔드포인트를 통해 Spring Cloud Gateway 애플리케이션을 모니터링하고 상호작용할 수 있습니다.  
이 엔드포인트를 원격으로 접근하려면 애플리케이션 속성에서 활성화하고 HTTP 또는 JMX를 통해 노출해야 합니다.  
다음은 이를 설정하는 방법입니다:  

```properties
management.endpoint.gateway.enabled=true  # 기본값
management.endpoints.web.exposure.include=gateway
```

제공되는 엔드포인트
이 엔드포인트는 사용 가능한 하위 액추에이터 엔드포인트와   
각 참조에 대한 사용 가능한 메서드의 개요를 제공합니다. 결과 응답은 다음과 유사합니다:  

```json
[
   {
      "href":"/actuator/gateway/",
      "methods":[ "GET" ]
   },
   {
      "href":"/actuator/gateway/routedefinitions",
      "methods":[ "GET" ]
   },
   {
      "href":"/actuator/gateway/globalfilters",
      "methods":[ "GET" ]
   },
   {
      "href":"/actuator/gateway/routefilters",
      "methods":[ "GET" ]
   },
   {
      "href":"/actuator/gateway/routes",
      "methods":[ "POST", "GET" ]
   },
   {
      "href":"/actuator/gateway/routepredicates",
      "methods":[ "GET" ]
   },
   {
      "href":"/actuator/gateway/refresh",
      "methods":[ "POST" ]
   },
   {
      "href":"/actuator/gateway/routes/route-id-1/combinedfilters",
      "methods":[ "GET" ]
   },
   {
      "href":"/actuator/gateway/routes/route-id-1",
      "methods":[ "POST", "DELETE", "GET" ]
   }
]

```
## 자세한 액추에이터 형식 (Verbose Actuator Format)  
Spring Cloud Gateway에는 각 라우트에 대한 프레디케이트와 필터를 자세히 볼 수 있는 더 자세한 형식이 추가되었습니다.   
/actuator/gateway/routes 구성을 설정하는 예는 다음과 같습니다:  

```json
[
  {
    "predicate": "(Hosts: [**.addrequestheader.org] && Paths: [/headers], match trailing slash: true)",
    "route_id": "add_request_header_test",
    "filters": [
      "[[AddResponseHeader X-Response-Default-Foo = 'Default-Bar'], order = 1]",
      "[[AddRequestHeader X-Request-Foo = 'Bar'], order = 1]",
      "[[PrefixPath prefix = '/httpbin'], order = 2]"
    ],
    "uri": "lb://testservice",
    "order": 0
  }
]

```
기본적으로 활성화된 이 기능을 비활성화하려면 다음 속성을 설정합니다:
```properties
spring.cloud.gateway.actuator.verbose.enabled=false

```
라우트 필터 검색
글로벌 필터

모든 라우트에 적용된 글로벌 필터를 검색하려면 /actuator/gateway/globalfilters에 GET 요청을 보냅니다.   
결과 응답 예시는 다음과 같습니다:
```json
{
  "org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter@77856cc5": 10100,
  "org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter@4f6fd101": 10000,
  "org.springframework.cloud.gateway.filter.NettyWriteResponseFilter@32d22650": -1,
  "org.springframework.cloud.gateway.filter.ForwardRoutingFilter@106459d9": 2147483647,
  "org.springframework.cloud.gateway.filter.NettyRoutingFilter@1fbd5e0": 2147483647,
  "org.springframework.cloud.gateway.filter.ForwardPathFilter@33a71d23": 0,
  "org.springframework.cloud.gateway.filter.AdaptCachedBodyGlobalFilter@135064ea": 2147483637,
  "org.springframework.cloud.gateway.filter.WebsocketRoutingFilter@23c05889": 2147483646
}

```
라우트 필터

특정 라우트에 적용된 GatewayFilter 팩토리를 검색하려면 /actuator/gateway/routefilters에 GET 요청을 보냅니다. 
결과 응답 예시는 다음과 같습니다:
```json
{
  "[AddRequestHeaderGatewayFilterFactory@570ed9c configClass = AbstractNameValueGatewayFilterFactory.NameValueConfig]": null,
  "[SecureHeadersGatewayFilterFactory@fceab5d configClass = Object]": null,
  "[SaveSessionGatewayFilterFactory@4449b273 configClass = Object]": null
}

```
  
라우트 캐시 새로 고침  
라우트 캐시를 지우려면 /actuator/gateway/refresh에 POST 요청을 보냅니다. 요청은 본문 없이 200을 반환합니다.  

특정 메타데이터 값을 가진 라우트를 새로 고치려면, 쿼리 매개변수 metadata에 일치해야 하는 key:value 쌍을 지정합니다.  

```json
// 예시: /actuator/gateway/refresh?metadata=group:group-1
[{
  "route_id": "first_route",
  "route_object": {
    "predicate": "...",
  },
  "metadata": { "group": "group-1" }
},
{
  "route_id": "second_route",
  "route_object": {
    "predicate": "...",
  },
  "metadata": { "group": "group-2" }
},
{
  "route_id": "third_route",
  "route_object": {
    "predicate": "...",
  },
  "metadata": { "group": "group-1" }
}]

```
## 게이트웨이에 정의된 라우트 검색
게이트웨이에 정의된 라우트를 검색하려면 /actuator/gateway/routes에 GET 요청을 보냅니다.   
결과 응답 예시는 다음과 같습니다:
```json
[{
  "route_id": "first_route",
  "route_object": {
    "predicate": "org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory$$Lambda$432/1736826640@1e9d7e7d",
    "filters": [
      "OrderedGatewayFilter{delegate=org.springframework.cloud.gateway.filter.factory.PreserveHostHeaderGatewayFilterFactory$$Lambda$436/674480275@6631ef72, order=0}"
    ]
  },
  "order": 0
},
{
  "route_id": "second_route",
  "route_object": {
    "predicate": "org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory$$Lambda$432/1736826640@cd8d298",
    "filters": []
  },
  "order": 0
}]

```
![스크린샷 2024-05-12 오후 8.33.49.png](..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-12%20%EC%98%A4%ED%9B%84%208.33.49.png)

## 특정 라우트 정보 검색
특정 라우트 정보를 검색하려면 /actuator/gateway/routes/{id}   
(예: /actuator/gateway/routes/first_route)에 GET 요청을 보냅니다. 결과 응답 예시는 다음과 같습니다:  

```json
{
  "id": "first_route",
  "predicates": [{
    "name": "Path",
    "args": {"_genkey_0":"/first"}
  }],
  "filters": [],
  "uri": "https://www.uri-destination.org",
  "order": 0
}

```
![스크린샷 2024-05-12 오후 8.35.00.png](..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-12%20%EC%98%A4%ED%9B%84%208.35.00.png)

## 라우트 정의 생성 및 삭제
라우트 정의를 생성하려면 /gateway/routes/{id_route_to_create}에 JSON 본문을 포함하여 POST 요청을 보냅니다.
라우트 정의를 삭제하려면 /gateway/routes/{id_route_to_delete}에 DELETE 요청을 보냅니다.  

## 여러 라우트 정의 생성
여러 라우트 정의를 한 번에 생성하려면 /gateway/routes에 JSON 본문을 포함하여 POST 요청을 보냅니다.   
라우트 생성 중 오류가 발생하면 모든 라우트 정의가 폐기됩니다.

## 엔드포인트 요약
![스크린샷 2024-05-12 오후 8.36.20.png](..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-12%20%EC%98%A4%ED%9B%84%208.36.20.png)

---

# Sharing Routes between multiple Gateway instances (여러 게이트웨이 인스턴스 간의 라우트 공유)

Spring Cloud Gateway는 두 가지 RouteDefinitionRepository 구현을 제공합니다.
첫 번째는 InMemoryRouteDefinitionRepository로, 하나의 게이트웨이 인스턴스 내에서만 메모리로 존재합니다. 
이 유형의 리포지토리는 여러 게이트웨이 인스턴스 간에 라우트를 공유하는 데 적합하지 않습니다.

RedisRouteDefinitionRepository 사용
여러 Spring Cloud Gateway 인스턴스 간에 라우트를 공유하려면 RedisRouteDefinitionRepository를 사용할 수 있습니다.
이 리포지토리를 활성화하려면 다음 속성을 true로 설정해야 합니다:

```properties
spring.cloud.gateway.redis-route-definition-repository.enabled=true

```
또한, RedisRateLimiter 필터 팩토리와 마찬가지로 spring-boot-starter-data-redis-reactive Spring Boot 스타터를 사용해야 합니다.

예시 구성
application.properties 예시:
```properties
# RedisRouteDefinitionRepository 활성화
spring.cloud.gateway.redis-route-definition-repository.enabled=true

# RedisRateLimiter 필터를 사용하는 경우 (옵션)
# spring.cloud.gateway.redis-rate-limiter.enabled=true

```

의존성 추가:

pom.xml 파일에 spring-boot-starter-data-redis-reactive 의존성을 추가합니다.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```
RedisRouteDefinitionRepository 구성 요약
속성 설정:

spring.cloud.gateway.redis-route-definition-repository.enabled=true를
설정하여 Redis 기반 라우트 정의 리포지토리를 활성화합니다.
의존성 추가:

spring-boot-starter-data-redis-reactive를 프로젝트에 포함시킵니다.
구성 예제 설명
이 구성을 통해 여러 Spring Cloud Gateway 인스턴스에서 동일한 Redis 인스턴스를 사용하여 라우트 정의를 공유할 수 있습니다.
이는 게이트웨이 인스턴스 간에 일관된 라우팅 구성을 유지하는 데 유용합니다.
Redis를 사용하면 확장성과 일관성이 향상되며, 여러 인스턴스에서 라우트를 동기화할 수 있습니다.
---
# Troubleshooting (트러블슈팅)
## Spring Cloud Gateway 사용 시 발생할 수 있는 일반적인 문제 해결

## 로그 레벨
문제 해결에 유용한 정보를 제공하는 로그 레벨은 다음과 같습니다. DEBUG 및 TRACE 레벨에서 값을 확인할 수 있습니다:

- org.springframework.cloud.gateway
- org.springframework.http.server.reactive
- org.springframework.web.reactive
- org.springframework.boot.autoconfigure.web
- reactor.netty
- redisratelimiter

## Wiretap
Reactor Netty HttpClient와 HttpServer는 wiretap을 활성화할 수 있습니다.   
reactor.netty 로그 레벨을 DEBUG 또는 TRACE로 설정하면, 전송된 헤더와 본문 등의 정보를 로깅할 수 있습니다.  
Wiretap을 활성화하려면 다음 속성을 설정합니다:  

## HttpServer wiretap 활성화:
```properties
spring.cloud.gateway.httpserver.wiretap=true
```

## HttpClient wiretap 활성화:
```properties
spring.cloud.gateway.httpclient.wiretap=true

```
설정 요약
로그 레벨 설정 예시:

application.properties
```properties
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.http.server.reactive=DEBUG
logging.level.org.springframework.web.reactive=DEBUG
logging.level.org.springframework.boot.autoconfigure.web=DEBUG
logging.level.reactor.netty=TRACE
logging.level.redisratelimiter=DEBUG

```
Wiretap 활성화 예시:

application.properties
```properties
# HttpServer wiretap 활성화
spring.cloud.gateway.httpserver.wiretap=true

# HttpClient wiretap 활성화
spring.cloud.gateway.httpclient.wiretap=true

```
문제 해결을 위한 단계
로그 레벨 설정:

- 필요한 로거에 대해 DEBUG 또는 TRACE 레벨로 설정하여 상세한 로그 정보를 확인합니다.
Wiretap 활성화:

- 네트워크 트래픽의 헤더와 본문을 로깅하여 전송된 데이터를 분석합니다.
로그 분석:

- 생성된 로그를 통해 문제의 원인을 파악합니다.
구성 확인:

- 문제 발생 시 구성 파일(application.properties 또는 application.yml)을 다시 확인하여 잘못된 설정이 없는지 점검합니다.

위의 설정과 단계를 통해 Spring Cloud Gateway의 문제를 효과적으로 진단하고 해결할 수 있습니다.

---
# Developer Guide
개발자 가이드: Spring Cloud Gateway의 사용자 정의 구성 요소 작성  
사용자 정의 Route Predicate 팩토리 작성  
Route Predicate를 작성하려면 RoutePredicateFactory를 빈으로 구현해야 합니다.  
AbstractRoutePredicateFactory라는 추상 클래스를 확장할 수 있습니다.  

예시: MyRoutePredicateFactory.java
```java
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

@Component
public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config> {

    public MyRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        // Config 객체에서 설정 가져오기
        return exchange -> {
            // 요청 가져오기
            ServerHttpRequest request = exchange.getRequest();
            // 요청 정보를 기반으로 설정과 일치 여부 확인
            return matches(config, request);
        };
    }

    public static class Config {
        // 필터의 구성 속성을 여기에 추가
    }

    private boolean matches(Config config, ServerHttpRequest request) {
        // 요청이 설정과 일치하는지 확인하는 로직 구현
        return true;
    }
}

```
사용자 정의 GatewayFilter 팩토리 작성  
GatewayFilter를 작성하려면 GatewayFilterFactory를 빈으로 구현해야 합니다.  
AbstractGatewayFilterFactory라는 추상 클래스를 확장할 수 있습니다.  

예시 1: PreGatewayFilterFactory.java
```java
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class PreGatewayFilterFactory extends AbstractGatewayFilterFactory<PreGatewayFilterFactory.Config> {

    public PreGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Config 객체에서 설정 가져오기
        return (exchange, chain) -> {
            // "pre" 필터를 빌드하려면 chain.filter를 호출하기 전에 요청을 조작
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            // builder를 사용하여 요청 조작
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }

    public static class Config {
        // 필터의 구성 속성을 여기에 추가
    }
}

```

예시 2: PostGatewayFilterFactory.java
```java
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PostGatewayFilterFactory extends AbstractGatewayFilterFactory<PostGatewayFilterFactory.Config> {

    public PostGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Config 객체에서 설정 가져오기
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            // 응답을 조작하는 로직 추가
        }));
    }

    public static class Config {
        // 필터의 구성 속성을 여기에 추가
    }
}

```
필터 및 구성 파일에서의 참조  
사용자 정의 필터 클래스 이름은 GatewayFilterFactory로 끝나야 합니다.   
예를 들어, 구성 파일에서 Something 필터를 참조하려면 필터 클래스는 SomethingGatewayFilterFactory여야 합니다.  
```java
// SomethingGatewayFilterFactory.java
@Component
public class SomethingGatewayFilterFactory extends AbstractGatewayFilterFactory<SomethingGatewayFilterFactory.Config> {
    // 구현 내용
}

```

사용자 정의 글로벌 필터 작성  
모든 요청에 필터를 적용하려면 GlobalFilter 인터페이스를 빈으로 구현해야 합니다.

예시: 글로벌 프리 필터
```java
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class CustomGlobalFilters {

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> exchange.getPrincipal()
            .map(Principal::getName)
            .defaultIfEmpty("Default User")
            .map(userName -> {
              // 프록시 요청에 헤더 추가
              exchange.getRequest().mutate().header("CUSTOM-REQUEST-HEADER", userName).build();
              return exchange;
            })
            .flatMap(chain::filter);
    }

    @Bean
    public GlobalFilter customGlobalPostFilter() {
        return (exchange, chain) -> chain.filter(exchange)
            .then(Mono.just(exchange))
            .map(serverWebExchange -> {
              // 응답에 헤더 추가
              serverWebExchange.getResponse().getHeaders().set("CUSTOM-RESPONSE-HEADER",
                  HttpStatus.OK.equals(serverWebExchange.getResponse().getStatusCode()) ? "It worked": "It did not work");
              return serverWebExchange;
            })
            .then();
    }
}

```

이러한 예시를 통해 Spring Cloud Gateway에서 사용자 정의 구성 요소를 쉽게 작성하고 구성할 수 있습니다.   
각 구성 요소는 필요에 따라 요청 또는 응답을 조작할 수 있습니다.

---
# AOT and Native Image Support
Spring Cloud Gateway 4.0.0: Spring AOT 및 네이티브 이미지 지원  
Spring Cloud Gateway 4.0.0부터 Spring AOT 변환 및 네이티브 이미지를 지원합니다.  

로드밸런싱 라우트 사용 시 LoadBalancerClient 서비스 ID 정의  
로드밸런싱 라우트를 사용하는 경우, LoadBalancerClient 서비스 ID를 명시적으로 정의해야 합니다.   
이를 위해 @LoadBalancerClient 애노테이션의 value 또는 name 속성을 사용하거나,   
spring.cloud.loadbalancer.eager-load.clients 속성에 값을 추가할 수 있습니다.  

@LoadBalancerClient 애노테이션 사용 예시  
예시 1: @LoadBalancerClient 사용  
```java
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@LoadBalancerClients({
    @LoadBalancerClient(name = "serviceA"),
    @LoadBalancerClient(name = "serviceB")
})
public class LoadBalancerConfiguration {
    // 추가 구성 또는 빈 정의
}

```
spring.cloud.loadbalancer.eager-load.clients 속성 사용 예시  
application.properties 예시:
```properties
spring.cloud.loadbalancer.eager-load.clients=serviceA,serviceB

```
```yaml
spring:
  cloud:
    loadbalancer:
      eager-load:
        clients:
          - serviceA
          - serviceB

```
## 주요 사항 요약
1. Spring AOT 및 네이티브 이미지 지원:

- Spring Cloud Gateway는 Spring AOT 변환 및 네이티브 이미지를 지원하여 애플리케이션 성능과 시작 속도를 개선합니다.

2. LoadBalancerClient 서비스 ID 정의:

- @LoadBalancerClient 애노테이션을 사용하거나 spring.cloud.loadbalancer.eager-load.clients 속성에 서비스 ID를 추가하여 로드밸런싱 라우트의 서비스 ID를 명시적으로 정의합니다.

3. 구성 예시:

- @LoadBalancerClient: 여러 서비스 ID를 선언하고 구성에 반영합니다.
- application.properties / application.yml: spring.cloud.loadbalancer.eager-load.clients 속성에 서비스 ID 목록을 추가합니다.

이 구성을 통해 로드밸런싱 라우트를 명확히 정의하고,   
Spring Cloud Gateway에서 Spring AOT 및 네이티브 이미지를 효과적으로 활용할 수 있습니다.

---
# Configuration properties
Spring Cloud Gateway 관련 구성 속성 목록 확인  
Spring Cloud Gateway의 모든 구성 속성 목록은 부록(Appendix)에서 확인할 수 있습니다.   
이 목록은 Spring Cloud Gateway에서 사용할 수 있는 다양한 구성 옵션을 제공합니다.  

주요 구성 속성 예시
1. Global CORS 설정
```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "https://example.org"
            allowedMethods:
            - GET

```
2. Redis 기반 라우트 정의 리포지토리
```yaml
spring:
  cloud:
    gateway:
      redis-route-definition-repository:
        enabled: true

```
3. HTTP 타임아웃 설정
Global 타임아웃
```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000 # 밀리초
        response-timeout: 5s # java.time.Duration

```
Per-Route 타임아웃
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: per_route_timeouts
        uri: https://example.org
        predicates:
        - Path=/delay/{timeout}
        metadata:
          response-timeout: 200 # 밀리초
          connect-timeout: 200 # 밀리초

```
4. 로드밸런서 서비스 ID 설정
```yaml
spring:
  cloud:
    loadbalancer:
      eager-load:
        clients:
          - serviceA
          - serviceB

```
5. 액추에이터 엔드포인트 활성화
```properties
management.endpoint.gateway.enabled=true
management.endpoints.web.exposure.include=gateway

```
## 부록에서 구성 속성 확인
Spring Cloud Gateway의 전체 구성 속성 목록을 확인하려면, 공식 문서의 부록(Appendix) 섹션을 참조해야 합니다.   
여기에는 다양한 구성 옵션 및 속성에 대한 자세한 설명이 포함되어 있습니다.  

## 공식 문서 링크
Spring Cloud Gateway의 최신 구성 속성 및 문서 링크는 다음과 같습니다:  

Spring Cloud Gateway Documentation  
위의 문서를 참고하여 Spring Cloud Gateway의 다양한 설정 및 구성 옵션을 확인하고 활용할 수 있습니다.  


---


---


---


---


---