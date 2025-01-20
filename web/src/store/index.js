import { createStore } from 'vuex'
import ModuleUser from './user'
import ModulePk from './pk'
import ModuleRecord from './record'

export default createStore({
  state: {
    baseUrl: "http://localhost:3000",
  },
  getters: {
    getBaseUrl: (state) => state.baseUrl, // 提供一个 getter 方便获取 baseUrl
  },
  mutations: {
    setBaseUrl(state, newBaseUrl) { // 提供一个 mutation 来更新 baseUrl
      state.baseUrl = newBaseUrl;
    },
  },
  actions: {
    // 从 config.json 加载 baseUrl
    loadBaseUrl({ commit }) {
      return fetch('../config.json') // 读取配置文件
        .then(response => {
          if (!response.ok) {
            throw new Error("Failed to load config.json");
          }
          return response.json();
        })
        .then(config => {
          commit('setBaseUrl', config.baseUrl); // 更新 baseUrl
        })
        .catch(error => {
          console.error("Failed to load baseUrl:", error);
        });
    },
  },
  modules: {
    user: ModuleUser,
    pk: ModulePk,
    record: ModuleRecord,
  }
});