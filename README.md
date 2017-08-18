```html
cron4j 是一个java的简单易用的计划管理任务包，项目地址在：
http://www.sauronsoftware.it/projects/cron4j/manual.php

本项目是cron4j的增强版本，cron4j的计划任务管理是每个执行计划由一个线程来守护，当存在大量的计划任务时浪费大量的线程资源
	/**
	 * This method starts the scheduler. When the scheduled is started the
	 * supplied tasks are executed at the given moment.
	 * 
	 * @throws IllegalStateException
	 *             Thrown if this scheduler is already started.
	 */
	public void start() throws IllegalStateException {
		synchronized (lock) {
			if (started) {
				throw new IllegalStateException("Scheduler already started");
			}
			// Initializes required lists.
			launchers = new ArrayList();
			executors = new ArrayList();
			
			//注释掉此处的执行计划关联的守护线程
			// Starts the timer thread.
//			timer = new TimerThread(this);
//			timer.setDaemon(daemon);
//			timer.start();
			// Change the state of the scheduler.
			started = true;
		}
	}

对此进行优化处理，使用线程池来守护计划任务资源，减少因为大量计划任务而导致的线程资源的消耗
使用方式详见example包中的TestCron4jEnhance类
cron4j的增强版本，cron4j的计划任务管理是每个执行计划由一个线程来守护，当存在大量的计划任务时浪费大量的线程资源

```

```
package example;

import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.TimerThreadPoolMananger;
/**
 * Cron4jEnhance使用说明
 * @author chm
 *
 */
public class TestCron4jEnhance {
    
    public static void main(String[] args) throws InterruptedException {
        
        List<String> schList = new ArrayList<String>();
        schList.add("*/1 * * * 1-7");
        schList.add("*/1 * * * 1-7");
//        schList.add("1 9-20/5 * * 1-5");
//        schList.add("30 08-18/4 * * 1-7");
        TimerThreadPoolMananger ttpm = TimerThreadPoolMananger.getInstance();
        //设置计划任务的守护线程数
        ttpm.setTimerHandScheduleCount(2);
        for (String scheduler : schList) {
            Scheduler sch = new Scheduler();
            String uuid = sch.schedule(scheduler, new Runnable() {
                public void run() {
                   System.out.println(System.currentTimeMillis() +  "Scheduler exec!");
                }
            });
            
            System.out.println("Scheduler: " + uuid);
            //添加么个计划管理到任务管理池中
            ttpm.addScheduler(sch);
        }
        ttpm.start();
        
        Thread.sleep(2 * 60 * 1000);
        System.out.println("Test ------------");
        Scheduler sch = new Scheduler();
        String uuid = sch.schedule("*/2 * * * 1-7", new Runnable() {
            public void run() {
               System.out.println(System.currentTimeMillis() + " new Scheduler exec!");
            }
        });
        
        ttpm.addScheduler(sch);
        
        Thread.sleep(4 * 60 * 1000);
        
        sch.reschedule(uuid, "*/1 * * * 1-7");
        
    }
}

```
