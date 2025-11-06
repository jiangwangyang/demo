package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.web.record.RecordTask;
import com.github.jiangwangyang.web.response.Response;
import com.github.jiangwangyang.web.util.RequestExtraUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/executor")
public class ExecutorController {

    @Autowired
    private ExecutorService executorService;

    @RequestMapping
    public Response<?> executor() {
        String result = CompletableFuture.supplyAsync(RecordTask.ofSupplier(
                RequestExtraUtil.of().getExtraMap(),
                "executor",
                () -> "executor"
        ), executorService).join();
        return Response.success(result);
    }
}
