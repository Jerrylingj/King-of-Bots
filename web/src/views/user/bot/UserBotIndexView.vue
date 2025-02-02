<template>
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card" style="margin-top: 20px;">
          <div class="card-body">
            <img :src="$store.state.user.photo" alt="" style="width: 100%;">
          </div>
        </div>
      </div>
      <div class="col-9">
        <div class="card" style="margin-top: 20px;">
          <div class="card-header">
            <span style="font-size: 120%;">我的Bot</span>
            <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-modal">创建Bot</button>

            <!-- Modal -->
            <div class="modal fade" id="add-bot-modal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
              <div class="modal-dialog modal-dialog modal-dialog-centered modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title" style="font-size: 150%;">创建Bot</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-title" class="form-label" style="font-size: 130%;">名称</label>
                      <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                    </div>
                    <div class="mb-3">
                      <label for="description" class="form-label" style="font-size: 130%;">备注</label>
                      <textarea v-model="botadd.description" class="form-control" id="description" rows="2" placeholder="请输入Bot备注"></textarea>
                    </div>
                    <div class="mb-3">


                      <nav class="navbar navbar-expand-lg bg-body-tertiary">
                        <div class="container-fluid">
                          <a class="navbar-brand" href="#">代码</a>
                          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarScroll" aria-controls="navbarScroll" aria-expanded="false" aria-label="Toggle navigation">
                            <span class="navbar-toggler-icon"></span>
                          </button>
                        </div>
                      </nav>
                      
                      <VAceEditor
                      v-model:value="botadd.content"
                      @init="editorInit"
                      lang="c_cpp"
                      theme="dawn"
                      style="height: 300px;font-size: large;"
                      :options="{
                      enableBasicAutocompletion: true, //启用基本自动完成
                      enableSnippets: true, // 启用代码段
                      enableLiveAutocompletion: true, // 启用实时自动完成
                      fontSize: 18, //设置字号
                      tabSize: 4, // 标签大小
                      showPrintMargin: false, //去除编辑器里的竖线
                      highlightActiveLine: true}"/>
                    </div>
                  </div>
                  <div class="modal-footer">
                    <!-- 放个位置留给报错信息 -->
                    <div class="error-message">{{ botadd.error_message }}</div>
                    <button type="button" class="btn btn-danger" @click="add_bot">提交</button>
                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal">取消</button>
                  </div>
                </div>
              </div>
            </div>

          </div>
          <div class="card-body">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th style="text-align: center;">Bot名称</th>
                  <th style="text-align: center;">备注</th>
                  <th style="text-align: center;">创建时间</th>
                  <th style="text-align: center;">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="bot in bots" :key="bot.id">
                  <td style="text-align: center;">{{ bot.title }}</td>
                  <td style="text-align: center;">{{ bot.description }}</td>
                  <td style="text-align: center;">{{ bot.createtime }}</td>
                  <td style="text-align: center;">
                    <button type="button" class="btn btn-primary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal' + bot.id">修改</button>
                    
                    <!-- Modal -->
                    <div class="modal fade" :id="'update-bot-modal' + bot.id" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
                      <div class="modal-dialog modal-dialog modal-dialog-centered modal-xl">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title" style="font-size: 150%;">修改Bot</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <div class="modal-body">
                            <div class="mb-3">
                              <label for="update-bot-title" class="form-label" style="font-size: 130%;">名称</label>
                              <input v-model="bot.title" type="text" class="form-control" id="update-bot-title" placeholder="请输入Bot名称">
                            </div>
                            <div class="mb-3">
                              <label for="description" class="form-label" style="font-size: 130%;">备注</label>
                              <textarea v-model="bot.description" class="form-control" id="description" rows="2" placeholder="请输入Bot备注"></textarea>
                            </div>
                            <div class="mb-3">
                              <label for="content" class="form-label" style="font-size: 130%;">代码</label>
                              <VAceEditor
                                  v-model:value="botadd.content"
                                  @init="editorInit"
                                  lang="c_cpp"
                                  theme="dawn"
                                  style="height: 300px;font-size: large;" 
                                  :options="{
                                  enableBasicAutocompletion: true, //启用基本自动完成
                                  enableSnippets: true, // 启用代码段
                                  enableLiveAutocompletion: true, // 启用实时自动完成
                                  fontSize: 18, //设置字号
                                  tabSize: 4, // 标签大小
                                  showPrintMargin: false, //去除编辑器里的竖线
                                  highlightActiveLine: true}"/>
                            </div>
                          </div>
                          <div class="modal-footer">
                            <!-- 放个位置留给报错信息 -->
                            <div class="error-message">{{ botupdate.error_message }}</div>
                            <button type="button" class="btn btn-danger"  @click="update_bot(bot)">提交</button>
                            <button type="button" class="btn btn-primary" data-bs-dismiss="modal">取消</button>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>  
import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-python';
import 'ace-builds/src-noconflict/theme-dawn';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {  
  components: {
    VAceEditor,
  },

  setup() {
    // 导入代码编辑器
    ace.config.set(
    "basePath", 
    "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")

    const store = useStore();
    const baseUrl = store.state.baseUrl;
    let bots = ref([]);

    const botadd = reactive({
      title: "",
      description: "",
      content: "",
      error_message: "",
    })

    const botupdate = reactive({
      title: "",
      description: "",
      content: "",
      error_message: "",
    })

    const refresh_bots = () => {
      $.ajax({
        url: `${baseUrl}/api/user/bot/getlist/`,
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

    const add_bot = () => {
      botadd.error_message = "";
      $.ajax({
        url: `${baseUrl}/api/user/bot/add/`,
        type: "POST",
        data: {
          // reactive后面不用加.value,ref后面才要加.value
          title: botadd.title,
          description: botadd.description,
          content: botadd.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            // 先清空当前信息
            botadd.title = "";
            botadd.description = "";
            botadd.content = "";
            Modal.getInstance("#add-bot-modal").hide();

            refresh_bots();
          } else {
            botadd.error_message = resp.error_message;
          }
        }

      })
    }

    const remove_bot = (bot) => {

      $.ajax({
        url: `${baseUrl}/api/user/bot/remove/`,
        type: "POST",
        data: {
          // reactive后面不用加.value,ref后面才要加.value
          bot_id: bot.id,
          
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if(resp.error_message === "success")
          {
            refresh_bots();
          }
        }
      })
    }

    const update_bot = (bot) => {
      botupdate.error_message = "";
      $.ajax({
        url: `${baseUrl}/api/user/bot/update/`,
        type: "POST",
        data: {
          // reactive后面不用加.value,ref后面才要加.value
          bot_id: bot.id,
          title: bot.title,
          description: bot.description,
          content: bot.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "update success") {
            Modal.getInstance("#update-bot-modal" + bot.id).hide();
            refresh_bots();
          } else {
            botupdate.error_message = resp.error_message;
          }
        }
      })
    }

    return {
      bots,
      botadd,
      botupdate,
      add_bot,
      remove_bot,
      update_bot,
    }
  }

};  
</script>  
  
<style scoped>  
/* 这里是你的组件样式 */  
div.error-message {
  color: red;
}
@media (max-width: 932px) {
  * {
    font-size: 0.8rem;
  }
}
</style>
