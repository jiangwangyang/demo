package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.web.exception.BusinessExceptionHandler;
import com.github.jiangwangyang.web.exception.ExceptionController;
import com.github.jiangwangyang.web.record.RecordAspect;
import com.github.jiangwangyang.web.response.ExtraResponseBodyAdvice;
import com.github.jiangwangyang.web.util.ObjectMapperUtil;
import com.github.jiangwangyang.web.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAspectJAutoProxy
@Import({
        RecordAspect.class,
        ExceptionController.class,
        BusinessExceptionHandler.class,
        ExtraResponseBodyAdvice.class,
        ObjectMapperUtil.class,
        SpringContextUtil.class
})
public class ImportConfig {
}
