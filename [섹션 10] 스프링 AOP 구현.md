

## 1. 포인트 컷의 적용

### 1.1 기본 적용법

기본 적용이다. `hello.aop.order` 패키지와 그 하위 패키지 `..`를 지정하는 AspectJ 표현식 사용 <br>
-> `execution(* hello.aop.order..*(..))`

```java
@Slf4j
@Aspect
public class AspectV1 {

    @Around("execution(* hello.aop.order..*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}

```

<br>

`joinPoint.getSignature()`로 메서드의 시그니처를 기록해봤다.

<br>

실제로 AspectJ를 사용하는 것은 아니다. 스프링 AOP는 단지 AspectJ의 문법을 지원할 뿐이다.  <br>
`@Aspec`를 포함한 `org.aspectj` 패키지 관련 기능은 `aspectjweaver.jar` 라이브러리가 제공 <br>
**하지만 계속 말 하지만 AspectJ가 제공하는 어노테이션이나 관련 인터페이스만 사용하고, 실제 AspectJ가 제공하는 컴파일, 로드타임 위버 등을 사용하는 것은 아님 <br>
스프링은 프록시 방식 AOP를 사용함** <br>


### 1.2 포인트 컷 시그니처 적용

```java

@Slf4j
@Aspect
public class AspectV2 {

    // hello.aop.order 패키지와 하위 패키지
    // MEMO : 포인트 컷 시그니처!
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder() {}

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); 
        return joinPoint.proceed();
    }

    @Around("allOrder() && allService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}

```
포인트 컷 시그니처를 사용해서 여러군데에서 편하게 쓸 수 있게 됐다. <br>
논리 연산자와 함께 여러 조건 하에 적용할 수도 있다.

### 1.3 포인트컷 클래스 운용

```java
public class Pointcuts {

    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder() {}

    // 클래스 이름 패턴이 *Service
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService() {}

    // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service 임
    @Pointcut("allOrder() && allService()")
    public void allOrderAndService() {}
}
```

위와 같이 포인트 컷들을 모아둔 클래스를 만들어서 사용해도 된다. <br>
사용은 아래와 같이 패키지명 전체를 적으며 사용해야 한다.
```java
@Slf4j
@Aspect
public class AspectV4 {

    @Around("hello.aop.order.aop.Pointcuts.allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service 임
    @Around("hello.aop.order.aop.Pointcuts.allOrderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}
```

### 1.4 어드바이스 순서 적용
어드바이스의 순서는 오직 @`Aspect` 단위로만 지정할 수 있다. <br>
따라서, 불편하겠지만 inner class를 사용해서 해결해야 한다. <br>
inner class를 만든 다음`@Aspect`를 달아주고 `Order(N)` 어노테이션을 통해 순서를 지정해준다.

```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Slf4j
public class AspectV5Order {

    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처
            return joinPoint.proceed();
        }
    }

    @Aspect
    @Order(1)
    public static class TransactionAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
            try {
                log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                Object result = joinPoint.proceed();
                log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
                return result;
            } catch (Exception e) {
                log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                throw e;
            } finally {
                log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
            }
        }
    }
}
```
