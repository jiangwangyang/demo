package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.web.exception.BusinessExceptionHandler;
import com.github.jiangwangyang.web.exception.GlobalExceptionHandler;
import com.github.jiangwangyang.web.record.ControllerRecordAspect;
import com.github.jiangwangyang.web.record.RecordResponseBodyAdvice;
import com.github.jiangwangyang.web.util.ObjectMapperUtil;
import com.github.jiangwangyang.web.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BusinessExceptionHandler.class,
        GlobalExceptionHandler.class,
        ControllerRecordAspect.class,
        RecordResponseBodyAdvice.class,
        ObjectMapperUtil.class,
        SpringContextUtil.class
})
public class ImportConfig {
}
