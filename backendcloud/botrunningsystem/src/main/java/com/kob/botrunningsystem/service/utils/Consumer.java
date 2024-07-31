package com.kob.botrunningsystem.service.utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread {
    private Bot bot;

    private static RestTemplate restTemplate;

    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout (long timeout, Bot bot) {
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); //  最多等待timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();   //  中断当前线程
        }


    }


    //  在code里类名添加uid
    private String addUid(String code, String uid) {
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        //  编译代码,类重名的只会编译一次，所以每个类都不能重名
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);

//        BotInterface botInterface = Reflect.compile(
//                "com.kob.botrunningsystem.utils.Bot" + uid,
//                addUid(bot.getBotCode(), uid)
//        ).create().get();
        //  换成更加普遍化的接口
        Supplier<Integer> botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        System.out.println("input "+bot.getInput());
        System.out.println("input "+bot.getInput());
        System.out.println("input "+bot.getInput());

        //  bot.getInput()-->执行bot代码得到dirction
        //  可以改变为操作文件，
//        Integer direction = botInterface.nextMove(bot.getInput());
        File file = new File ("input.txt");
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput());   // 输出内容
            fout.flush();                   // 清空缓存区
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Integer direction = botInterface.get();


        System.out.println("move-direction: " + bot.getUserId() + " " + direction);
        //  至此,代码已发送至BotPool

        //  返回到Backend
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject(receiveBotMoveUrl,data,String.class);
    }
}
