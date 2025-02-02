<template>
   <div class="container content-field">
      <div class="card1">
        <div class="card-body">
          <slot>
            <div class="card interact">
              <h3>Bot交互方式</h3>
              每一回合系统会将当前对局信息完全传输给用户，并读取用户的Bot代码后对Bot进行操作<br><br>
              
              <h5>输入格式</h5>
              <ul>
                <li>一串字符串：初始地图信息#己方Sx#己方Sy#(己方所有操作)#对方Sx#对方Sy#(对方所有操作)</li>
                <li>不用担心（。＾▽＾），这些信息在Bot代码样例模板中都处理好了~</li>
              </ul>
              <h5>输出格式</h5>
              <ul>
                <li>一个整数，代表运动方向：0 上  |  1 右  |  2 下  |  3 左</li>
                <li>玩家只需在'nextMove'函数中给出每一回合的输出即可</li>
              </ul>
              <h5>输入样例</h5>
              <ul>
                11111111111111100011010000011001000000000110001100001001100000001000011001000000000110000100100001100000000010011000010000000110010000110001100000000010011000001011000111111111111111#11#1#(011101210010)#1#12#(233322233033)
              </ul>
              <h5>样例解释</h5>
              <ul>
                <li>前面的01字符串为2为地图信息压缩后的1维字符串，0表示空地，1表示障碍物</li>
                <li>接下来的初始坐标信息可以帮助你判断你和对方的起点</li>
                <li>括号内的内容为每回合双方的移动方向，结合蛇身的伸长方式可以完全确定当前的游戏局面</li>
                <li>通过以上信息，你可以判断出当前回合下最有利于己方的移动方向</li>
              </ul>
            </div>
            <div class="card code">
              <h3 style="margin-bottom: 15px;">Bot样例代码</h3>
                <VAceEditor
                  lang="c_cpp"
                  theme="dracula"
                  style="height: 1850px; width: 100%; font-size: large;"
                  :options="{
                    enableBasicAutocompletion: true,
                    enableSnippets: true,
                    enableLiveAutocompletion: true,
                    fontSize: 18,
                    tabSize: 4,
                    showPrintMargin: false,
                    highlightActiveLine: true,
                    readOnly: true,
                  }"
                  v-model:value="code"
                />
            </div>
          </slot>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { VAceEditor } from 'vue3-ace-editor';
  import { ref } from 'vue';  
  import ace from 'ace-builds';
  import 'ace-builds/src-noconflict/theme-dracula';
  import 'ace-builds/src-noconflict/mode-c_cpp';
  import 'ace-builds/src-noconflict/ext-language_tools';
  
  export default {
    components: {
      VAceEditor
    },
    
    setup() {
        const code = ref(`package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bot implements java.util.function.Supplier<Integer> {
    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private boolean check_tail_increasing(int step) {  // 检验当前回合，蛇的长度是否增加
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    //  还原出蛇身
    public List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int i = 0; i < steps.length(); i ++ ) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing( ++ step)) {
                res.remove(0);
            }
        }
        return res;
    }


    //  获取下一步输入,返回值即为当前回合Bot移动方向
    //  0 上  |  1 右  |  2 下  |  3 左
    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        int[][] g = new int[13][14];

        //  还原原始地图
        for (int i = 0, k = 0; i < 13; i ++ ) {
            for (int j = 0; j < 14; j ++, k ++ ) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }

        // 还原蛇身
        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);

        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);

        //  更新地图
        for (Cell c: aCells) g[c.x][c.y] = 1;
        for (Cell c: bCells) g[c.x][c.y] = 1;

/*-----------------------------------需要修改的部分----------------------------------------*/

        // 制定当前回合策略
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for (int i = 0; i < 4; i ++ ) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];
            int y = aCells.get(aCells.size() - 1).y + dy[i];
            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
                return i;
            }
        }

        return 0;
/*-----------------------------------------------------------------------------------------*/
    }
    
    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}`);
      // 导入代码编辑器
      ace.config.set(
        "basePath",
        "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/"
      );

      return {
        code
      };
    }
  };
  </script>
  
  <style scoped>
  h3 {
    font-size: 1.2em;
    font-weight: 550;
  }
  h5 {
    font-size: 1.1em;
    font-weight: 550;
  }
  div.interact {
    padding: 1vh;
    border-width: 2px;
  }
  div.code {
    margin-top: 5vh;
    padding: 1vh;
    height: 205vh;
    border-width: 2px;
  }

  :root {
    font-size: 20px;
  }

  *,
  *:before,
  *:after {
    box-sizing: border-box;
  }

  p {
    margin: 0;
  }

  p:not(:last-child) {
    margin-bottom: 1.5em;
  }

  body {
    font: 1em/1.618 Inter, sans-serif;

    display: flex;
    align-items: center;
    justify-content: center;
    
    min-height: 100vh;
    padding: 30px;
    margin: 0;
    
    color: #224;
    background:
      url(https://source.unsplash.com/E8Ufcyxz514/2400x1823)
      center / cover no-repeat fixed;
  }

  .card {
    max-width: 65vw;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 35px;

    border: 1px solid rgba(255, 255, 255, .25);
    border-radius: 20px;
    background-color: rgba(255, 255, 255, 0.45);
    box-shadow: 0 0 10px 1px rgba(0, 0, 0, 0.25);

    backdrop-filter: blur(15px);
  }

  .card-footer {
    font-size: 0.65em;
    color: #446;
  }

  div.content-field{
      margin-top: 6vh;
      width: 65vw;
      height: 60vh;
  }

  @media (max-width: 932px) {
    div.content-field{
      width: 90vw;
    }

    div.card {
      max-width: 90vw;
    }

    * {
      font-size: 0.9rem;
    }
  }

</style>
  