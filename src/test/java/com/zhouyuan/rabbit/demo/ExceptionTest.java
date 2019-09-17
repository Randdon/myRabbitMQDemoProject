package com.zhouyuan.rabbit.demo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ExceptionTest extends DemoApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(ExceptionTest.class);

    @Test
    public void exceptionPrintTest() {
        try {

            int i = 10;
            log.debug("{}被零整除会报异常", i);
            i = i / 0;
        } catch (Exception e) {
            log.error("异常打印到日志：", e);
            log.error("e.fillInStackTrace方法测试：", e.fillInStackTrace());
        }

    }

    @Test
    public void logTest() {
        Map<String, String> param = new HashMap<>(3);
        param.put("TradeType", "GetMemberInfo");
        log.info("log直接打印map测试：param：{}", param);
    }
}
