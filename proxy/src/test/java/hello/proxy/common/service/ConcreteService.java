package hello.proxy.common.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteService {

    // 인터페이스 없는 버전
    public void call() {
        log.info("ConcreteService 호출");
    }
}
