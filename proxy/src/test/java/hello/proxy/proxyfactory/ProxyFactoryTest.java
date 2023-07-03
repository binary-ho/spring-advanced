package hello.proxy.proxyfactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

@Slf4j
public class ProxyFactoryTest {

    @Test
    void ProxyFactory_는_인터페이스가_있으면_JDK_동적_프록시를_사용한다() {
        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass = {}", target.getClass());
        log.info("proxyClass = {}", proxy.getClass());

        assertAll(
            () -> assertThat(AopUtils.isAopProxy(proxy)).isTrue(),
            () -> assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue(),
            () -> assertThat(AopUtils.isCglibProxy(proxy)).isFalse()
        );

        proxy.save();
    }

    @Test
    void ProxyFactory_는_구체_클래스만_있으면_CGLIB를_사용한다() {
        ConcreteService target = new ConcreteService();

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());

        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();
        log.info("targetClass = {}", target.getClass());
        log.info("proxyClass = {}", proxy.getClass());

        proxy.call();

        assertAll(
            () -> assertThat(AopUtils.isAopProxy(proxy)).isTrue(),
            () -> assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse(),
            () -> assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
        );
    }

    @Test
    void ProxyTargetClass_옵션을_사용하면_인터페이스가_있어도_CGLIB와_클래스_기반_프록시를_사용한다() {
        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(target);

        // MEMO : 이 한줄로 CGLIB 강제
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass = {}", target.getClass());
        log.info("proxyClass = {}", proxy.getClass());

        assertAll(
            () -> assertThat(AopUtils.isAopProxy(proxy)).isTrue(),
            () -> assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse(),
            () -> assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
        );

        proxy.save();
    }
}
