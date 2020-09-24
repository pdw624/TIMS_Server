package kr.tracom.platform.launcher.job;

import kr.tracom.platform.service.manager.TransactionManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public TransactionJob() {

	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		TransactionManager.timeCheck();
	}
}
