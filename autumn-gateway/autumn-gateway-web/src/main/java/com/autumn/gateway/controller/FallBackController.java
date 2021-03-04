package com.autumn.gateway.controller;

import com.autumn.model.enums.ResultCode;
import com.autumn.model.web.BaseResponse;
import com.autumn.model.web.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: dataearth-cloud-dev
 * @description: 熔断处理
 * @author: qius
 * @create: 2019-03-06 10:57
 **/

@Slf4j
@RestController
public class FallBackController {

    @GetMapping("/fallback")
    public BaseResponse fallback() {
        log.error("熔断异常");
        return ResponseBuilder.fail(ResultCode.SVR_INNER_ERROR.getCode(),"熔断fallback");
    }

}
