<template>
  <nav class="navbar navbar-expand bg-body-tertiary" style="box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);">
    <div class="navbar-icon"></div>
    <div class="container-fluid">
      <router-link class="navbar-brand title" :to="{name: 'home'}">King of Bots</router-link>

      <div class="navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item" v-if="$store.state.user.is_login">
            <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'pk_index'}">
              <img src="https://img.icons8.com/?size=100&id=yOrOkAN9XhDW&format=png&color=000000" class="icons">
              对战
            </router-link>
          </li>
          <li class="nav-item" v-if="$store.state.user.is_login">
            <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index'}">
              <img src="https://img.icons8.com/?size=100&id=R2FM5ROUGp7B&format=png&color=000000" class="icons">
              对局列表
            </router-link>
          </li>
          <li class="nav-item" v-if="$store.state.user.is_login">
            <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index'}">
              <img src="https://img.icons8.com/?size=100&id=zZ5b6zKe0H8l&format=png&color=000000" class="icons">
              排行榜
            </router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name == 'rule_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'rule_index'}">
              <img src="https://img.icons8.com/?size=100&id=oQjhDr71haP1&format=png&color=000000" class="icons">
              游戏规则
            </router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name == 'example_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'example_index'}">
              <img src="https://img.icons8.com/?size=100&id=CSzrvtlqxYnE&format=png&color=000000" class="icons">
              样例代码
            </router-link>
          </li>
        </ul>

        <ul class="navbar-nav" v-if="$store.state.user.is_login">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <img src="https://img.icons8.com/?size=100&id=4V1nG4SioGjp&format=png&color=000000" class="icons">
              {{ $store.state.user.username }}
            </a>
            <ul class="dropdown-menu">
              <li>
                <router-link class="dropdown-item" :to="{name: 'user_bot_index'}">
                  <img src="https://img.icons8.com/?size=100&id=XNotH4e8lEuO&format=png&color=000000" class="icons">
                  我的Bot
                </router-link>
              </li>
              <li>
                <a class="dropdown-item" href="#" @click="logout">
                  <img src="https://img.icons8.com/?size=100&id=42byuSCZTK8Q&format=png&color=000000" class="icons">
                  退出
                </a>
              </li>
            </ul>
          </li>
        </ul>

        <ul class="navbar-nav" v-else>
          <li class="nav-item">
            <router-link class="nav-link" :to="{name: 'user_account_login'}" role="button">
              <img src="https://img.icons8.com/?size=100&id=ZksQ17CxR66o&format=png&color=000000" class="icons">
              登录
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
              <img src="https://img.icons8.com/?size=100&id=oaVmuvfbtK2x&format=png&color=000000" class="icons">
              注册
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue';
import { useStore } from 'vuex';

export default {
  setup() {
    const store = useStore();
    const route = useRoute();
    const route_name = computed(() => route.name);

    const logout = () => {
      store.dispatch("logout");
    };

    return {
      route_name,
      logout
    };
  },

  mounted() {
    const script = document.createElement('script');
    script.src = "https://api.vvhan.com/api/script/yinghua";
    script.async = true;
    document.body.appendChild(script);
  }
};
</script>

<style scoped>
.navbar-icon {
  width: 2vw;
  height: 2vw;
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
  -webkit-text-fill-color: transparent;
  -webkit-background-clip: text;
  -webkit-background-size: 200% 100%;
  -webkit-animation: flowCss 6s infinite linear;
  animation: flowCss 6s infinite linear;
}

@-webkit-keyframes flowCss {
  0% { background-position: 0 0; }
  100% { background-position: -400% 0; }
}

@keyframes flowCss {
  0% { background-position: 0 0; }
  100% { background-position: -400% 0; }
}

.title:hover {
  -webkit-animation: flowCss 3s infinite linear;
  animation: flowCss 3s infinite linear;
}

img.icons {
  width: 1.5vw;
  height: 1.5vw;
}

.navbar {
  width: 100%;
  overflow: visible;
}

.container-fluid {
  max-width: 100%;
}

* {
  font-weight: bold;
}

@media (max-width: 932px) {
  .navbar {
    height: 2rem;
  }
  .nav-link {
    height: 24px;
  }
  * {
    font-size: 0.6rem;
  }
}

</style>
