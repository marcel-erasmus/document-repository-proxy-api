package com.voidworks.drc.config.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    @ConditionalOnExpression(
            "${logging.request.filter.enabled:false}"
    )
    public CommonsRequestLoggingFilter loggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludeHeaders(false);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(10000);

        return loggingFilter;
    }

}