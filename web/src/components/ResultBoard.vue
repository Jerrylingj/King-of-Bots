<template>
    <div class="result-board">
        <div class="result-board-text draw" v-if="$store.state.pk.loser === 'all'">
            Draw
        </div>
        <div class="result-board-text defeat" v-else-if="$store.state.pk.loser === 'A' && $store.state.pk.a_id == $store.state.user.id || $store.state.pk.loser === 'B' && $store.state.pk.b_id == $store.state.user.id">
            Defeat…
        </div>
        <div class="result-board-text victory" v-else>
            Victory!!
        </div>
        <div class="result-board-btn" v-if="$store.state.pk.loser !== 'none'">
            <button type="button" class="btn btn-danger" @click="restart">
                再来一把!
            </button>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';

export default {
    setup() {
        const store = useStore();

        //  重新匹配
        const restart = () => {
            //  重新回到匹配界面
            store.commit("updateStatus","matching");
            store.commit("updateLoser","none");
            store.commit("updateOpponent",{
                username: "我的对手",
                photo: "https://ts1.cn.mm.bing.net/th/id/R-C.c256dbecea080677593c4236b699020a?rik=qK71Iap4gmYNHA&riu=http%3a%2f%2fwww.imanami.com%2fwp-content%2fuploads%2f2016%2f03%2funknown-user.jpg&ehk=K9lZs3cX1UTchIGU%2baD4jf5rQeIyn41WGbDE9Ggxkm4%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1",
            });
        }

        return {
            restart,
        };
    }
}
</script>

<style scoped>
div.result-board {
    margin-top: 4vh;
    height: 30vh;
    width: 30vw;
    background-color: transparent;
    position: absolute;
    top: 30vh;
    left: 35vw;
}

div.result-board-text {
    margin-top: 5vh;
    padding: none;
    height: 15vh;
    width: 30vw;
    font-size: 5em;
    color: white;
    text-align: center;
    font-weight: 600;
    font-style: italic;
}

div.result-board-btn {
    height: 10vh;
    width: 30vw;
    font-size: 3em;
    text-align: center;
}

button.btn {
    width: 10vw;
}
.draw {  
  background: -webkit-linear-gradient(135deg,  
                    #f2f2f2, /* 浅灰色，接近白色但不过于刺眼 */  
                    #d9d9d9 25%, /* 稍微深一点的灰色 */  
                    #bfbfbf 50%, /* 中等灰色 */  
                    #a6a6a6 60%, /* 稍深灰色 */  
                    #8c8c8c 75%, /* 更深的灰色 */  
                    #737373); /* 最深的灰色 */  
  /* 其他属性保持不变 */  
  -webkit-text-fill-color: transparent;  
  -webkit-background-clip: text;  
  -webkit-background-size: 200% 100%;  
  -webkit-animation: flowCss 6s infinite linear;  
  animation: flowCss 6s infinite linear;  
}  
  
.draw:hover {  
  animation: flowCss 3s infinite linear; /* 悬停时加速 */  
}

.victory {  
  background: -webkit-linear-gradient(135deg,  
                    #ff6347, /* 浅红色 */  
                    #ff2d55 25%, /* 亮红色 */  
                    #e50000 50%, /* 深红色 */  
                    #ff69b4 55%, /* 紫红色 */  
                    #ff0000 60%, /* 纯红色 */  
                    #990000 80%, /* 暗红色 */  
                    #e9967a 95%, /* 橙红色 */  
                    #d2691e); /* 棕红色 */  
  -webkit-text-fill-color: transparent;  
  -webkit-background-clip: text;  
  -webkit-background-size: 200% 100%;  
  -webkit-animation: flowCss 6s infinite linear;  
  animation: flowCss 6s infinite linear;  
}  
  
.victory:hover {  
  animation: flowCss 3s infinite linear; /* 悬停时加速 */  
}

.defeat {  
  background: -webkit-linear-gradient(135deg,  
  #42a5f5, /* 亮蓝色 */  
                    #03a9f4 25%, /* 浅蓝色 */  
                    #007bff 50%, /* 蓝色 */  
                    #2196f3 55%, /* 青色 */  
                    #0040ff 60%, /* 深蓝色 */  
                    #0019b5 80%, /* 靛蓝色 */  
                    #00bfff 95%, /* 亮青色 */  
                    #0000ff); /* 纯蓝色 */   
  -webkit-text-fill-color: transparent;  
  -webkit-background-clip: text;  
  -webkit-background-size: 200% 100%;  
  -webkit-animation: flowCss 6s infinite linear; 
  animation: flowCss 6s infinite linear;  
}  
  
@-webkit-keyframes flowCss {
  0% {
      /* 移动背景位置 */
      background-position: 0 0;
  }

  100% {
      background-position: -400% 0;
  }
}

@keyframes flowCss {
  0% {
      /* 移动背景位置 */
      background-position: 0 0;
  }

  100% {
      background-position: -400% 0;
  }
}

.defeat:hover {  
  animation: flowCss 3s infinite linear;
}


</style>