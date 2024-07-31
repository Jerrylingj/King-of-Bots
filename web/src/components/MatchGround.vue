<template>
    <div class="matchground">
      <div class="row">
        <div class="col-4">
          <div class="user-photo">
            <img :src="$store.state.user.photo">
          </div>
          <div class="user-username">
            {{ $store.state.user.username  }}
          </div>
        </div>
        <div class="col-4">
          <div class="user-select-bot">
            <select v-model="select_bot" class="form-select" aria-label="Default select example">
              <option selected style="font-family: cursive;" value="-1">亲自出马</option>
              <option v-for="bot in bots" :key="bot.id" :value="bot.id" style="font-family: cursive;">
                {{  bot.title }}
              </option>
            </select>
          </div>
        </div>
        <div class="col-4">
          <div class="user-photo">
            <img :src="$store.state.pk.opponent_photo">
          </div>
          <div class="user-username">
            {{ $store.state.pk.opponent_username }}
          </div>
        </div>
        <div class="col-12" style="text-align: center;">
          <button type="button" class="btn btn-primary" @click="click_match_btn">{{match_btn_info}}</button>
        </div>
      </div>
    </div>
</template>


<script>
import { ref } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'

export default {
  setup() {
    const store = useStore();
    let match_btn_info = ref("开始匹配");
    let bots = ref([])
    //  传回选择的机器人
    let select_bot = ref("-1");

    //  增加取消匹配事件
    const click_match_btn = () => {
      if (match_btn_info.value === "开始匹配") {
        match_btn_info.value = "取消";
        console.log(select_bot.value);
        //  向后端发送请求开始匹配
        store.state.pk.socket.send(JSON.stringify({
          event: "start-matching",
          bot_id: select_bot.value,
        }));
      } else {
        match_btn_info.value = "开始匹配";
        //  向后端发送请求取消匹配
        store.state.pk.socket.send(JSON.stringify({
          event: "stop-matching",
        }));
      }
    } 
    const refresh_bots = () => {
      $.ajax({
        url: "https://app7033.acapp.acwing.com.cn/api/user/bot/getlist/",
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          bots.value = resp;
        }
      })
    }

    refresh_bots();

    return {
      match_btn_info,
      click_match_btn,
      bots,
      select_bot,
    }
  }
}
</script>

<style scoped>

div.matchground {
  width: 60vw;
  height: 70vh;
  margin: 40px auto;
  background-color: rgba(50,50,50,0.5);
}

div.user-photo {
  text-align: center;
  margin-top: 140px;
  margin-bottom: 20px;
}

div.user-photo > img {
  border-radius: 50%;
  width: 18vw;
  height: 18vw;
}

div.user-username {
  text-align: center;
  font-size: 24px;
  font-weight: 550;
  color:white;
}

button.btn {
  margin: 30px;
  width: 10vw;
  height: 4.5vh;
  font-size: 110%;
}

div.user-select-bot {
  padding-top: 20vh;
}
div.user-select-bot > select {
  font-family: cursive;
  width: 60%;
  margin: 0 auto;
}

</style>