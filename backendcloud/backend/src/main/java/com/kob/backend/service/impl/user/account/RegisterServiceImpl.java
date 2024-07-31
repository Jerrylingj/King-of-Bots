package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {
        Map<String, String> map = new HashMap<>();

//        先判断所有不合法情况
        if(username ==null)
        {
            map.put("error_message","用户名不能为空！");
            return map;
        }

        if(password == null || confirmPassword == null)
        {
            map.put("error_message","密码不能为空！");
            return map;
        }

//        删掉前面的空格
        username = username.trim();
        if (username.isEmpty()) {
            map.put("error_message","用户名不能为空！");
            return map;
        }

        if(password.isEmpty() || confirmPassword.isEmpty()) {
            map.put("error_message","密码不能为空！");
            return map;
        }

        if (username.length() > 100) {
            map.put("error_message","用户名长度不能大于100！");
            return map;
        }

        if(password.length() > 100 || confirmPassword.length() > 100) {
            map.put("error_message","密码长度不能大于100!");
            return map;
        }

        if(!password.equals(confirmPassword)) {
            map.put("error_message","两次密码输入不一致，请重新输入！");
            return map;
        }

    //  判断用户是否已经存在
        // 先将输入的用户名封装成类，相当于强制类型转换
        QueryWrapper<User>    queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            map.put("error_message","用户名已存在！");
            return map;
        }

//        用户名密码合法，给密码加密后导入数据库
        String encodePassword = passwordEncoder.encode(password);

        Random random = new Random();
        int t = random.nextInt(3);
        String photo;
        if (t == 0 ) {
            photo = "https://assets.codepen.io/1480814/archer.jpg";
        } else if (t == 1){
            photo = "https://assets.codepen.io/1480814/saber.jpg";
        } else {
            photo = "https://ts1.cn.mm.bing.net/th/id/R-C.c256dbecea080677593c4236b699020a?rik=qK71Iap4gmYNHA&riu=http%3a%2f%2fwww.imanami.com%2fwp-content%2fuploads%2f2016%2f03%2funknown-user.jpg&ehk=K9lZs3cX1UTchIGU%2baD4jf5rQeIyn41WGbDE9Ggxkm4%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1";
        }

        User user = new User(null,username,encodePassword,photo,1500);
        userMapper.insert(user);

        map.put("error_message","success");
        return map;

    }

}
