package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        //  接收到Matchingsystem端的匹配信息
        System.out.println("start game: " + aId + " " + aBotId + " " + bId + " " + bBotId);
        //  把信息传回Client端
        WebSocketServer.startGame(aId, aBotId, bId, bBotId);
        return "start game success";
    }
}
