package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveSeriviceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receiveBotMove " + userId + " " + direction);

        //  把操作传向nextstep
        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;

            if (game != null) {
                //  判断是哪名玩家,设定其方向
                System.out.println("move");
                if (game.getPlayerA().getId().equals(userId)) {
                        game.setNextStepA(direction);
                }   else if (game.getPlayerB().getId().equals(userId)) {
                        game.setNextStepB(direction);
                }
            }
        }

        return "receive bot move from " + userId + " to " + direction + " success";
    }
}
