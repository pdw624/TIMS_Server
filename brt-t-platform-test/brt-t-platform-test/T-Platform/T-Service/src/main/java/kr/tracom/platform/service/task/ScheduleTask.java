package kr.tracom.platform.service.task;

/*
public abstract class ScheduleTask {	
	private ScheduledThreadPoolExecutor scheduler = null;	

	public ScheduleTask(int poolSize) {
		scheduler = new ScheduledThreadPoolExecutor(poolSize);
	}
	
	public abstract void init();	
	public abstract void execute();	

	public void start(int delaySeconds, int sleepSeconds) {
		scheduler.scheduleAtFixedRate(new Runnable() {
			public void startup() {
				execute();
			}
		}, delaySeconds, sleepSeconds, TimeUnit.SECONDS);
	}
	
	public void shutdown() {
		if(scheduler != null) {
			scheduler.shutdown();
			try {
				scheduler.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			scheduler.shutdownNow();
		}
	}
}
*/