<template>
  <HomeField v-if="show_content">
    <div class="row justify-content-md-center">
      <div class="col-3 gradient-border" id="box">
        <form @submit.prevent="login">
          <div class="mb-3">
            <label for="username" class="form-label" style="color: black;">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label" style="color: black;">密码</label>
            <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
          </div>
          <div class="error-message">{{ error_message }}</div>
          <button type="submit" class="btn btn-primary" style="background-image: linear-gradient(rgba(241, 157, 172, 1),rgba(255, 255, 170, 1));">登录</button>
        </form>
      </div>
    </div>
  </HomeField>
</template>

<script>  
import HomeField from '../../../components/HomeField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue'
import router from '../../../router/index'

export default {  
  components: {
    HomeField,
  },
  setup() {
    const store = useStore();
    let username = ref('');
    let password = ref('');
    let error_message = ref('');
    let show_content = ref(false);

    const jwt_token = localStorage.getItem('jwt_token');
    if (jwt_token) {
      store.commit("updateToken", jwt_token);
      store.dispatch("getinfo",{
        success() {
          show_content
          router.push({ name: 'home' });
        },
        error() {
          show_content.value = true;
        }
      })
    } else {
      show_content.value = true;
    }

    const login = () => {
      store.dispatch("login", { 
        username: username.value, 
        password: password.value,
        success() {
          alert("登录成功！");
          store.dispatch("getinfo",{
            success() {
              setTimeout(() => {
                router.push({name: "home"});
              }, 500);
            }
          })
        },
        error() {
          error_message.value = "用户名或密码错误";
        }
      })
    }

    return {
      username,
      password,
      error_message,
      login,
      show_content,
    }
  }
};  
</script>  
  
<style scoped>  
button {
  width: 100%;
}

div.error-message {
  color: red;
}

@import url('https://fonts.googleapis.com/css?family=Raleway:200');

#box {
  padding: 30px 45px 30px 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: auto;
  height: auto;
  color: white;
}

.gradient-border {
  --borderWidth: 3px;
  background: white;
  position: relative;
  border-radius: 15px;
}

.gradient-border:after {
  content: '';
  position: absolute;
  top: calc(-1 * var(--borderWidth));
  left: calc(-1 * var(--borderWidth));
  height: calc(100% + var(--borderWidth) * 2);
  width: calc(100% + var(--borderWidth) * 2);
  background: linear-gradient(60deg, #f79533, #f37055, #ef4e7b, #a166ab, #5073b8, #1098ad, #07b39b, #6fba82);
  border-radius: calc(15px);
  z-index: -1;
  animation: animatedgradient 3s ease alternate infinite;
  background-size: 300% 300%;
}

div.login-card {
  height: 5vh;
  background-color: red;
}

@keyframes animatedgradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@media (max-width: 932px) {
  #box {
    padding: 25px 30px 25px 30px;
  }
  
  * {
    font-size: 0.8rem;
  }
}
</style>
