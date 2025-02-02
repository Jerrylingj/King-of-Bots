<template>
    <HomeField>
      <div class="row justify-content-md-center">
        <div class="col-3 gradient-border" id="box">
          <form @submit.prevent="register">
            <div class="mb-3">
              <label for="username" class="form-label" style="color: black;">用户名</label>
              <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
            </div>
            <div class="mb-3">
              <label for="password" class="form-label" style="color: black;">密码</label>
              <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
            </div>
            <div class="mb-3">
              <label for="confirmPassword" class="form-label" style="color: black;">确认密码</label>
              <input v-model="confirmPassword" type="password" class="form-control" id="confirmPassword" placeholder="请再次输入密码">
            </div>
            <div class="error-message">{{ error_message }}</div>
            <button type="submit" class="btn btn-primary"  style="background-image: linear-gradient(rgba(241, 157, 172, 1),rgba(255, 255, 170, 1));">注册</button>
          </form>

        </div>
      </div>
    </HomeField>
  </template>
  
  
  <script>  
  import HomeField from '../../../components/HomeField.vue'
  import { ref } from 'vue'
  import router from '../../../router/index'
  import {useStore} from 'vuex'
  import $ from 'jquery'

  export default {  
    components: {
      HomeField,
    },
    setup() {
      let username = ref('');
      let password = ref('');
      let confirmPassword = ref('');
      let error_message = ref('');
      const store = useStore();
      const baseUrl = store.state.baseUrl;

      const register = () => {
        console.log("baseUrl：",baseUrl);
        $.ajax({
          // 连接到后端
          url: `${baseUrl}/api/user/account/register/`,
          type: "post",
          data: {
            username: username.value,
            password: password.value,
            confirmPassword: confirmPassword.value,
          },
          success(resp) {
            if (resp.error_message === "success" ) {
              alert("注册成功！即将跳转到登录页面...");
            setTimeout(() => {
              router.push({name: "user_account_login"});
            }, 500); // 0.5秒后跳转
            } else {
              error_message.value = resp.error_message;
            }
          }
        })
      }

      return {
        username,
        password,
        confirmPassword,
        error_message,
        register
      }
    }
  };  
  </script>  
    
  <style scoped>  
  /* 这里是你的组件样式 */  
  button {
    /* 注册按钮 */
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
    font-size: 0.7rem;
  }
}

</style>
  