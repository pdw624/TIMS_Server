package kr.tracom.platform.launcher.job;

import kr.tracom.platform.launcher.manager.LicenseManager;
import kr.tracom.platform.launcher.module.TServiceModule;
import kr.tracom.platform.service.model.ServiceArgs;
import kr.tracom.platform.service.ServiceLauncher;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class LicenseJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public LicenseJob() {

	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		logger.info("License check...");

		Iterator<ServiceArgs> iterator = TServiceModule.getInstance().getIterator();
		ServiceArgs serviceArgs;
		while(iterator.hasNext()) {
			serviceArgs = iterator.next();

			if(!LicenseManager.licenseCheck(serviceArgs)) {
				serviceStop(serviceArgs.getId(), serviceArgs.getServiceLauncher());
			}
		}
	}

	private void serviceStop(String serviceId, ServiceLauncher serviceLauncher) {
		logger.info("License expired : " + serviceId);
		serviceLauncher.shutdown();
	}
}
