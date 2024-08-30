package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.kob.backend.consumer.WebSocketServer.users;

public class Game extends Thread {
    //  行
    private final Integer rows;
    //  列
    private final Integer cols;
    //  障碍物
    private final Integer inner_walls_count;
    //  地图
    @Getter
    private int[][] g;

    private  static final int[] dx = {-1, 0, 1, 0};
    private  static final int[] dy = {0, 1, 0, -1};

    @Getter
    private final Player playerA, playerB;

    //  存储两名玩家的下一步操作
    private Integer nextStepA = null;
    private Integer nextStepB = null;

    //  加锁
    private ReentrantLock lock = new ReentrantLock();

    //  游戏状态
    private String status = "playing"; // playing → finished
    private String loser = "";  //  all: 平局   A: A输   B: B输
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";
    //  初始化
    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1;
        String botCodeA = "";
        if ( botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        Integer botIdB = -1;
        String botCodeB = "";
        if ( botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        } else {
            botIdB = -2;
             botCodeB = "package com.kob.botrunningsystem.utils;\n" +
                     "\n" +
                     "import java.io.File;\n" +
                     "import java.io.FileNotFoundException;\n" +
                     "import java.util.ArrayList;\n" +
                     "import java.util.List;\n" +
                     "import java.util.Random;\n" +
                     "import java.util.Scanner;\n" +
                     "\n" +
                     "public class Bot implements java.util.function.Supplier<Integer> {\n" +
                     "    static class Cell {\n" +
                     "        public int x, y;\n" +
                     "        public Cell(int x, int y) {\n" +
                     "            this.x = x;\n" +
                     "            this.y = y;\n" +
                     "        }\n" +
                     "    }\n" +
                     "\n" +
                     "    //  检验蛇身是否变长\n" +
                     "    private boolean check_tail_increasing(int step) {\n" +
                     "        if (step <= 10) return true;\n" +
                     "        return step % 3 == 1;\n" +
                     "    }\n" +
                     "\n" +
                     "    //  拿取蛇身→从头开始一步步走，利用之前存的steps数组\n" +
                     "    public List<Cell> getCells(int sx, int sy, String steps) {\n" +
                     "        System.out.println(\"steps: \" + steps);\n" +
                     "        steps = steps.substring(1, steps.length() - 1); // 去掉括号\n" +
                     "        List<Cell> res = new ArrayList<>();\n" +
                     "\n" +
                     "        int[] dx = {-1, 0, 1, 0},dy = {0, 1, 0, -1};\n" +
                     "\n" +
                     "        int x = sx,y = sy;\n" +
                     "\n" +
                     "        int step = 0; // 记录步数\n" +
                     "        res.add(new Cell(x, y));\n" +
                     "        //  模拟出之前的每一步\n" +
                     "        for  (int i = 0; i < steps.length(); i++) {\n" +
                     "            int d = steps.charAt(i) - '0'; //   转化为整数\n" +
                     "            x += dx[d];\n" +
                     "            y += dy[d];\n" +
                     "            res.add(new Cell(x, y));\n" +
                     "            if (!check_tail_increasing(++step)) {\n" +
                     "                //  蛇尾不增加,删除蛇尾\n" +
                     "                res.remove(0);\n" +
                     "            }\n" +
                     "        }\n" +
                     "\n" +
                     "        return res;\n" +
                     "    }\n" +
                     "\n" +
                     "\n" +
                     "    public Integer nextMove(String input) {\n" +
                     "        //  0:上 1:右 2:下 3:左\n" +
                     "        String[] strs = input.split(\"#\");\n" +
                     "        int[][] g = new int[13][14];\n" +
                     "        for (int i = 0, k = 0; i < 13; i++) {\n" +
                     "            for (int j = 0; j < 14; j++, k++) {\n" +
                     "                if (strs[0].charAt(k) == '1') {\n" +
                     "                    g[i][j] = 1;\n" +
                     "                }\n" +
                     "            }\n" +
                     "        }\n" +
                     "\n" +
                     "        int aSx = Integer.parseInt(strs[1]);\n" +
                     "        int aSy = Integer.parseInt(strs[2]);\n" +
                     "        int bSx = Integer.parseInt(strs[4]);\n" +
                     "        int bSy = Integer.parseInt(strs[5]);\n" +
                     "        System.out.println(\"aSx: \" + aSx);" +
                     "        System.out.println(\"aSy: \" + aSy);" +
                     "\n" +
                     "        List<Cell> aCells = getCells(aSx, aSy, strs[3]);\n" +
                     "        List<Cell> bCells = getCells(bSx, bSy, strs[6]);\n" +
                     "\n" +
                     "        for (Cell cell : aCells)  g[cell.x][cell.y] = 1;\n" +
                     "        for (Cell cell : bCells)  g[cell.x][cell.y] = 1;\n" +
                     "\n" +
                     "        int[] dx = {-1, 0, 1, 0},dy = {0, 1, 0, -1};\n" +
                     "\n" +
                     "        //  版本4：版本3+随机数优化版\n" +
                     "        Random random = new Random();\n" +
                     "        for(int i = 0; i < 20; i ++) {\n" +
                     "            int d = random.nextInt(4);\n" +
                     "            int x1 = aCells.get(aCells.size() - 1).x + dx[d];\n" +
                     "            int y1 = aCells.get(aCells.size() - 1).y + dy[d];\n" +
                     "            if(x1 >= 0 && x1 < 13 && y1 >= 0 && y1 < 14 && g[x1][y1] == 0) {\n" +
                     "                for (int j = 0; j < 4; j ++){\n" +
                     "                    int x2 = x1 + dx[j];\n" +
                     "                    int y2 = y1 + dy[j];\n" +
                     "                    if (x2 >= 0 && x2 < 13 && y2 >= 0 && y2 < 14 && g[x2][y2] == 0) {\n" +
                     "                        for (int k = 0; k < 4; k ++){\n" +
                     "                            int x3 = x2 + dx[k];\n" +
                     "                            int y3 = y2 + dy[k];\n" +
                     "                            if (x3 >= 0 && x3 < 13 && y3 >= 0 && y3 < 14 && g[x3][y3] == 0) {\n" +
                     "                                return d;\n" +
                     "                            }\n" +
                     "                        }\n" +
                     "                    }\n" +
                     "                }\n" +
                     "            }\n" +
                     "        }\n" +
                     "\n" +
                     "\n" +
                     "        return 0;\n" +
                     "    }\n" +
                     "\n" +
                     "    @Override\n" +
                     "    public Integer get() {\n" +
                     "        File file = new File(\"input.txt\");\n" +
                     "        try {\n" +
                     "            Scanner scanner = new Scanner(file);\n" +
                     "            return nextMove(scanner.next());\n" +
                     "        } catch (FileNotFoundException e) {\n" +
                     "            throw new RuntimeException(e);\n" +
                     "        }\n" +
                     "    }\n" +
                     "}\n";
        }

        //  初始化两名玩家
        playerA = new Player(idA, botIdA,botCodeA,rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB,botCodeB,1, cols - 2, new ArrayList<>());
    }

