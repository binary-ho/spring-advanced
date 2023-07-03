package hello.proxy.advisor;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

@Slf4j
public class MyPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MyMethodMatcher();
    }

    static class MyMethodMatcher implements MethodMatcher {

        private final String MATCH_METHOD_NAME = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {

            boolean result = method.getName().equals(MATCH_METHOD_NAME);

            log.info("포인트컷 호출 method = {}, targetClass = {}", method.getName(), targetClass);
            log.info("포인트컷 결과 result = {}", result);

            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        // isRuntime이 true면 얘가 호출된다.
        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }
}
