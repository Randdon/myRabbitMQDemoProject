package com.zhouyuan.rabbit.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class InitService {

    private static Integer MOBILE = 0;
    private static final int MAX_THREAD_NUM = 500;
    private static final Logger log = LoggerFactory.getLogger(InitService.class);
    @Autowired
    ConcurrencyService concurrencyService;
    public void generateMultiThread(){

        log.info("开始初始化线程数：----> ");

        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < MAX_THREAD_NUM; i++) {
            new Thread(new ThreadService(countDownLatch)).start();
        }
        //启动多个线程
        countDownLatch.countDown();
    }

    class ThreadService implements Runnable{

        private CountDownLatch latch;

        ThreadService(CountDownLatch latch){
            this.latch = latch;
        }
        @Override
        public void run() {
            try {
                latch.await();
                MOBILE += 1;
                concurrencyService.manageRobbing(String.valueOf(MOBILE));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //log.info("打印手机号：{}",MOBILE);
        }
    }
}
