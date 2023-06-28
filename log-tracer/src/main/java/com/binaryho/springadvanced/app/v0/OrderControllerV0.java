package com.binaryho.springadvanced.app.v0;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV0 {

    private final OrderServiceV0 orderService;

    // MEMO : request string `http://localhost:8080/v0/request?itemId=ex`
    @GetMapping("/v0/request")
    public ResponseEntity<String> request(String itemId) {
        orderService.orderItem(itemId);
        return ResponseEntity
            .ok()
            .body("ok");
    }
}
