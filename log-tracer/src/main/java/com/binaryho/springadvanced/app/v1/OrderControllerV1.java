package com.binaryho.springadvanced.app.v1;

import com.binaryho.springadvanced.trace.TraceStatus;
import com.binaryho.springadvanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderService;
    private final HelloTraceV1 trace;

    // MEMO : request string http://localhost:8080/v1/request?itemId=ex
    @GetMapping("/v1/request")
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderController.request()");
            orderService.orderItem(itemId);
            trace.end(status);
            return "ok";
        } catch (Exception e) {
            trace.exception(status, e);

            // MEMO : 예외를 다시 던져줘야 예외 확인 가능
           throw e;
        }
    }
}
