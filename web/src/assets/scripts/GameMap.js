import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
import { Snake } from './Snake';

export class GameMap extends AcGameObject {
    constructor(ctx, parent, store) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;

        this.L = 0;
        this.rows = 13;
        this.cols = 14;

        this.inner_walls_count = 20; // 存储总共的障碍物数
        this.walls = [];

        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this),
        ];

        this.add_listening_events(); // 添加键盘事件监听
    }

    create_walls() {
        const g = this.store.state.pk.gamemap;

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }

        return true;
    }

    add_listening_events() {    //  监听用户操作
        //  先判断当前操作是要放录像还是要对战
        if (this.store.state.record.is_record) {
            //  放录像,每300毫秒走一步
            console.log("放录像");
            let k = 0;
            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.record.record_loser;
            const [snake0, snake1] = this.snakes;

            console.log("asteps: " + a_steps);
            console.log("bsteps: " + b_steps);
            const interval_id = setInterval(() => {
                if (k >= a_steps.length - 1) {
                    // 最后一步,死亡,
                    console.log("死亡")
                    if (loser === "all" || loser === "A") {
                        snake0.status = "die";
                    }
                    if (loser === "all" || loser === "B") {
                        snake1.status = "die";
                    }
                    //  更新状态
                    clearInterval(interval_id);
                } else {
                    console.log("a_dir: " + parseInt(a_steps[k]));
                    console.log("b_dir: " + parseInt(b_steps[k]));
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k++;
            }, 500);
        } else {
            this.ctx.canvas.focus();       // canvas获取焦点

            this.ctx.canvas.addEventListener("keydown", e => {
                //  存储移动方向
                let d = -1;

                // 监听键盘事件
                if (e.key === 'ArrowUp') d = 0;
                else if (e.key === 'ArrowRight') d = 1;
                else if (e.key === 'ArrowDown') d = 2;
                else if (e.key === 'ArrowLeft') d = 3;

                if (d >= 0) {
                    //  接收到了用户操作,向后端发送信息
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        direction: d,
                    }));
                }
            });
        }
    }

    start() {
        this.create_walls();
    }

    update_size() {
        this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows);
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() {
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }

    next_step() {
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell) {  // 检测目标位置是否合法：没有撞到两条蛇的身体和障碍物
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) {  // 当蛇尾会前进的时候，蛇尾不要判断
                k--;
            }
            for (let i = 0; i < k; i++) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }

        return true;
    }

    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();
    }

    render() {
        const color_odd = "#A2D149", color_even = "#AAD751";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                this.ctx.fillStyle = (r + c) % 2 === 0 ? color_even : color_odd;
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }

        for (const wall of this.walls) {
            wall.render();
        }

        for (const snake of this.snakes) {
            snake.update();
        }
    }
}