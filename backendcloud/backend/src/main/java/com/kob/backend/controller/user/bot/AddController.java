package com.kob.backend.controller.user.bot;

import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddController {

//    连接后端
    @Autowired
    private AddService addService;

//    获取前端输入
    @PostMapping("/api/user/bot/add/")
    public Map<String, String> add(@RequestParam Map<String, String> data) {
    //  将前端输入放到后端函数中，再把结果返回给前端
        return addService.add(data);
    }
}
