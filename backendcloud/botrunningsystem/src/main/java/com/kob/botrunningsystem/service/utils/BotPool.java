package com.kob.botrunningsystem.service.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition  = lock.newCondition(); //  增加条件变量

    public void addBot(Integer userId, String bodCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, bodCode, input));

            //  唤醒线程
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //  队列存储BotCode
    private Queue<Bot> bots = new LinkedList<Bot>();

    //  更改语言就在这里
    private void consume(Bot bot) {
        Consumer consumer = new Consumer();

        //  最多等待2秒钟
        consumer.startTimeout(2000, bot);

    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    //  线程是空的，阻塞住
                    condition.await();  // 会自动把锁释放
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                // 取出队头
                Bot bot = bots.remove();
                lock.unlock();

                //  消耗当前任务
                consume(bot);   // 耗时间,放在解锁的后面
            }
        }
    }
}
