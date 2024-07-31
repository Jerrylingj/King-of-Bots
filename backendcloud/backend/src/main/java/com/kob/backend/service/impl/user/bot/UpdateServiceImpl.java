package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {

//        还是要判断用户是否登录
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
            map.put("error_message","代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("error_message","代码长度不能大于100000！");
            return map;
        }

        int bot_id = Integer.parseInt(data.get("bot_id"));

        Bot bot = botMapper.selectById(bot_id);

        if (bot == null) {
            map.put("error_message","Bot不存在或已被删除！");
            return map;
        }

        if(!bot.getUserId().equals(user.getId())) {
            map.put("error_message","没有权限修改该Bot！");
            return map;
        }

        Bot new_bot = new Bot(
                bot.getId(),
                user.getId(),
                title,
                description,
                content,
                bot.getCreatetime(),
                new Date()
        );

        botMapper.updateById(new_bot);

        map.put("error_message","update success");

        return map;
    }
}



