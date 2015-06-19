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
    
    public static void main(String[] args) {
        
        List<String> schList = new ArrayList<String>();
        schList.add("50 14 * * 1-7");
        schList.add("30 15 * * 1-7");
        schList.add("1 9-20/5 * * 1-5");
        schList.add("30 08-18/4 * * 1-7");
        TimerThreadPoolMananger ttpm = TimerThreadPoolMananger.getInstance();
        //设置计划任务的守护线程数
        ttpm.setTimerHandScheduleCount(3);
        for (String scheduler : schList) {
            Scheduler sch = new Scheduler();
            sch.schedule(scheduler, new Runnable() {
                public void run() {
                   System.out.println("Scheduler exec!");
                }
            });
            //添加么个计划管理到任务管理池中
            ttpm.addScheduler(sch);
        }
        ttpm.start();
    }
}
