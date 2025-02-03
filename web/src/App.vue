<template>
  <div id="app-container">
    <NavBar/>
    <router-view/>
    <!-- 竖屏模式下的提示覆盖层 -->
    <div v-if="isPortrait" class="orientation-overlay">
      <p>请旋转设备</p>
      <p>为了更好的体验，请将设备旋转至横屏模式。</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue';
import NavBar from '@/components/NavBar.vue';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";

export default {
  components: { 
    NavBar
  },
  setup() {
    const isPortrait = ref(window.matchMedia('(orientation: portrait)').matches);

    const checkOrientation = () => {
      isPortrait.value = window.matchMedia('(orientation: portrait)').matches;
    };

    onMounted(() => {
      window.addEventListener('resize', checkOrientation);
      window.addEventListener('orientationchange', checkOrientation);
    });

    onUnmounted(() => {
      window.removeEventListener('resize', checkOrientation);
      window.removeEventListener('orientationchange', checkOrientation);
    });

    return {
      isPortrait,
    };
  },
};
</script>

<style>
body {
  background-image: url("assets/images/Background1.jpg"); /* 使用 WebP 格式 */
  background-size: cover; /* 覆盖整个页面 */
  background-position: center; /* 图片居中 */
  background-repeat: no-repeat; /* 防止重复 */
  background-attachment: fixed; /* 固定背景图 */
  background-color: #f0f0f0; /* 加载时的占位背景色 */
}

/* 移动端优化 */
@media (max-width: 932px) {
  body {
    background-attachment: scroll; /* 取消固定 */
  }
}

#app-container {
  position: relative;
  width: 100%;
  height: 100vh;
}

.orientation-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 1);
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 20px;
  box-sizing: border-box;
  z-index: 1000;
}

.orientation-content h1 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.orientation-content p {
  font-size: 1.2rem;
  margin: 0;
}
</style>
