<template>
  <ContentField>
    <table class="table table-striped table-hover" style="margin-top: 2vh;">
              <thead>
                <tr style="padding: 1vh;">
                  <th>玩家</th>
                  <th>rating</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td style="text-align: center;">
                    <img :src="user.photo" class="user-photo">
                    <span class="user-username">{{ user.username }}</span>
                  </td>
                  <td style="text-align: center;font-size: 130%;">{{ user.rating }}</td>
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
export default {  
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    const baseUrl = store.state.baseUrl;
    let users = ref([]);
    let current_page = 1;
    let total_users = 0;
    let pages = ref([]);

    //  页面点击按钮
    const click_page = page => {
      if (page === -2) page = Math.max(current_page - 1, 1);
      else if (page === -1) {
        let max_pages = parseInt(Math.ceil(total_users / 10));
        page = Math.min(max_pages, current_page + 1);
      } 

      pull_page(page);
    }

    //更新页面
    const update_pages = () => {
      let max_pages = parseInt(Math.ceil(total_users / 10));
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
        url: `${baseUrl}/api/ranklist/getlist/`,
        data: {
            page,
        },
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          users.value = resp.users;
          total_users = resp.users_count;
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

    return {
      users,
      total_users,
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
img.user-photo {
  width: 3vw;
  height: 3vw;
  margin-right: 1vw;
  border-radius: 50%;

} 

span.user-username {
  font: italic 1.5em Georgia, serif;
}

</style>
