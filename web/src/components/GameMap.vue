<template>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>

<script>
import { GameMap } from "@/assets/scripts/GameMap";
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'

export default {
    setup() {
        const store = useStore();
        let parent = ref(null);//创建对象，为了使用gamemap
        let canvas = ref(null);//创建对象,为了使用canvas
        
        //挂载玩游戏对象后，要执行的一些操作
        onMounted(()=>{
            store.commit(
                "updateGameObject",
                new GameMap(canvas.value.getContext('2d'),parent.value, store)
            );
        });

        return {
            parent,
            canvas
        }
    }
}

</script>


<style scoped>
div.gamemap{
    margin-top: 10vh;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;

}
</style>