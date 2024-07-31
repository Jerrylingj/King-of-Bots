<template>
    <div class="playground">
        <br><br><br><br>
        <GameMap></GameMap>
    </div>
</template>


<script>
import GameMap from './GameMap.vue'

export default {  
    name: 'PlayGround', // 可选的，用于调试和日志输出  
    components:{
        GameMap,
    }
  };  
</script>

<style scoped>

div.playground{
    width: 60vw;
    height: 70vh;

    margin: 3vh auto;
}

</style>