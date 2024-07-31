package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {

    @Autowired
    private  BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
//        插入新机器人
    //    先判断当前是哪个用户操作
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

    //  获取用户输入
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

    //  定义返回结果
        Map<String, String> map = new HashMap<>();

        //  接下来对输入进行判断
        if (title == null || title.isEmpty()) {
            map.put("error_message","标题不能为空！");
            return map;
        }

        if (title.length() > 100) {
            map.put("error_message","标题长度不能大于100！");
            return map;
        }

        if (description == null || description.isEmpty()) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 30) {
            map.put("error_message","Bot备注的长度不能大于30！");
            return map;
        }

        if (content == null || content.isEmpty()) {
            map.put("error_message","代码不能为空！");
            return map;
        }

        if (content.length() > 10000) {
            map.put("error_message","代码长度不能大于100000！");
            return map;
        }

        //  限制Bot总数量不能超过10
        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        if (botMapper.selectCount(queryWrapper) >= 10) {
            map.put("error_message", "每名用户最多只可以添加10个Bot!");
            return map;
        }

        Date now = new Date();
        Bot bot = new Bot(null, user.getId(),  title, description, content, now, now);

        botMapper.insert(bot);
        map.put("error_message","success");

        return map;
    }
}
