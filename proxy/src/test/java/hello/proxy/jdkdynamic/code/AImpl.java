package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AImpl implements AInterface {

    @Override
    public String call1() {
        log.info("A1 호출");
        return "a1";
    }

    @Override
    public String call2() {
        log.info("A2 호출");
        return "a2";
    }

    @Override
    public String call3() {
        log.info("A3 호출");
        return "a3";
    }
}
