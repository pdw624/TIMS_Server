package kr.tracom.platform.launcher.job;

import kr.tracom.platform.launcher.TLauncher;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestartJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public RestartJob() {

	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		logger.info("Restart application...");

		synchronized (TLauncher.restartEvent) {
			TLauncher.restartEvent.setSyncId(1);
			TLauncher.restartEvent.notify();
		}
	}
}
