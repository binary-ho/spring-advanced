package hello.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.order.aop.AspectV6Advice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
//@Import(AspectV1.class)
//@Import(AspectV2.class)
//@Import(AspectV3.class)
//@Import(AspectV4WithPointcuts.class)
//@Import({AspectV5Order.LogAspect.class, TransactionAspect.class})
@Import(AspectV6Advice.class)
public class AopTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void AOP_적용이_되었다() {
        assertAll(
            () -> assertThat(AopUtils.isAopProxy(orderService)).isEqualTo(true),
            () -> assertThat(AopUtils.isAopProxy(orderRepository)).isEqualTo(true)
        );
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
    }

    @Test
    void ex_넣으면_excepion_발생() {
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
            .isInstanceOf(IllegalStateException.class);
    }
}
