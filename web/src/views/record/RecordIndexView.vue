<template>
  <ContentField>
    <table class="table table-striped table-hover" style="margin-top: 2vh;">
              <thead>
                <tr style="padding: 1vh;">
                  <th>玩家A</th>
                  <th>玩家B</th>
                  <th>对战结果</th>
                  <th>对战时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in records" :key="record.record.id">
                  <td style="text-align: center;">
                    <img :src="record.a_photo" class="record-user-photo">
                    <span class="record-user-username">{{ record.a_username }}</span>
                  </td>
                  <td style="text-align: center;">
                    <img :src="record.b_photo" class="record-user-photo">
                    <span class="record-user-username">{{ record.b_username }}</span>
                  </td>
                  <td style="text-align: center;font-size: 1.5em;font-family: cursive;">{{ record.result }}</td>

                  <td style="text-align: center;">{{ record.record.createtime }}</td>
                  <td style="text-align: center;">
                    <button type="button" class="btn btn-danger" @click="open_record_content(record.record.id)">查看录像</button>
                  </td>
                </tr>
              </tbody>
            </table>
            <nav aria-label="...">
              <ul class="pagination" style="float: right;">
                <li class="page-item" @click="click_page(-2)">
                  <a class="page-link" href="#">上一页</a>
                </li>
                
                <li :class="'page-item ' + page.is_active" aria-current="page" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                  <a class="page-link" href="#">{{  page.number }}</a>
                </li>

                <li class="page-item" @click="click_page(-1)">
                  <a class="page-link" href="#">下一页</a>
                </li>
              </ul>
            </nav>
  </ContentField>
</template>


<script>  
import ContentField from '../../components/ContentField.vue'
import { useStore} from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';
import router from '../../router/index'

export default {  
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    const baseUrl = store.state.baseUrl;
    let records = ref([]);
    let current_page = 1;
    let total_records = 0;
    let pages = ref([]);

    //  页面点击按钮
    const click_page = page => {
      if (page === -2) page = Math.max(current_page - 1, 1);
      else if (page === -1) {
        let max_pages = parseInt(Math.ceil(total_records / 10));
        page = Math.min(max_pages, current_page + 1);
      } 

      pull_page(page);
    }

    //更新页面
    const update_pages = () => {
      let max_pages = parseInt(Math.ceil(total_records / 10));
      let new_pages = [];
      for (let i = current_page - 2; i <= current_page + 2; i ++) {
        if(i >= 1 && i <= max_pages) {
          new_pages.push({
            number: i,
            is_active: i === current_page ? "active" : "",
          });
        }
      }
      pages.value = new_pages;
    }
    //  查询某一个页面内容
    const pull_page = page => {
      current_page = page;  // 更新当前页码
      $.ajax({
        url: `${baseUrl}/api/record/getlist/`,
        data: {
            page,
        },
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          records.value = resp.records;
          total_records = resp.records_count;
          console.log(resp);
          update_pages();
        },
        error(resp) {
          console.log(resp);
        }
      })
    }

    //  调用
    pull_page(current_page);

    //  把字符串转化为2维数组
    const stringTo2D = map => {
      let g = [];
      for (let i = 0, k = 0; i < 13; i ++ ) {
        let line = [];
        for (let j = 0; j < 14; j ++, k ++) {
          if (map[k] === '0') line.push(0);
          else line.push(1);
        }
        g.push(line)
      }

      return g;
    }

    const open_record_content = recordId => {
      console.log("recordId: " + recordId);
      for (const record of records.value) {
        console.log("record.record.id: " + record.record.id);
        if (record.record.id === recordId) {
          //  找到了要播放的对局记录
          store.commit("updateIsRecord",true);
          store.commit("updateGame",{
            // 参数的形式最好还是回F12查看
            map: stringTo2D(record.record.map),
            a_id: record.record.aid,
            a_sx: record.record.asx,
            a_sy: record.record.asy,
            b_id: record.record.bid,
            b_sx: record.record.bsx,
            b_sy: record.record.bsy,
          });
          store.commit("updateSteps",{
            a_steps: record.record.asteps,
            b_steps: record.record.bsteps,
          });
          store.commit("updateRecordLoser",record.record.loser);
          //  页面跳转
          router.push({
            name: "record_content",
            params: {
              recordId,
            }
          })
          break;
          } 
        }
    }

    return {
      records,
      total_records,
      open_record_content,
      pages,
      click_page,
    }


  }

    
}


</script>  
  
<style scoped>  
/* 这里是你的组件样式 */ 
th {
  font-size: 1.5em;
  text-align: center;
}
img.record-user-photo {
  width: 3vw;
  height: 3vw;
  margin-right: 1vw;
  border-radius: 50%;

} 

span.record-user-username {
  font: italic 1.5em Georgia, serif;
}

@media (max-width: 932px) {
  * {
    font-size: 0.6rem;
  }
}
</style>
