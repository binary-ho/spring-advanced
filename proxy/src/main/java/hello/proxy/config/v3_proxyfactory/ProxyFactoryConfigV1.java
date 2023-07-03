package hello.proxy.config.v3_proxyfactory;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.app.v1.OrderControllerV1Impl;
import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.app.v1.OrderRepositoryV1Impl;
import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.app.v1.OrderServiceV1Impl;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProxyFactoryConfigV1 {

    // 인터페이스 버전

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1Impl orderRepository = new OrderRepositoryV1Impl();

        ProxyFactory factory = new ProxyFactory(orderRepository);
        factory.addAdvisor(createAdvisor(logTrace));

        OrderRepositoryV1 repositoryProxy = (OrderRepositoryV1) factory.getProxy();
        log.info("ProxyFactory repositoryProxy = {}, target = {}", repositoryProxy.getClass(), orderRepository.getClass());
        return repositoryProxy;
    }

    private Advisor createAdvisor(LogTrace logTrace) {
        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");

        //advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        ProxyFactory factory = new ProxyFactory(orderService);
        factory.addAdvisor(createAdvisor(logTrace));
        OrderServiceV1 serviceProxy = (OrderServiceV1) factory.getProxy();
        log.info("ProxyFactory serviceProxy = {}, target = {}", serviceProxy.getClass(), orderService.getClass());
        return serviceProxy;
    }

    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1 orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));
        ProxyFactory factory = new ProxyFactory(orderController);
        factory.addAdvisor(createAdvisor(logTrace));
        OrderControllerV1 controllerProxy = (OrderControllerV1) factory.getProxy();
        log.info("ProxyFactory controllerProxy = {}, target = {}", controllerProxy.getClass(), orderController.getClass());
        return controllerProxy;
    }
}
