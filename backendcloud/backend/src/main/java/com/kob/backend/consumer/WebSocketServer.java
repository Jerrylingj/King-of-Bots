package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    //   线程安全的哈希表
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    private User user;

    private Session session = null;

    public static UserMapper userMapper;
    //  利用BotId取出Bot
    private static BotMapper botMapper;
    public static RecordMapper recordMapper;

    public static RestTemplate restTemplate;
    public Game game = null;

    //  添加用户的Url
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper) { WebSocketServer.botMapper = botMapper; }
    @Autowired
    public void setUserMapper(UserMapper userMapper) { WebSocketServer.userMapper = userMapper; }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }
    

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接,在访问到当前链接时自动调用
        this.session = session;

        // 调试信息
        System.out.println("connetcted!");

        Integer userId = JwtAuthentication.getUserId(token);

        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            // 用户存在，连接成功
            users.put(userId, this);
        } else {
            this.session.close();
        }

        System.out.println(users);
    }

    @OnClose
    public void onClose() {
        // 关闭链接,在关闭当前链接时自动调用
        //  用户退出

        // 输出调试信息
        System.out.println("disconnetcted!");

        if(this.user != null) {
            users.remove(this.user.getId());
        }
    }

    //  把匹配逻辑封装成一个函数
    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);

        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB
        );
        game.createMap();

        //  把线程同步给AB
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).game = game;
        }
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).game = game;
        }
        game.start();   //  开始一个线程

        //  把地图信息封装到JSON里面
        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map",game.getG());

        //  a和b配对,把信息传给a,b
        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username",b.getUsername());
        respA.put("opponent_photo",b.getPhoto());
        respA.put("game",respGame);
        //  向前端发送用户a的信息
        if(users.get(a.getId()) != null) {
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game",respGame);
        //  向前端发送用户b的信息
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    //  开始匹配，向Matchingsystem发送请求，微服务把匹配信息传给Server之后再运行startGame
    private void startMatching(Integer botId) {
        System.out.println("startMatching" + botId);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id",botId.toString());
        //  利用restTemplate发送请求
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    //  结束匹配
    private void stopMatching() {
        System.out.println("stopMatching");

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());

        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {
        //  判断是哪名玩家,设定其方向
        //  如果玩家选择的是Bot操作，就必须屏蔽其输入
        System.out.println("move");
        if (game.getPlayerA().getId().equals(user.getId())) {
            if (game.getPlayerA().getBotId().equals(-1)) //人工操作
                game.setNextStepA(direction);
        }   else if (game.getPlayerB().getId().equals(user.getId())) {
            if (game.getPlayerB().getBotId().equals(-1)) //人工操作
                game.setNextStepB(direction);
        }
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息，在后端接收信息
        System.out.println("received message: " + message);

        //  解析信息
        JSONObject data = JSONObject.parseObject(message);

        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            //  交给开始匹配函数处理
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    //  后端向前端发送信息
    public void sendMessage(String message) {
        synchronized (this.session) {
            this.session.getAsyncRemote().sendText(message);
        }
    }
}