<template>
  <NavBar/>
  <router-view/>
</template>
//写html




<script>
//写js
import NavBar from '@/components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

export default{
  components:{ 
    NavBar
  },

}


</script>



//写css
<style>
body{
  background-image: url("assets/images/Background1.jpg");
  background-attachment: fixed;
  background-size: cover;
}
</style>
