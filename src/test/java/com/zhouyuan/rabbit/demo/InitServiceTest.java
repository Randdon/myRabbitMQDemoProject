package com.zhouyuan.rabbit.demo;

import com.zhouyuan.rabbit.demo.service.InitService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InitServiceTest extends DemoApplicationTests {
    @Autowired
    InitService initService;
    @Test
    public void threadStartTest(){
        initService.generateMultiThread();
    }
}
