package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {

//    注入接口
    @Autowired
    private RegisterService registerService;

    @PostMapping("/api/user/account/register/")
    public Map<String, String> register(@RequestParam Map<String, String> map) {
//        @RequestParam从前端获取参数map,然后传到后端
        String username = map.get("username");
        String password = map.get("password");
        String confirmPassword = map.get("confirmPassword");
        return registerService.register(username, password, confirmPassword);
    }


}
