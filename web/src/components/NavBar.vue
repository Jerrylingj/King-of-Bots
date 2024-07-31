<template>
<nav class="navbar navbar-expand-lg bg-body-tertiary">
  <div class="navbar-icon"></div>
  <div class="container">
    
    <router-link class="navbar-brand title" :to="{name: 'home'}">King of Bots</router-link>
    <div class="collapse navbar-collapse" id="navbarText">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item" v-if="$store.state.user.is_login">
          <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'pk_index'}">对战</router-link>
        </li>
        <li class="nav-item" v-if="$store.state.user.is_login">
          <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index'}">对局列表</router-link>
        </li>
        <li class="nav-item" v-if="$store.state.user.is_login">
          <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index'}">排行榜</router-link>
        </li>
        <li class="nav-item">
          <router-link :class="route_name == 'rule_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'rule_index'}">游戏规则</router-link>
        </li>
        <li class="nav-item">
          <router-link :class="route_name == 'example_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'example_index'}">样例代码</router-link>
        </li>
      </ul>
      <ul class="navbar-nav" v-if="$store.state.user.is_login">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            {{ $store.state.user.username }}
          </a>
          <ul class="dropdown-menu">
            <li><router-link class="dropdown-item" :to="{name: 'user_bot_index'}">我的Bot</router-link></li>
            <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
          </ul>
        </li>
      </ul>
      <ul class="navbar-nav" v-else>
        <li class="nav-item">
          <router-link class="nav-link" :to="{name: 'user_account_login'}" role="button">
            登录
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
            注册
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</nav>
</template>

<script>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useStore } from 'vuex'

export default{
  setup(){
      const store = useStore();
      const route = useRoute();
      let route_name = computed(() => route.name)

      const logout = () => {
        store.dispatch("logout");
      }

      return {
          route_name,
          logout
      }
  },

  mounted() {
    const script = document.createElement('script');
    script.src = "https://api.vvhan.com/api/script/yinghua";
    script.async = true;
    document.body.appendChild(script);
  }
}

</script>

<style scoped>
/* 加上 -webkit- 注意兼容 */
.navbar-icon {
  width: 40px;
  height: 40px;
  margin-left: 100px;
  background-image: url("../assets/images/icon.png");
  background-size: 100% 100%;
}
.title {
  font: italic 1.5em Georgia, serif;
  background: -webkit-linear-gradient(135deg,
                    #0eaf6d,
                    #ff6ac6 25%,
                    #147b96 50%,
                    #e6d205 55%,
                    #2cc4e0 60%,
                    #8b2ce0 80%,
                    #ff6384 95%,
                    #08dfb4);
  /* 文字颜色填充设置为透明 */
  -webkit-text-fill-color: transparent;
  /* 背景裁剪，即让文字使用背景色 */
  -webkit-background-clip: text;
  /* 背景图放大一下，看着柔和一些 */
  -webkit-background-size: 200% 100%;
  /* 应用动画flowCss 4秒速度 无限循环 线性匀速动画*/
  -webkit-animation: flowCss 6s infinite linear;
  animation: flowCss 6s infinite linear; /* 标准动画属性 */
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

.title:hover {
  -webkit-animation: flowCss 3s infinite linear;
  animation: flowCss 3s infinite linear; /* 标准动画属性 */
}
</style>
