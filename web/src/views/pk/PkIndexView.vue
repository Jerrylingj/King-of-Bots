/* eslint-disable */
<template>
  <PlayGround v-if="$store.state.pk.status === 'playing'">

  </PlayGround>

  <MatchGround v-if="$store.state.pk.status === 'matching'">

  </MatchGround>
  <ResultBoard v-if="$store.state.pk.loser !== 'none'">
  </ResultBoard>

  <div class="user-color" v-if="$store.state.pk.status === 'playing'">
    <div class="text">当前颜色：</div>
    <div class="color-red" v-if="parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)"></div>
    <div class="color-blue" v-if="parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)"></div>
  </div>
  <!-- <div class="user-color">
    右上角
    </div> -->
</template>


<script>  
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue';
import ResultBoard from '../../components/ResultBoard.vue'
import { onMounted, onUnmounted} from 'vue'
import { useStore } from 'vuex'
export default {  
  components: {
    PlayGround,
    MatchGround,
    ResultBoard
  },

  setup() {
    let socket = null;
    const store = useStore();
    const baseUrl = store.state.baseUrl;
    const wssUrl = baseUrl.replace("http", "ws");
    console.log("wssUrl:",wssUrl);
    const socketUrl = `${wssUrl}/websocket/${store.state.user.token}/`;
  
    // 界面加载时
    onMounted(() => {
      store.commit("updateOpponent", {
        username: "我的对手",
        photo: "https://ts1.cn.mm.bing.net/th/id/R-C.c256dbecea080677593c4236b699020a?rik=qK71Iap4gmYNHA&riu=http%3a%2f%2fwww.imanami.com%2fwp-content%2fuploads%2f2016%2f03%2funknown-user.jpg&ehk=K9lZs3cX1UTchIGU%2baD4jf5rQeIyn41WGbDE9Ggxkm4%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1",
      })
      socket = new WebSocket(socketUrl);

      socket.onopen = () => {
        console.log("connected");
        store.commit("updateSocket", socket);
      }

      socket.onmessage = msg => {
        const data  = JSON.parse(msg.data);
        
        //  匹配成功,更新对手信息
        if (data.event === "start-matching") {
          //  匹配成功,更新对手信息
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });

          //  更改当前界面为匹配成功
          setTimeout(() => {
          store.commit("updateStatus", "playing");
          },400);
          store.commit("updateGame", data.game);
        } else if (data.event === "move") {
          //  开始移动
          console.log(data);
          const game = store.state.pk.gameObject;
          const [snake0, snake1] = game.snakes;
          snake0.set_direction(data.a_direction);
          snake1.set_direction(data.b_direction);


        } else if (data.event === "result") {
          //  对局结束
          console.log(data);
          const game = store.state.pk.gameObject;
          const [snake0, snake1] = game.snakes;
            
          if (data.loser === "all" || data.loser === "A") {
            snake0.status = "die";
          }
          if (data.loser === "all" || data.loser === "B"){
            snake1.status = "die";
          }
          //  更新状态
          store.commit("updateLoser",data.loser);
        }
      }

      socket.onclose = () => {
        console.log("disconnected");
        store.commit("updateStatus", "matching");
        store.commit("updateLoser","none");
        store.commit("updateIsRecord", false);
      }
    });

    // 界面关闭时,断开连接,同时默认放弃比赛
    onUnmounted(() => {
      socket.close();
      store.commit("updateStatus", "matching");
      store.commit("updateLoser","none");
      store.commit("updateIsRecord", false);
    })


  }
};  
</script>  
  
<style scoped>  
/* 这里是你的组件样式 */  
div.user-color {
   height: 5vh;
   width: 30vh;
   margin-left: 5vh;
   padding: none;

}
div.text {
    font-family: cursive;
    font-size: 1.8em;
    font-weight: 400;
    color: white;
    position: absolute;
    padding: none;
}
div.color-red {
  margin-left: 15vh;
  background-color: #F94848;
  height: 5vh;
  width: 5vh;
  border-radius: 50%;
  position: absolute;
}
div.color-blue {
  margin-left: 15vh;
  background-color: #4876EC;
  height: 5vh;
  width: 5vh;
  border-radius: 50%;
  position: absolute;
}
</style>
