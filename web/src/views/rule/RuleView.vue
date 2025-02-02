<template>
    <div class="container content-field">
      <div class="card" id="1">
        <div class="card-body">
          <slot>
            <div class="card1 introduction">
              <h3>前言</h3>
                <p>确切地说，这并不是贪吃蛇。<br>
                  King of Bots为<strong>双人对战的回合制游戏</strong>(●ˇ∀ˇ●)<br>
                  欢迎各位大佬贡献自己的Bot代码，祝各位游戏愉快~ o(*￣▽￣*)ブ
                </p>
            </div>

            <div class="card1 rule">
              <h3>游戏规则</h3>
              <ul>
                <li>玩家可以选择亲自出马或者提交Bot代码（目前仅支持Java）来操纵自己的蛇，成功进入对局后在<span style="color:red;font-weight: 550;">对战界面的左下角</span>会显示己方蛇的颜色。</li>
                <li>每一回合，双方通过交互给出一条Bot的移动指令，<span style="color:red;font-weight: 550;">最多等待5秒</span>，接收到双方指令后，Bot移动一步</li>
                <li>
                  地图为13×14的网格，为保证公平已将地图设置为中心对称，由1x1的草地与障碍物（共20个）构成,进行以下行为将会直接被判定为失败：
                  <ul>
                      <li>在规定时间(5秒)内未给出Bot移动指令</li>
                      <li>撞到障碍物</li>
                      <li>主动撞到对方或者自己咬自己</li>
                  </ul>
                </li>
                <li>当双方同时在一回合内失败，对局结果为平局</li>
              </ul>
      s     </div>
            <div class="card1 guidence">
              <h3>指引</h3>
              ps:由于制作的非常简陋，当前版本可能还存在许多bug，蹲蹲后续的反馈，后面可能会进行更新维护
              <ul>
                <li>
                  当前已经<span style="color:red;font-weight: 550;">更新系统人机</span>，后续打算更新提交头像以及社交账号授权登录功能
                </li>
                <li>
                  注册→登录→进入主页面，看到4个主要模块：
                  <ul>
                    <li>对战：玩家在此对局匹配</li>
                    <li>对局列表：当前可以查看所有对局记录，支持对局回放功能(目前完全开放)</li>
                    <li>排行榜：展示所有玩家的rating值</li>
                    <li>用户中心（右上角）：玩家可以在 '我的Bot' 管理自己的Bot仓库，编辑自己的Bot代码，打造最强Bot！</li>
                  </ul>
                </li>
                <li>在对局过程中<span style="color:red;font-weight: 550;">千万不要刷新！千万不要刷新！千万不要刷新！</span>（哈哈重要的事情说三遍），目前还未实现断线重连功能，一旦刷新将直接退出对局</li>
                <li>在对战界面中可以选择Bot的操纵方式：
                  <ul>
                    <li>'亲自出马'：玩家通过键盘的↑ → ↓ ←来控制每回合Bot的移动</li>
                    <li>'<i>你的Bot </i>'：玩家选择出战的Bot，通过读取每回合代码的输出来控制Bot的移动方向</li>
                  </ul>
                </li>
                <li>每轮对局，胜方rating+5,败方rating-2，平局双方rating+3</li>
              </ul>
            </div>
          </slot>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {    
    setup() {
    }
  }
  </script>
  
  <style scoped>
  h3 {
    font-size: 1.2em;
    font-weight: 550;
  }
  div.introduction {
    padding: 1vh;
    border-width: 2px;
  }
  div.rule {
    margin-top: 3vh;
    padding: 1vh;
    border-width: 2px;
  }
  div.guidence {
    margin-top: 3vh;
    padding: 1vh;
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

  .card1 {
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
      margin-top: 2vh;
      width: 65vw;
      height: 60vh;
  }

  .card {
    background-color: transparent;
    border: none;

  }

  </style>
  