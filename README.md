# kotlin-workshop

Kotlin examples

### Kotlin Basic

Kotlin을 처음 접하는 초보자를 위한 예제 

### Kotlin Coroutines

Kotlin Coroutines 와 관련된 예제 

### Kotlin Tests

Kotlin 개발 시 JUnit5 와 TestContainers 를 활용한 테스트 예제 및 라이브러리 

### Spring Boot 

Spring Boot 2 에서 Kotlin을 활용하는 예제들

#### redis

Redis 서버를 사용하는 예 

#### resilience4j

[resilience4j](https://github.com/resilience4j/resilience4j) 를 spring boot 2에서 사용하는 예제입니다. 
`@CircuitBreaker`, `@Retry`, `@RateLimiter`, `@Bulkhead` 등 resilience4j 가 제공하는 기능을 annotation을 통해 사용할 수 있습니다. 

#### route

Kotlin 언어의 DSL을 이용하여 route를 정의하는 예 

#### webflux

Spring Framework 5 의 webflux 와 Reactive Mongo를 이용하여 Nonblocking REST API 예를 보여줍니다.

#### webmvc

Spring Framework 의 전통적인 방식은 webmvc 를 이용한 REST API 예

### Spring Data

다양한 spring-data-xxxx 프로젝트에 대한 예제들 

#### jdbc

spring-data-jdbc 에 대한 예제
 
spring-data-jdbc는 spring-data-jpa 만으로는 성능 상의 문제가 있는 부분에 대안으로 적용할 수 있습니다.
예를 들어, 대량 Insert 또는 통계 조회 등에 대안으로 사용 가능합니다.   

#### jOOQ

spring-data-jdbc 와 jOOQ 를 혼합해서 사용하는 예제, insert/update 등은 spring-data-jdbc를 사용하고,
복잡한 쿼리는 jOOQ를 사용하도록 합니다.

#### jpa

spring-data-jpa 에 대한 예제

다양한 entity mapping 방식과 사용법에 대한 예제를 제공합니다. 

#### multi-store

다른 종류의 저장소인 DB와 MongoDB을 동시에 사용하여 Entity를 관리하는 예  