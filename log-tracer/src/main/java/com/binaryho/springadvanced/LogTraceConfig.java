package com.binaryho.springadvanced;

import com.binaryho.springadvanced.trace.logtrace.LogTrace;
import com.binaryho.springadvanced.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}
