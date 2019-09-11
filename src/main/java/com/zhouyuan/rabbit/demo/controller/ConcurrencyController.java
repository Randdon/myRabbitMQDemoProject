package com.zhouyuan.rabbit.demo.controller;

import com.zhouyuan.rabbit.demo.service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class ConcurrencyController {
    @Autowired
    InitService initService;

    @RequestMapping(value = "rob")
    public String concurrencyRequest(){
        initService.generateMultiThread();
        return "success";
    }
}
