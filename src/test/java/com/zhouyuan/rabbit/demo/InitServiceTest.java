package com.zhouyuan.rabbit.demo;

import com.zhouyuan.rabbit.demo.service.InitService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InitServiceTest extends DemoApplicationTests {
    @Autowired
    InitService initService;
    @Test
    /**
     * 似乎不能用单元测试来测多线程访问数据库的这种情况
     */
    public void threadStartTest(){
        initService.generateMultiThread();
    }
}
