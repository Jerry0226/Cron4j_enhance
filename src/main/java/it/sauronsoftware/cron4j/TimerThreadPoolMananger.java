package it.sauronsoftware.cron4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TimerThreadPoolMananger {
	
	private static TimerThreadPoolMananger ttpm = new TimerThreadPoolMananger();
	
	private TimerThreadPoolMananger() {
		
	}
	
	volatile boolean isRunning = false;
	
	public static TimerThreadPoolMananger getInstance() {
		synchronized (ttpm) {
			if (ttpm == null) {
				ttpm = new TimerThreadPoolMananger();
			}
			return ttpm;
		}
	}
	
	private List<Scheduler> listScheduler = new ArrayList<Scheduler>();
	
	private List<TimerThreadPool> timerThreadList = new ArrayList<TimerThreadPool>();
	
	/**
	 * 默认值
	 * */
	private int timerHandScheduleCount = 1;
	
	/**
	 * 最大的线程池服务
	 * */
	private final int MAZTIMECOUNT = 40;

	public int getTimerHandScheduleCount() {
		return timerHandScheduleCount;
	}

	public void setTimerHandScheduleCount(int timerHandScheduleCount) {
		if (timerHandScheduleCount < 1) {
			this.timerHandScheduleCount = 1;
		}
		else if (timerHandScheduleCount > MAZTIMECOUNT) {
			this.timerHandScheduleCount = MAZTIMECOUNT;
		}
		else {
			this.timerHandScheduleCount = timerHandScheduleCount;
		}
	}
	
	
	/**
	 * 启动服务
	 */
	public synchronized void start() {
	    
	    isRunning = true;
	        int schListSize = listScheduler.size();
	        if (schListSize <= 0) {
	            return;
	        }
	        
	        int count = schListSize/timerHandScheduleCount;
	        if (count <= 0) {
	            for (Scheduler scheduler : listScheduler) {
	                scheduler.start();
	                TimerThreadPool timer = new TimerThreadPool();
	                timer.addScheduler(scheduler);
	                timer.setDaemon(scheduler.isDaemon());
	                timer.start();
	                timerThreadList.add(timer);
	            }
	            
	        }
	        else {
	            for( int i = 0;i<timerHandScheduleCount;i++) {
	                TimerThreadPool timer = new TimerThreadPool();
	                for (int j=0;j < count ;j++) {
	                    Scheduler scheduler = listScheduler.get(j + count*i);
	                    scheduler.start();
	                    timer.addScheduler(scheduler);
	                    timer.setDaemon(scheduler.isDaemon());
	                }
	                timer.start();
	                timerThreadList.add(timer);
	            }
	            int lastTodoList = schListSize%timerHandScheduleCount;
	            if (lastTodoList != 0) {
	                TimerThreadPool timer = new TimerThreadPool();
	                for(int i = 0;i<lastTodoList;i++) {
	                    Scheduler scheduler = listScheduler.get(i + count*timerHandScheduleCount);
	                    scheduler.start();
	                    timer.addScheduler(scheduler);
	                    timer.setDaemon(scheduler.isDaemon());
	                }
	                timer.start();
	                timerThreadList.add(timer);
	            }
	        }
	}
	
	
	private TimerThreadPool getMinThreadPool() {
	    TimerThreadPool miniThreadPool = null ;
	    int miniSize = Integer.MAX_VALUE;
	    for (TimerThreadPool temp : timerThreadList) {
	        if (miniSize > temp.getSchedulerList().size()) {
	            miniSize = temp.getSchedulerList().size();
	            miniThreadPool = temp;
	        }
	    }
	    return miniThreadPool;
	}
	
	/**
	 * 停止所有的服务
	 */
	public void stop() {
		for(TimerThreadPool timer : timerThreadList) {
			ConcurrentLinkedQueue<Scheduler> listSch = timer.getSchedulerList();
			timer.interrupt();
			tillThreadDies(timer);
			timer = null;
			
			for (Scheduler scheduler : listSch) {
				scheduler.stop();
				scheduler = null;
			}
			
		}
		
		listScheduler.clear();
		timerThreadList.clear();
	}
	
	
	/**
	 * It waits until the given thread is dead. It is similar to
	 * {@link Thread#join()}, but this one avoids {@link InterruptedException}
	 * instances.
	 * 
	 * @param thread
	 *            The thread.
	 */
	private void tillThreadDies(Thread thread) {
		boolean dead = false;
		do {
			try {
				thread.join();
				dead = true;
			} catch (InterruptedException e) {
				;
			}
		} while (!dead);
	}
	
	/**
	 * 添加新的Scheduler了，如果线程服务已经启动，那么将选择TimerThreadPool 中包含最小服务的pool进行新增
	 * @param scheduler
	 */
	public synchronized void addScheduler(Scheduler scheduler) {
	    if (!isRunning) {
	        listScheduler.add(scheduler);
	    }
        else {
            TimerThreadPool miniThreadPool = getMinThreadPool();
            scheduler.start();
            miniThreadPool.addScheduler(scheduler);
        }
	    
	}
	
	
}
