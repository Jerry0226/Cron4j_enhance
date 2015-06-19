package it.sauronsoftware.cron4j;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TimerThreadPool extends Thread {

	/**
	 * A GUID for this object.
	 */
	private String guid = GUIDGenerator.generate();

	/**
	 * The owner scheduler.
	 */
	private ConcurrentLinkedQueue<Scheduler> schedulerList = new ConcurrentLinkedQueue<Scheduler>();

	public ConcurrentLinkedQueue<Scheduler> getSchedulerList() {
		return schedulerList;
	}

	public void setSchedulerList(ConcurrentLinkedQueue<Scheduler> schedulerList) {
		this.schedulerList = schedulerList;
	}

	/**
	 * Builds the timer thread.
	 * 
	 * @param scheduler
	 *            The owner scheduler.
	 */
	public TimerThreadPool() {
		// Thread name.
		String name = "cron4j::timer[" + guid + "]";
		setName(name);
	}
	
	public void addScheduler(Scheduler scheduler) {
		schedulerList.add(scheduler);
	}

	/**
	 * Returns the GUID for this object.
	 * 
	 * @return The GUID for this object.
	 */
	public Object getGuid() {
		return guid;
	}

	/**
	 * It has been reported that the {@link Thread#sleep(long)} method sometimes
	 * exits before the requested time has passed. This one offers an
	 * alternative that sometimes could sleep a few millis more than requested,
	 * but never less.
	 * 
	 * @param millis
	 *            The length of time to sleep in milliseconds.
	 * @throws InterruptedException
	 *             If another thread has interrupted the current thread. The
	 *             <i>interrupted status</i> of the current thread is cleared
	 *             when this exception is thrown.
	 * @see Thread#sleep(long)
	 */
	private void safeSleep(long millis) throws InterruptedException {
		long done = 0;
		do {
			long before = System.currentTimeMillis();
			sleep(millis - done);
			long after = System.currentTimeMillis();
			done += (after - before);
		} while (done < millis);
	}

	/**
	 * Overrides {@link Thread#run()}.
	 */
	public void run() {
		// What time is it?
		long millis = System.currentTimeMillis();
		// Calculating next minute.
		long nextMinute = ((millis / 60000) + 1) * 60000;
		// Work until the scheduler is started.
		for (;;) {
			// Coffee break 'till next minute comes!
			long sleepTime = (nextMinute - System.currentTimeMillis());
			if (sleepTime > 0) {
				try {
					safeSleep(sleepTime);
				} catch (InterruptedException e) {
					// Must exit!
					break;
				}
			}
			// What time is it?
			millis = System.currentTimeMillis();
			// Launching the launching thread!
			for(Scheduler scheduler : schedulerList) {
				scheduler.spawnLauncher(millis);
			}
			
			// Calculating next minute.
			nextMinute = ((millis / 60000) + 1) * 60000;
		}
//		for(Scheduler scheduler1 : schedulerList) {
//			scheduler1 = null;
//		}
		// Discard scheduler reference.
		
	}

}
