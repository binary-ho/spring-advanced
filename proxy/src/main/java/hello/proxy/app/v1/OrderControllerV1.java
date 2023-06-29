package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// MEMO : 스프링은 @Controller 또는 @RequestMapping 이 있어야 스프링 컨트롤러로 인식된다.
@RequestMapping
@ResponseBody
public interface OrderControllerV1 {

    // MEMO : request string `http://localhost:8080/v1/request?itemId=ex`
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);
    // MEMO : 인터페이스 컨트롤러에선 RequestParam 을 달아주지 않으면,
    //  컴파일 타임에 확실히 인식되지 않을 때가 있다.

    @GetMapping("/v1/no-log")
    String noLog();
}
