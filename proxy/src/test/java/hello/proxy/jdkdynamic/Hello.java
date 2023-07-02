package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Hello {
    public String callA() {
        log.info("call A~~");
        return "A";
    }
    public String callB() {
        log.info("call B!!");
        return "B";
    }
}
