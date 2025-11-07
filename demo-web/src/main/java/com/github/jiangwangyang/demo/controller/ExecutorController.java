package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.web.record.Recordable;
import com.github.jiangwangyang.web.record.RequestRecordUtil;
import com.github.jiangwangyang.web.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/executor")
@Recordable
public class ExecutorController {

    @Autowired
    private ExecutorService executorService;

    @RequestMapping
    public Response<?> executor() {
        String result = RequestRecordUtil.recordSupplyAsync(
                () -> "executor",
                executorService
        ).join();
        return Response.success(result);
    }
}
