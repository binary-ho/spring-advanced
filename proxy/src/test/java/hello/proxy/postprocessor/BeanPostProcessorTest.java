package hello.proxy.postprocessor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanPostProcessorTest {

    @Test
    void basicConfig() {
        ApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);

        // beanA 이름으로 B 객체가 빈으로 등록된다.
        // 바꿔치기
        ClassB classB = applicationContext.getBean("beanA", ClassB.class);
        classB.helloB();

        // A는 빈으로 등록되지 않는다.
        assertAll(
            () -> assertDoesNotThrow(classB::helloB),
            () -> assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(ClassA.class))
        );
    }

    @Slf4j
    static class AToBPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
            log.info("beanName = {} bean = {}", beanName, bean);

            // MEMO : Class A의 빈인 경우 ClassB로 바꿔치기!!
            if (bean instanceof ClassA) {
                return new ClassB();
            }
            return bean;
        }
    }

    @Slf4j
    @Configuration
    static class BeanPostProcessorConfig {

        // A를 등록
        @Bean(name = "beanA")
        public ClassA classA() {
            return new ClassA();
        }

        // 빈을 등록할 때 넣어준다?
        @Bean
        public AToBPostProcessor helloPostProcessor() {
            return new AToBPostProcessor();
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
