package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Bot implements java.util.function.Supplier<Integer> {

    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // ==============================
    // 保持原有函数: check_tail_increasing
    // ==============================
    private boolean check_tail_increasing(int step) {
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    // ==============================
    // 保持原有函数: getCells
    // ==============================
    public List<Cell> getCells(int sx, int sy, String steps) {
        System.out.println("steps: " + steps);
        steps = steps.substring(1, steps.length() - 1); // 去掉括号
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));

        for (int i = 0; i < steps.length(); i++) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                res.remove(0);
            }
        }
        return res;
    }

    // ==============================
    // 以下是你原本就有的评估和 BFS 函数
    // 保持原样: evaluateDirection, calculateSafeSpace, calculateEnemySpace
    // ==============================
    private double evaluateDirection(int[][] g, int x, int y,
                                     List<Cell> aCells, List<Cell> bCells,
                                     int[] dx, int[] dy) {
        double score = 0;

        // 1. 安全空间
        int safeSpace = calculateSafeSpace(g, x, y, dx, dy);
        score += safeSpace * 0.6;

        // 2. 压制对手
        int enemySpace = calculateEnemySpace(g,
                bCells.get(bCells.size() - 1).x,
                bCells.get(bCells.size() - 1).y, dx, dy);
        score += (100 - enemySpace) * 0.4;

        // 3. 距离对手
        int enemyHeadX = bCells.get(bCells.size() - 1).x;
        int enemyHeadY = bCells.get(bCells.size() - 1).y;
        double distanceToEnemy = Math.sqrt(Math.pow(x - enemyHeadX, 2)
                + Math.pow(y - enemyHeadY, 2));
        score += (14 - distanceToEnemy) * 0.2;

        return score;
    }

    private int calculateSafeSpace(int[][] g, int x, int y,
                                   int[] dx, int[] dy) {
        boolean[][] visited = new boolean[13][14];
        Queue<Cell> queue = new LinkedList<>();
        if (inRange(x, y) && g[x][y] == 0) {
            visited[x][y] = true;
            queue.add(new Cell(x, y));
        }
        int space = 0;

        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            space++;

            for (int d = 0; d < 4; d++) {
                int nx = cell.x + dx[d];
                int ny = cell.y + dy[d];
                if (inRange(nx, ny) && g[nx][ny] == 0 && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    queue.add(new Cell(nx, ny));
                }
            }
        }
        return space;
    }

    private int calculateEnemySpace(int[][] g, int x, int y,
                                    int[] dx, int[] dy) {
        boolean[][] visited = new boolean[13][14];
        Queue<Cell> queue = new LinkedList<>();
        if (inRange(x,y) && g[x][y] == 0) {
            visited[x][y] = true;
            queue.add(new Cell(x, y));
        }
        int space = 0;

        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            space++;

            for (int d = 0; d < 4; d++) {
                int nx = cell.x + dx[d];
                int ny = cell.y + dy[d];
                if (inRange(nx, ny) && g[nx][ny] == 0 && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    queue.add(new Cell(nx, ny));
                }
            }
        }
        return space;
    }

    // ==============================
    // 新增辅助函数 #1: simulateMove
    // 用于“模拟某条蛇移动一步”得到新的蛇身 (不修改原蛇身)
    // ==============================
    private List<Cell> simulateMove(int[][] grid,
                                    List<Cell> oldCells,
                                    int direction,
                                    int stepsCount) {
        // 复制蛇身
        List<Cell> newCells = new ArrayList<>(oldCells);

        // 方向映射
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

        // 找到蛇头
        Cell head = newCells.get(newCells.size() - 1);
        int nx = head.x + dx[direction], ny = head.y + dy[direction];

        // 加入新蛇头
        newCells.add(new Cell(nx, ny));

        // 判断尾巴是否增长
        if (!check_tail_increasing(stepsCount + 1)) {
            // 不增长则移除尾巴
            newCells.remove(0);
        }
        return newCells;
    }

    // ==============================
    // 新增辅助函数 #2: occupySnake
    // 用于在模拟后，“先清理旧蛇身，再标记新蛇身”到一份grid上
    // ==============================
    private void occupySnake(int[][] grid,
                             List<Cell> oldA, List<Cell> oldB,
                             List<Cell> newA, List<Cell> newB) {
        // 先把旧 A、B 的身体在 grid 上清 0
        for (Cell c : oldA) {
            if (inRange(c.x, c.y)) {
                grid[c.x][c.y] = 0;
            }
        }
        for (Cell c : oldB) {
            if (inRange(c.x, c.y)) {
                grid[c.x][c.y] = 0;
            }
        }
        // 再把 new A、B 的身体设为 1
        for (Cell c : newA) {
            if (inRange(c.x, c.y)) {
                grid[c.x][c.y] = 1;
            }
        }
        for (Cell c : newB) {
            if (inRange(c.x, c.y)) {
                grid[c.x][c.y] = 1;
            }
        }
    }

    // ==============================
    // 新增辅助函数 #3: copyGrid
    // ==============================
    private int[][] copyGrid(int[][] g) {
        int[][] newG = new int[13][14];
        for (int i = 0; i < 13; i++) {
            System.arraycopy(g[i], 0, newG[i], 0, 14);
        }
        return newG;
    }

    // ==============================
    // 新增辅助函数 #4: inRange
    // ==============================
    private boolean inRange(int x, int y) {
        return (x >= 0 && x < 13 && y >= 0 && y < 14);
    }

    // ==============================
    // 新增辅助函数 #5: evaluateBoard
    // 在“两层搜索”的终局使用的综合评估
    // (可与 evaluateDirection 类似，但我们在搜索终局做更灵活的加权)
    // ==============================
    private double evaluateBoard(int[][] grid,
                                 List<Cell> aCells,
                                 List<Cell> bCells,
                                 int totalSteps) {
        // 这里示范用“动态权重”思路：
        // 前期更注重安全空间，后期更注重进攻/压制
        double progress = calculateProgress(totalSteps);
        double safeW     = (1 - progress) * 0.8 + progress * 0.3;
        double restrictW = (1 - progress) * 0.2 + progress * 0.6;
        double approachW = (1 - progress) * 0.0 + progress * 0.4;

        // 先把A,B身体覆盖到 tempGrid 上
        int[][] tempGrid = copyGrid(grid);
        for (Cell c : aCells) {
            if (inRange(c.x, c.y)) {
                tempGrid[c.x][c.y] = 1;
            }
        }
        for (Cell c : bCells) {
            if (inRange(c.x, c.y)) {
                tempGrid[c.x][c.y] = 1;
            }
        }

        // 计算 A 的安全空间
        Cell aHead = aCells.get(aCells.size()-1);
        // 计算 B 的安全空间
        Cell bHead = bCells.get(bCells.size()-1);
        int[] DX = {-1, 0, 1, 0}, DY = {0, 1, 0, -1};

        int aSafe = calculateSafeSpace(tempGrid, aHead.x, aHead.y, DX, DY);
        int bSafe = calculateSafeSpace(tempGrid, bHead.x, bHead.y, DX, DY);

        // A,B 头距离
        double dist = Math.sqrt(Math.pow(aHead.x - bHead.x,2)
                + Math.pow(aHead.y - bHead.y,2));

        // 综合得分
        double score = 0.0;
        score += aSafe * safeW;
        score += (100 - bSafe) * restrictW;
        score += (14 - dist)   * approachW;

        return score;
    }

    // ==============================
    // 新增辅助函数 #6: calculateProgress
    // 你可以用 (totalSteps / 50.0) 来简单衡量前/后期
    // ==============================
    private double calculateProgress(int totalSteps) {
        double ratio = (double) totalSteps / 50.0;
        if (ratio > 1.0) ratio = 1.0;
        return ratio;
    }

    /**
     * ======================================================================================
     *  核心：nextMove — 在不改变原有函数的前提下，实现“两层搜索 + simulateMove + 动态评估”
     * ======================================================================================
     */
    public Integer nextMove(String input) {
        // 1) 解析输入
        String[] strs = input.split("#");
        int[][] g = new int[13][14];
        for (int i = 0, k = 0; i < 13; i++) {
            for (int j = 0; j < 14; j++, k++) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }
        int aSx = Integer.parseInt(strs[1]);
        int aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]);
        int bSy = Integer.parseInt(strs[5]);

        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);

        // 在 grid 上标记出 A,B 的身体
        for (Cell c : aCells) {
            g[c.x][c.y] = 1;
        }
        for (Cell c : bCells) {
            g[c.x][c.y] = 1;
        }

        // 2) 计算当前步数(仅用来做进度判断，非必须)
        int stepA = strs[3].length() - 2;
        int stepB = strs[6].length() - 2;
        if (stepA < 0) stepA = 0;
        if (stepB < 0) stepB = 0;
        int totalSteps = stepA + stepB;

        // 3) 进行简单的“两层搜索”：A走 -> B走 -> 评估
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        Cell aHead = aCells.get(aCells.size()-1);

        double bestScore = -1e9;
        int bestDir = -1;

        // 遍历我方四个方向
        for (int d = 0; d < 4; d++) {
            int nx = aHead.x + dx[d];
            int ny = aHead.y + dy[d];
            // 如果下一步撞墙/身体，则不可行
            if (!inRange(nx, ny) || g[nx][ny] == 1) {
                continue;
            }

            // =============== A先走一步，模拟到copyG1中 ===============
            int[][] copyG1 = copyGrid(g);
            List<Cell> newACells = simulateMove(copyG1, aCells, d, stepA);

            // 覆盖到copyG1(先去除旧A身、再加新A身)
            occupySnake(copyG1, aCells, bCells, newACells, bCells);

            // 对手B可能的最优反击(最坏情况)
            double worstResponse = 1e9;

            // 遍历B的4个走法
            Cell bHead = bCells.get(bCells.size()-1);
            for (int d2 = 0; d2 < 4; d2++) {
                int nbx = bHead.x + dx[d2];
                int nby = bHead.y + dy[d2];
                // 如果对手这样走撞墙，可以认为对手送死(对我们有利) => 设置一个低分
                // 或者直接跳过这步不算。
                // 这里先简单地给个极低分(对手不想撞墙)
                if (!inRange(nbx, nby) || copyG1[nbx][nby] == 1) {
                    worstResponse = Math.min(worstResponse, -999999);
                    continue;
                }

                int[][] copyG2 = copyGrid(copyG1);
                List<Cell> newBCells = simulateMove(copyG2, bCells, d2, stepB);
                // 再占位
                occupySnake(copyG2, bCells, bCells, newACells, newBCells);

                // 评估局面(终局)分数
                double finalScore = evaluateBoard(copyG2, newACells, newBCells, totalSteps+1);

                worstResponse = Math.min(worstResponse, finalScore);
            }

            // 我方选使 worstResponse 最大化的方向
            if (worstResponse > bestScore) {
                bestScore = worstResponse;
                bestDir = d;
            }
        }

        // 如果 bestDir 仍为 -1，说明4方向都无效(撞墙)
        // 那就随便找一个可走方向
        if (bestDir == -1) {
            for (int d = 0; d < 4; d++) {
                int nx = aHead.x + dx[d];
                int ny = aHead.y + dy[d];
                if (inRange(nx, ny) && g[nx][ny] == 0) {
                    bestDir = d;
                    break;
                }
            }
            if (bestDir == -1) {
                // 实在没有就只能返回0
                bestDir = 0;
            }
        }

        return bestDir;
    }

    // ==============================
    // 保持原样：get()
    // ==============================
    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner scanner = new Scanner(file);
            String input = scanner.next();
            Integer move = nextMove(input);
            // 为了在本地调试时能看到结果，可打印一下
            System.out.println("AI final decision = " + move);
            return move;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
