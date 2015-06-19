```html
<h2>Cron4j_enhance</h2>

<pre>
    <div class="container">
        <div class="block two first">
            <h2>cron4j-enhance</h2>
            <div class="wrap">
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

            </div>
        </div>
    </div>
</pre>
```

