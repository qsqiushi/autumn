package com.autumn.passport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: autumn
 * @description: 测试controller
 * @author: qius
 * @create: 2021-03-03:11:39
 */
@RestController
@RequestMapping("/api/autumn/test")
public class TestController {

  @GetMapping(value = "/test1")
  public String test() {
    return "success";
  }
}
