# 프록시 패턴과 데코레이터 패턴
실무에서는 스프링 빈으로 등록할 클래스의 종류가 다양하다.  
인터페이스가 있는 경우도 있고, 없는 경우 모두 있고, <br>
스프링 빈을 수동으로 등록하는 경우도 있고, 컴포넌트 스캔으로 자동 등록하는 경우도 있다. <br>

이런 다양한 케이스에 프록시를 어떻게 적용하는지 알아보기 위해 다양항 예제를 준비해보자. <br>
아래 3가지 상황에서 프록시를 적용하는 것이 목표이다. 

1. v1 : 인터페이스와 구현 클래스 - 스프링 빈으로 수동 등록
2. v2 : 인터페이스 없는 구체 클래스 - 스프링 빈으로 수동 등록
3. v3 : 컴포넌트 스캔으로 스프링 빈 자동 등록


## 클라이언트 서버 모델
우리가 잘 아는 클라이언트와 서버의 관계를 요청해보자. <br>
서버에 필요한 것을 요청하는 객체를 클라이언트라고 하고,
클라이언트의 요청을 처리해주는 객체를 서버 객체라고 부른다.

이떄 클라이언트는 서버의 대리자를 세워 대리자에게 요청을 보내고, <br>
대리자가 서버에게 요청을 보내줄 수 있는데,  
간접 호출을 위해 중간에 끼게 되는 대리자를 Proxy라고 부른다

프록시는 서버와 대체 가능해야 하므로, 같은 인터페이스를 구현중이여야 하고, <br>
DI를 사용하면 클라이언트 코드의 변경 없이 유연하게 프록시를 주입할 수 있다.

## 프록시를 통해서 할 수 있는 일
프록시 객체가 중간에 있으면 정말 많은 것이 가능하다. <br>
정말로 간단하게 생각하자면, 내가 하려는 주요 작업에 부가적인 작업을 감싸줄 수 있다. <br> 
예를 들어 해당 작업을 수행하기 전에 권한을 확인한다던지, <br>
작업이 끝나면 로그를 남긴다던지, 여러 부가 기능들을 원본 작업 코드를 건들지 않고 추가적으로 수행 할 수 있다.

1. 접근제어 
   - 권한에 따른 접근 차단 : 리소스 접근 제어
   - 캐싱 : 프록시가 중간에서 서버 접근을 제한하고 미리 저장된 자료를 돌려줌
   - 지연 로딩 : 클라이언트가 프록시를 통해 작업을 처리하다가, <br> 실제 요청이 있을 때 데이터를 조회한다.
2. 부가 기능 추가 : 원래 제공하는 기능에 더해, 부가 기능을 수행할 수 있다. <br>
   -> ex) 요청 값이나, 응답 값을 중간에 변형하기, 실행 시간을 측정해 추가 로그 남기기  

사실 둘 다 프록시를 이용해 주요 관심사 외에 부가적인 기능을 추가한다는 점에서 동일하나,
GOF 디자인 패턴에서는 이 둘을 의도에 따라 "프록시 패턴"과 "데코레이터 패턴"으로 구분한다.

# 1. 프록시 패턴과 데코레이터 패턴
- 프록시 패턴 : 접근 제어
- 데코레이터 패턴 : 새로운 기능 추가가 목적

둘 다 프록시를 사용하는 것은 같지만, 다르게 분류된다.

```java

@Slf4j
public class RealSubject implements Subject {

   @Override
   public String operation() {
      log.info("실제 객체 호출");
      sleep(1000);
      return "data";
   }

   ...
}

public class ProxyPatternClient {

   ...

   public void execute() {
      subject.operation();
   }
}
```
위 클라이언트가 찍는 로그의 내용물은 항상 값이 같다.
만약 excute() 를 여러번 호출한다면, 내용물을 캐싱해 두는 것이 좋을 것이다.

```java
public class ProxyPatternTest {

   @Test
   void noProxyTest() {
      RealSubject realSubject = new RealSubject();
      ProxyPatternClient client = new ProxyPatternClient(realSubject);
      client.execute();
      client.execute();
      client.execute();
   }
}
```

이런 상황에서 프록시를 이용해 캐싱해보자.

## 1.1 프록시 패턴 - 캐싱
일단 전체적인 그림은 `client -> realSubject` 와 같은 모습에서

`client -> Proxy -> realSubject`와 같은 모습으로 만드는 것이다. <br>
그리고 중간 프록시에서 캐싱을 해주면 된다. <br>
**프록시를 만들어 주자. 프록시는 반드시 실제 객체를 대체할 수 있어야 한다.**

```java
@Slf4j
public class CacheProxy implements Subject {

    private Subject target;
    private String cacheValue;

    public CacheProxy(Subject target) {
        this.target = target;
    }

    @Override
    public String operation() {
        log.info("프록시 호출");
        if (cacheValue == null) {
            cacheValue = target.operation();
        }
        return cacheValue;
    }
}
```
내부적으로 Subject를 저장하고 있다.

중요한건 operation 메서드인데, `cacheValue` 라는 필드에 값이 저장되어 있지 않다면,
실제 객체의 operation을 호출하고, 아닌 경우엔 미리 저장해둔 값을 돌려준다.

결과는 아래와 같이 나오게 된다.
```java
public class ProxyPatternTest {

   ...

   @Test
   void cacheProxyTest() {
      // 실제 객체를 넣어준다.
      Subject realSubject = new RealSubject();
      Subject cacheProxy = new CacheProxy(realSubject);

      ProxyPatternClient client = new ProxyPatternClient(cacheProxy);
      client.execute();
      client.execute();
      client.execute();
      client.execute();
      client.execute();
   }
}


// 20:48:46.496 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy - 프록시 호출
// 20:48:46.499 [Test worker] INFO hello.proxy.pureproxy.proxy.code.RealSubject - 실제 객체 호출

// 20:48:47.513 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy - 프록시 호출
// 20:48:47.513 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy - 프록시 호출
// 20:48:47.513 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy - 프록시 호출
// 20:48:47.513 [Test worker] INFO hello.proxy.pureproxy.proxy.code.CacheProxy - 프록시 호출
```

첫 두줄은 캐싱되지 않은 첫 번째 호출이고, <br>
아래 나머지 부분들은 캐싱된 결과를 반환하는 부분이다. <br>
시간을 한번 보자 두번째 호출 부터 마지막까지는 밀리초 까지 같다. <br>
**거의 즉시 호출된 것이다! 캐싱 효과 확실하군**
