package hello.proxy.pureproxy.decorator.code.v1;

import hello.proxy.pureproxy.decorator.code.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecorator implements Component {

    private Component component;

    public MessageDecorator(Component component) {
        this.component = component;
    }

    @Override
    public String operation() {

        // MEMO : 메시지에 별들 추가하기기 추가
       log.info("MessageDecorator 실행");
        String result = component.operation();
        String decoResult = "************************" + result + "************************";
        log.info("MessageDecorator 꾸미기 적용 전 = {}, 적용 후 = {}", result, decoResult);
        return decoResult;
    }
}