    //  获取用户下一步操作
    public void setNextStepA(Integer nextStepA) {
        System.out.println("setNextStepA："+nextStepA);
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        System.out.println("setNextStepB："+nextStepB);
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }

    }

    //  判断连通性
    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i];
            int y = sy + dy[i];
            if (x >= 0 && x < rows && y >= 0 && y < cols && g[x][y] == 0) {
                if(check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }

            }
        }

        g[sx][sy] = 0;

        return false;
    }


    //  画地图
    private boolean draw() {
        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                g[i][j] = 0;
            }
        }


        //  先画围墙
        for (int r = 0; r < rows; r++) {
            g[r][0] = g[r][cols-1] = 1;
        }
        for (int c = 1; c < cols; c++) {
            g[0][c] = g[rows-1][c] = 1;
        }

        //  随机类,方便生成障碍物
        Random random = new Random();

        //  随机生成障碍物
        for (int i = 0; i < inner_walls_count / 2; i++) {
            for (int j = 0; j < 1000; j ++ ) {
                int r = random.nextInt(rows);
                int c = random.nextInt(cols);

                //  画过了
                if (g[r][c] == 1 || g[rows - 1 - r][cols - 1 - c] == 1) {
                    continue;
                }

                //  画在了围墙上
                if (r == rows - 2 && c == 1 || r == 1 && c == cols - 2) {
                    continue;
                }

                g[r][c] = g[rows - 1 - r][cols - 1 - c] = 1;
                break;
            }
        }


        return check_connectivity(rows - 2, 1, 1, cols - 2);
    }

    //
    public void createMap() {
        //  随机1000次
        for (int i = 0; i < 1000; i++) {
            if (draw())
                break;

        }
    }

    //  当前局面，把当前的局面信息编码成字符串
    //  地图#me.sx#me.sy#(me.steps)#you.sx#you.sy#(you.steps)
    private String getInput(Player player) {

        //  确定两名玩家
        Player me,you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }
        System.out.println("mebotId:"+me.getBotId()+" youbotId:"+you.getBotId());
        System.out.println("mebotId:"+me.getBotId()+" youbotId:"+you.getBotId());
        System.out.println("mebotId:"+me.getBotId()+" youbotId:"+you.getBotId());

        return getMapString() + "#" +
                me.getSx() + "#"  +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";

    }

    //  向微服务发送Bot代码,如果是人就不理了，如果不是人就执行Bot代码并绑定到读取输入中
    private void sendBotCode(Player player) {

        System.out.println(+player.getBotId());
        //  如果是人
        if (player.getBotId().equals(-1)) return;

        //  如果是机器人
        MultiValueMap <String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",player.getId().toString());
        data.add("bot_code",player.getBotCode());
        data.add("input", getInput(player)); //   获取当前局面
        System.out.println(player.getId() + " " + "sendBotCode:" + player.getBotCode() + " " + getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    //  获取两名玩家的下一步操作
    private boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("nextStep()");
        sendBotCode(playerA);
        sendBotCode(playerB);

        //  判断5次,每次等到1秒,最多等待5秒
        for(int i = 0; i < 50; i ++ ) {
            try {
                Thread.sleep(100); //  等待0.1秒
                lock.lock();
                try {
                    if (playerB.getId() < 0 && nextStepB == null) {

                    }
                    if (nextStepA != null && nextStepB != null) {
                        //  两名玩家的下一步操作均读到了

                        System.out.println("nextStepA: " + nextStepA);
                        System.out.println("nextStepB: " + nextStepB);
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    //  辅助函数：判断某一步是否合法
    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n-1);

        //  最后一步撞墙
        if(g[cell.x][cell.y] == 1) {
            System.out.println("撞墙");
            return false;
        }

        // 判断两蛇是否相撞
        //  自己撞自己
        for (int i = 0; i < n - 1; i++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) {
                System.out.println("撞自己");
                return false;
            }
        }
        //  自己撞对方
        for (int i = 0; i < n - 1; i++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) {
                System.out.println("撞对方");
                return false;
            }
        }

        return true;
    }

    //  判断两条蛇下一步是否合法
    private void judge() {
        //  先把两条蛇取出来
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        //  开始判断
        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished"; // 游戏结束

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }

        }

    }

    //  向每一名玩家广播信息
    private void sendAllMessage(String message) {
        if (users.get(playerA.getId()) != null) {
            users.get(playerA.getId()).sendMessage(message);
        }
        if (playerB.getId() > 0 && users.get(playerB.getId()) != null) {
            users.get(playerB.getId()).sendMessage(message);
        }
    }

    //  向两名玩家传播信息
    private void sendMove() {
        lock.lock();

        try {
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);
            sendAllMessage(resp.toJSONString());
            //  清空两名玩家的移动
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }

    }

    //  把g转换为字符串
    private String getMapString() {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < rows; i ++) {
            for(int j = 0; j < cols; j ++) {
                res.append(g[i][j]);
            }
        }

        return res.toString();
    }

    //  更新rating
    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }


    //  将结果存进数据库里
    private void saveToDatabase() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();

        Integer ratingB = 1500;
        if (playerB.getId() > 0) {
            ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
        }


        if ("A".equals(loser)) {
            ratingA -= 2;
            if (playerA.getId() > 0) {
                ratingB += 5;
            }
        } else if ("B".equals(loser)) {
            if (playerB.getId() > 0) {
                ratingB -= 2;
            }
            ratingA += 5;
        } else {
            ratingA += 3;
            if (playerA.getId() > 0) {
                ratingB += 3;
            }
        }

        updateUserRating(playerA, ratingA);
        if (playerB.getId() > 0) {
            updateUserRating(playerB, ratingB);
        }

        Record record= new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    //  向两名玩家公布结果
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event","result");
        resp.put("loser",loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    //  新线程的入口函数
    @Override
    public void run() {
        //  最多可以走600步
        for (int i = 0; i < 1000; i ++ ) {
            // 第一回合要先等待前端加载的2秒钟，否则第一回合玩家只要3秒不输入就死了
            if (i == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (nextStep()) {
                //  是否获取到了两条蛇的下一步操作
                judge();

                if (status.equals("playing")) {
                    //  游戏没有结束,把操作同步到前端
                    sendMove();
                } else {
                    //  游戏结束,把结果同步到前端
                    sendResult();
                    break;
                }
            } else {
                //  没获取到,游戏结束
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }

                //  游戏结束
                sendResult();
                break;
            }
        }
    }
}
