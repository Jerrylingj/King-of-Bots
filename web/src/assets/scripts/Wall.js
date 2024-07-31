import { AcGameObject } from "./AcGameObject";  
  
export class Wall extends AcGameObject {  
    constructor(r, c, gamemap) {  
        super(); // 调用基类构造函数  
  
        this.r = r;  
        this.c = c;  
        this.gamemap = gamemap;  
        this.color = "#B37226";
    }  
  
    update() {  
        this.render();  
    }  
  
    // 可能还需要实现 render 方法，因为你调用了它，但这里没有给出定义  
    render() {  
        const L = this.gamemap.L;
        const ctx= this.gamemap.ctx;

        //绘制墙
        ctx.fillStyle = this.color;
        ctx.fillRect(this.c*L,this.r*L,L,L);
    }  
}