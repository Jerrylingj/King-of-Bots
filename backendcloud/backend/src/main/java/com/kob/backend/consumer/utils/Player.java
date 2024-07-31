package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;  // -1表示自己亲自出马，否则表示用AI
    private String botCode;
    private Integer sx;
    private Integer sy;
    //  存储历史路径
    private List<Integer> steps;

    //  检验蛇身是否变长
    private boolean check_tail_increasing(int step) {
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    //  拿取蛇身→从头开始一步步走，利用之前存的steps数组
    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0},dy = {0, 1, 0, -1};

        int x = sx,y = sy;

        int step = 0; // 记录步数
        res.add(new Cell(x, y));
        //  模拟出之前的每一步
        for  (int d: steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                //  蛇尾不增加,删除蛇尾
                res.remove(0);
            }
        }

        return res;
    }

    //  将steps转化为字符串
    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d:steps) {
            res.append(d);
        }

        return res.toString();
    }
}
