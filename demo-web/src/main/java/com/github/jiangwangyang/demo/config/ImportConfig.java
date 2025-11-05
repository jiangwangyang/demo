package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.web.exception.BusinessExceptionHandler;
import com.github.jiangwangyang.web.exception.ExceptionController;
import com.github.jiangwangyang.web.record.RecordAspect;
import com.github.jiangwangyang.web.response.ExtraResponseBodyAdvice;
import com.github.jiangwangyang.web.util.ObjectMapperUtil;
import com.github.jiangwangyang.web.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BusinessExceptionHandler.class,
        ExceptionController.class,
        RecordAspect.class,
        ExtraResponseBodyAdvice.class,
        ObjectMapperUtil.class,
        SpringContextUtil.class
})
public class ImportConfig {
}
