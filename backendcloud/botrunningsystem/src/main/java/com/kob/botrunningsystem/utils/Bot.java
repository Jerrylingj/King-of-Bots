package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Bot implements java.util.function.Supplier<Integer> {
    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    //  检验蛇身是否变长
    private boolean check_tail_increasing(int step) {
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    //  拿取蛇身→从头开始一步步走，利用之前存的steps数组
    public List<Cell> getCells(int sx, int sy, String steps) {
        System.out.println("steps: " + steps);
        steps = steps.substring(1, steps.length() - 1); // 去掉括号
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0},dy = {0, 1, 0, -1};

        int x = sx,y = sy;

        int step = 0; // 记录步数
        res.add(new Cell(x, y));
        //  模拟出之前的每一步
        for  (int i = 0; i < steps.length(); i++) {
            int d = steps.charAt(i) - '0'; //   转化为整数
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


    public Integer nextMove(String input) {
        //  0:上 1:右 2:下 3:左
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

        for (Cell cell : aCells)  g[cell.x][cell.y] = 1;
        for (Cell cell : bCells)  g[cell.x][cell.y] = 1;

        int[] dx = {-1, 0, 1, 0},dy = {0, 1, 0, -1};


        //  版本1
//        for(int  i = 0; i  < 4; i ++) {
//            int x = aCells.get(aCells.size() - 1).x  + dx[i];
//            int y = aCells.get(aCells.size() - 1).y  + dy[i];
//            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
//                return i;
//            }
//        }

        // 版本2
//        Random random = new Random();
//        for(int i = 0; i < 100; i ++) {
//            int d = random.nextInt(4);
//            int x = aCells.get(aCells.size() - 1).x + dx[d];
//            int y = aCells.get(aCells.size() - 1).y + dy[d];
//            if(x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
//                return d;
//            }
//        }

        //  版本3
//        for (int i = 0; i < 4; i ++){
//            int x1 = aCells.get(aCells.size()-1).x + dx[i];
//            int y1 = aCells.get(aCells.size()-1).y + dy[i];
//            if (x1 >= 0 && x1 < 13 && y1 >= 0 && y1 < 14 && g[x1][y1] == 0) {
//                for (int j = 0; j < 4; j ++){
//                    int x2 = x1 + dx[j];
//                    int y2 = y1 + dy[j];
//                    if (x2 >= 0 && x2 < 13 && y2 >= 0 && y2 < 14 && g[x2][y2] == 0) {
//                        for (int k = 0; k < 4; k ++){
//                            int x3 = x2 + dx[k];
//                            int y3 = y2 + dy[k];
//                            if (x3 > 0 && x3 < 13 && y3 > 0 && y3 < 14 && g[x3][y3] == 0) {
//                                return i;
//                            }
//                        }
//                    }
//                }
//            }
//        }

        //  版本4：版本3+随机数优化版
        Random random = new Random();
        for(int i = 0; i < 20; i ++) {
            int d = random.nextInt(4);
            int x1 = aCells.get(aCells.size() - 1).x + dx[d];
            int y1 = aCells.get(aCells.size() - 1).y + dy[d];
            if(x1 >= 0 && x1 < 13 && y1 >= 0 && y1 < 14 && g[x1][y1] == 0) {
                for (int j = 0; j < 4; j ++){
                    int x2 = x1 + dx[j];
                    int y2 = y1 + dy[j];
                    if (x2 >= 0 && x2 < 13 && y2 >= 0 && y2 < 14 && g[x2][y2] == 0) {
                        for (int k = 0; k < 4; k ++){
                            int x3 = x2 + dx[k];
                            int y3 = y2 + dy[k];
                            if (x3 >= 0 && x3 < 13 && y3 >= 0 && y3 < 14 && g[x3][y3] == 0) {
                                return d;
                            }
                        }
                    }
                }
            }
        }


        return 0;
    }

    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner scanner = new Scanner(file);
            return nextMove(scanner.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
