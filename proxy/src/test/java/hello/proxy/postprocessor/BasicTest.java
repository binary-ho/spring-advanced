package hello.proxy.postprocessor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BasicTest {

    @Test
    void basicConfig() {
        ApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(BasicConfig.class);

        ClassA classA = applicationContext.getBean("beanA", ClassA.class);

        // B는 빈으로 등록되지 않는다
        assertAll(
            () -> assertDoesNotThrow(classA::helloA),
            () -> assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(ClassB.class))
        );
    }

    @Slf4j
    @Configuration
    static class BasicConfig {

        @Bean(name = "beanA")
        public ClassA classA() {
            return new ClassA();
        }
    }

    @Slf4j
    static class ClassA {

        public void helloA() {
            log.info("hello A");
        }
    }

    @Slf4j
    static class ClassB {

        public void helloB() {
            log.info("hello B");
        }
    }
}
