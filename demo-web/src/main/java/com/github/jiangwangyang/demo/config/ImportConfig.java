package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.web.exception.BusinessControllerAdvice;
import com.github.jiangwangyang.web.exception.ExceptionController;
import com.github.jiangwangyang.web.util.ObjectMapperUtil;
import com.github.jiangwangyang.web.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ExceptionController.class,
        BusinessControllerAdvice.class,
        ObjectMapperUtil.class,
        SpringContextUtil.class
})
public class ImportConfig {
}
