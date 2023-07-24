package hello.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AopTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void AOP_적용이_안_되었다() {
        assertAll(
            () -> assertThat(AopUtils.isAopProxy(orderService)).isEqualTo(false),
            () -> assertThat(AopUtils.isAopProxy(orderRepository)).isEqualTo(false)
        );
    }

    @Test
    void ex_넣으면_excepion_발생() {
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
            .isInstanceOf(IllegalStateException.class);
    }
}
