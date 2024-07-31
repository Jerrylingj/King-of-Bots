import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();
        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;
        this.cells = [new Cell(info.r, info.c)];
        this.next_cell = null;

        this.speed = 5;
        this.direction = -1;        //-1表示没有指令，0、1、2、3表示上右下左
        this.status = "idle";       //idle表示静止,move表示正在移动,die表示死亡

        this.dr = [-1, 0, 1, 0];    //行偏移量
        this.dc = [0, 1, 0, -1];    //列偏移量

        this.step = 0;              //记录总步数
        this.eps = 1e-1;           //精度

        this.eye_dirction = 0;  //存眼睛方向
        if (this.id === 1) this.eye_dirction = 2;   // 左下角的蛇眼睛初始朝上,右上角的朝下

        this.eye_dx = [
            [-1, 1], // 上
            [1, 1],  // 右
            [1, -1], // 下
            [-1, -1] // 左
        ]
        this.eye_dy = [
            [-1, -1], // 上
            [1, -1],  // 右
            [1, 1],   // 下
            [-1, 1]   // 左
        ]
    }

    start() {
        // 初始化蛇的状态
    }

    set_direction(d) {
        this.direction = d;
    }

    check_tail_increasing() {   //检测当前回合蛇是否增加
        //增加逻辑：
        if (this.step <= 10) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }

    next_step() {   // 将蛇的装固态变为走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);  //存储下一个头的位置
        this.direction = -1;         // 清空状态                                                                      
        this.status = "move";        // 开始移动
        this.step++;                 // 更新步数
        this.eye_dirction = d;

        const k = this.cells.length;
        for (let i = k; i > 0; i--) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }

        if (!this.gamemap.check_valid(this.next_cell)) {  // 下一步操作撞了，蛇瞬间去世
            this.status = "die";
        }
    }

    update_move() {
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps) {
            this.cells[0] = this.next_cell; // 添加新蛇头
            this.next_cell = null;          // 清空下一个蛇头
            this.status = "idle";           // 走完了，停下来

            if (!this.check_tail_increasing()) {
                this.cells.pop();           // 不增加，则删除尾巴
            }
        } else {
            const move_distance = this.speed * this.timedelta / 1000;
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            //处理当不变长的情况
            if (!this.check_tail_increasing()) {
                const k = this.cells.length;
                const tail = this.cells[k - 1];
                const tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() {  //每一帧执行一次
        if (this.status === 'move') {
            this.update_move();
        }   //只有在状态为移动时才更新

        this.render();      //每一帧都要渲染
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;


        ctx.fillStyle = this.color;
        if (this.status === "die") {
            //死了,换颜色
            ctx.fillStyle = "white";
        }
        for (const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, 0.4 * L, 0, Math.PI * 2);
            ctx.fill();
        }

        //让蛇更丰满
        for (let i = 1; i < this.cells.length; i++) {
            const a = this.cells[i - 1], b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;//两个球重合了
            if (Math.abs(a.x - b.x) < this.eps) {
                //竖方向
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, 0.8 * L, Math.abs(a.y - b.y) * L);
            }
            else {
                //横方向
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, 0.8 * L);
            }
        }

        //画眼睛
        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i++) {
            ctx.beginPath();
            ctx.arc(this.cells[0].x * L + this.eye_dx[this.eye_dirction][i] * 0.2 * L, this.cells[0].y * L + this.eye_dy[this.eye_dirction][i] * 0.2 * L, 0.08 * L, 0, Math.PI * 2);
            ctx.fill();
        }

    }
}