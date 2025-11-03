package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.web.response.Response;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping
    public Response<VO> demo(@RequestBody @Valid DTO dto) {
        return Response.success(new VO(dto.query));
    }

    public record DTO(
            // 查询参数
            @NotEmpty
            String query
    ) {
    }

    public record VO(
            // 响应数据
            String data
    ) {
    }
}
