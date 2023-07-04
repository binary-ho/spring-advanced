package hello.proxy.config.v5_autoproxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {


    /*
    * 따로 프로세서 없이 바로 어드바이저를 등록해도 되는 이유
    *
    * 빌드 파일에 추가된 아래 한 줄..
	* implementation 'org.springframework.boot:spring-boot-starter-aop'
	*
	* 이 한줄만으로 AnnotationAwareAspectJAutoProxyCreator라는 빈 후처리기가 스프링 빈에 자동으로 등록됩니다.
	* 이름 그대로 자동으로 프록시를 생성해주는 빈 후처리기입니다. (-> AutoProxyCreator)
	* 이 빈 후처리기가 스프링 빈으로 등록된 Advisor들을 자동으로 찾아 프록시가 필요한 곳에 자동으로 적용해줍니다.
	* Adivsor 안에 Pointcut과 Advice가 이미 모두 포함되어 있다.
	* Advisor만 알고 있으면, 그 안의 Pointcut으로 어떤 스프링 빈에 프록시를 저용해야 할지 알 수 있다.
    * */

    // 이름으로 구분하는 기본적인 포인트컷컷 방식    @Bean
    public Advisor advisor1(LogTrace logTrace) {
        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");

        //advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /* 좀 더 정밀하게 구분하는 방식
    *  hello.proxy.app 패키지와 그 모든 하위 패키지가 포인트컷의 적용 대상이 된다.
    * 물론 이 버전은 no-log가 포함된다.
    *  */
    @Bean
    public Advisor advisor2(LogTrace logTrace) {
        //pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* hello.proxy.app..*(..))");
        //advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /*
     * no-log 메서드를 제외한 방식식     *  */
//    @Bean
    public Advisor advisor3(LogTrace logTrace) {
        //pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");
        //advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

}
